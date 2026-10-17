
# NPCI & UPI — A Practical, End‑to‑End System Design

> A user‑friendly, production‑grade blueprint that maps your notes to how UPI actually works on NPCI rails—complete with flows (P2P, P2M, refunds, mandates), APIs, data models, security, SRE, and checklists you can take to an architecture review.

---
# Table of Contents
[0) What is NPCI and where UPI fits](#0-what-is-npci-and-where-upi-fits)
[1) Problem scope actors](#1-problem-scope-actors)
[2) High-level architecture (NPCI-style)](#2-highlevel-architecture-npci-style)
[3) Clean service boundaries](#3-clean-service-boundaries)
[4) Data storage design](#4-data-storage-design)
- [Relational (MySQL)](#relational-mysql)
- [NoSQL (event sourced)](#nosql-event-sourced)
- [Cache (Redis)](#cache-redis)


[5) Messaging (Kafka topics patterns)](#5-messaging-kafka-topics-patterns)
[6) Authentication, authorization, and gateway posture](#6-authentication-authorization-and-gateway-posture)
[7) Core UPI flows (step by step)](#7-core-upi-flows-step-by-step)
  

- [A) Registration & device binding](#a-registration--device-binding)
- [B) P2P/P2M Push (intent/pay)](#b-p2pp2m-push-intentpay--realtime)
- [C) Collect (Payer approves)](#c-collect-payer-approves)
- [D) QR flows](#d-qr-flows)
- [E) Refunds](#e-refunds)
- [F) Mandates (Autopay/e-mandate)](#f-mandates-autopaye-mandate)
- [G) Reconciliation reversals](#g-reconciliation-reversals)


[8) Transaction state machine](#8-transaction-state-machine)
[9) Security compliance (NPCI aligned)](#9-security-compliance-npci-aligned)
[10) Observability SLOs](#10-observability-slos)
[11) Scalability resilience patterns](#11-scalability-resilience-patterns)
[12) Developer experience CI/CD](#12-developer-experience-cicd)
[13) Public API sketches (mobile merchant)](#13-public-api-sketches-mobile-merchant)
[14) Risk controls rate limits (practical defaults)](#14-risk-controls-rate-limits-practical-defaults)
[15) Testing strategy](#15-testing-strategy)
[16) Operational playbooks (quick recipes)](#16-operational-playbooks-quick-recipes)
[17) Minimal DDL TTL cheatsheet](#17-minimal-ddl-ttl-cheatsheet)
[18) ASCII sequence diagrams](#18-ascii-sequence-diagrams)
[19) NPCI specific glossary (quick reference)](#19-npci-specific-glossary-quick-reference)
[20) Ready to build backlog (in order)](#20-ready-to-build-backlog-in-order)
[Final note](#final-note)

---

## 0) What is NPCI and where UPI fits

**NPCI (National Payments Corporation of India)** is the not‑for‑profit body that operates national retail payment systems: UPI, IMPS, AEPS, NETC, RuPay, etc. For UPI specifically, NPCI operates the **UPI switch** (also called the **Central Mapper** and **Clearing & Settlement** rails) that routes, authorizes, and settles interbank UPI transactions.

**Key responsibilities of NPCI for UPI**

* **Directory & addressing:** Manages VPA (like `alice@bank`) resolution to underlying bank accounts.
* **Interbank routing:** Orchestrates debit/credit between **Issuer** (payer’s bank) and **Acquirer**/**PSP** (payee’s side).
* **Authorization & responses:** Ensures messages adhere to UPI specs; returns result codes and **RRN** (reference number).
* **Clearing/Settlement:** Net settlement between banks via RBI; handles T+0/T+1 reports; supports reversals.
* **Rules & compliance:** Message standards, security baselines, dispute SLAs, fraud monitoring.

**Your role (PSP/TPAP)**: You build the apps and platform that front users/merchants and talk to NPCI/banks through certified connectors.

---
#### [🔼 Back to Top](#table-of-contents)
---


## 1) Problem scope actors

* **Users:** Payers/Payees using mobile/web apps.
* **Merchants:** Accept UPI on web/POS/app (P2M and collect).
* **PSP/TPAP:** Your platform presenting UPI to customers.
* **Banks:**

  * **Issuer bank:** Holds the payer’s account (debit side).
  * **Acquirer/beneficiary bank:** Holds payee’s account (credit side).
* **NPCI UPI Switch:** Interbank routing + clearing/settlement.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 2) High‑level architecture (NPCI‑style)

```
[ Mobile App ] ─┐                              ┌─ [ Merchant Web/App ]
[ Web App ]    ─┤                              └─ [ POS/QR ]
                 │
           [ CDN (static JS) ]
                 │
            [ API Gateway ]  ← WAF, rate limits, mTLS (merchant)
                 │
             [ AuthN/Z ]  ← OAuth 2.0/JWT, RBAC, device binding
                 │
           ┌──────────────┬──────────────────┬─────────────────────┐
           │              │                  │                     │
   [Transaction Svc] [Account Svc] [Fraud/Risk Svc]     [Notification Svc]
           │              │                  │                     │
           └──────┬───────┴──────┬───────────┴──────────┬──────────┘
                  │              │                      │
               [ Kafka ]     [ Redis Cache ]     [ Config Service ]
                  │              │                      │
           ┌──────┴─────┐  ┌────┴────┐            ┌────┴──────┐
           │  UPI/NPCI  │  │  MySQL   │            │   NoSQL   │
           │  Adapter   │  │ (users,  │            │ (tx, logs │
           │  (PSP API) │  │  accounts│            │  events)  │
           └──────┬─────┘  └────┬─────┘            └────┬──────┘
                  │             │                         │
          [ NPCI UPI Switch ]  │                   [ ELK / Grafana / Prom ]
                  │             │
       [ Issuer / Acquirer Banks ]
```

**Infra notes**: LB (NLB/ALB) → API Gateway; service discovery (K8s DNS); blue/green or canary; IaC with Terraform; secrets in Vault/KMS; multi‑AZ plus DR.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 3) Clean service boundaries

1. **Transaction Service**

* Orchestrates UPI flows: push-pay (P2P/P2M), collect, refunds, mandates.
* State machine + idempotency + retries + reconciliation hooks.
* Emits domain events to Kafka; durable outbox pattern.

2. **Account Service**

* VPA lifecycle, device binding, account linking.
* Balance inquiry, mandate metadata, UPI Lite wallets if applicable.

3. **UPI Adapter (Anti‑corruption layer)**

* Encapsulates NPCI/bank APIs, schemas, crypto, client certs, signing (HSM/KMS).
* Bank‑specific connectors behind a single interface; resilience/circuit breakers.

4. **Fraud/Risk Service**

* Velocity limits, device fingerprinting, rules + ML.
* Step‑up auth decisions (PIN/biometric/OTP) via policy.

5. **Notification Service**

* Push/SMS/email/WhatsApp, merchant webhooks.
* Outbox + dedup + exponential backoff; signed webhooks (JWS).

6. **Reconciliation & Settlement Service**

* Ingests NPCI/bank reports; auto‑reversal workflows; finance ops dashboards.

7. **Merchant Service**

* Onboarding/KYC, keys/webhooks, settlement configs, invoices, dispute portal.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 4) Data storage design

### Relational (MySQL)

* **users**(id, phone, email, kyc\_status, created\_at, …)
* **devices**(id, user\_id, device\_hash, bound\_at, risk\_score)
* **vpa**(id, user\_id, handle, is\_default, status, bank\_ref)
* **accounts**(id, user\_id, bank\_ifsc, account\_ref\_token, masked\_number, status)
* **mandates**(id, user\_id, umn, type, start\_date, end\_date, max\_amount, status)
* **merchants**(id, name, kyc\_tier, webhook\_url, settlement\_bank, status)
* **refunds**(id, tx\_id, rrn, amount, reason, status)
* **api\_clients**(id, client\_id, scopes, rate\_limit, jwk\_set)

> Index on user\_id, handle, rrn, merchant\_id; unique(handle), unique(rrn).

### NoSQL (event sourced)

**transactions** (snapshot) and **events** (append‑only), **audit\_logs** (immutable, PII‑safe).

```json
{
  "txId":"UUID",
  "type":"P2P|P2M|COLLECT|REFUND|MANDATE",
  "payer":{"vpa":"a@psp","deviceId":"..."},
  "payee":{"vpa":"b@bank","merchantId":"..."},
  "amount":5499,
  "currency":"INR",
  "state":"CREATED|PENDING|AUTHORIZED|SUCCESS|FAILED|REVERSED|TIMEOUT",
  "rrn":"NPCI_RRN",
  "npci":{"msgId":"...","respCode":"...","ts":"..."},
  "risk":{"score":0.12,"rules":["..."]},
  "ids":{"idempotencyKey":"...","merchantOrderId":"..."},
  "timestamps":{"created":"...","updated":"..."}
}
```

### Cache (Redis)

* `sess:<jti>` session; `tx:<txId>` hot state.
* `risk:vel:<userId>` counters; `idemp:<merchantId>:<key>` results for POST idempotency.
* `vpa:resolve:<handle>` short‑TTL VPA resolution.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 5) Messaging (Kafka topics patterns)

* Topics: `tx.created`, `tx.authorized`, `tx.success`, `tx.failed`, `tx.timeout`, `tx.refund.*`, `notify.*`, `recon.*`, `risk.*`.
* Pattern: **Outbox → Kafka** for exactly‑once semantics; consumer groups for notifications, analytics, recon, risk.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 6) Authentication, authorization, and gateway posture

* **OAuth 2.0 / JWT** for mobile & merchant APIs, short TTL access tokens; rotating refresh tokens.
* **mTLS** for merchant server‑to‑server calls; WAF in front of gateway; IP allowlists for NPCI/banks.
* **RBAC** roles: user, merchant\_admin, ops, recon\_analyst.
* **Rate limiting**: per client\_id + per VPA + per device (leaky bucket or token bucket).

---
#### [🔼 Back to Top](#table-of-contents)
---

## 7) Core UPI flows (step by step)

### A) Registration & device binding

1. App login (OTP/biometric/PIN).
2. Create/link VPA, fetch linked accounts (per bank SDK if required).
3. Bind device fingerprint to account/VPA; seed risk baseline.

### B) **P2P/P2M Push (intent/pay)** — real‑time

1. Client → **TransactionSvc** `/payments` (amount, payee VPA/QR, note, idempotencyKey).
2. Risk check; if needed, step‑up (PIN/biometric).
3. TxSvc → **UPI Adapter** → **NPCI** → **Issuer** (debit) → NPCI → **Acquirer** (credit).
4. Success → **RRN** returned; update state to `SUCCESS`; notify both parties.
5. Persist idempotent response keyed by idempotencyKey.

### C) **Collect (Payer approves)**

1. Merchant → MerchantSvc `/collect` creates a request.
2. TxSvc pushes approval notification to payer.
3. Payer approves (PIN/biometric) → process as push → webhook to merchant.

### D) **QR flows**

* **Static QR**: VPA embedded; user enters amount → push flow.
* **Dynamic QR**: includes amount & order → push flow with idempotency.

### E) **Refunds**

* Merchant → `/refunds` with original RRN/txId and amount ≤ original.
* Adapter calls NPCI refund; state `REFUND_PROCESSED` on success; notify.

### F) **Mandates (Autopay/e‑mandate)**

* Merchant creates **UMN** with schedule and cap; user approves once.
* Scheduler triggers debits on due dates; risk rules still apply; notify success/failure.

### G) **Reconciliation reversals**

* ReconSvc ingests NPCI/Bank reports (T+0/T+1).
* Mismatches → auto‑reversal or manual queue; dashboards for ops.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 8) Transaction state machine

```
CREATED → PENDING → AUTHORIZED → SUCCESS
     \→ FAILED
     \→ TIMEOUT (→ async reversal if mandated)
```

**Rules**: strict idempotency (RRN + bank tokens), bounded retries (exp backoff), timeouts with resume on callback or status query.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 9) Security compliance (NPCI aligned)

* **PIN/biometric** never leaves secure element; only verification result passes.
* **HSM/KMS** for signing keys, certs; encrypt data at rest (DB/NoSQL/Logs) and in transit (TLS 1.2+).
* **PII minimization**: store tokens/masks; avoid raw account numbers.
* **Secret management**: Vault/SSM; zero secrets in images.
* **Fraud controls**: device reputation, geo‑velocity, MCC tiers, high‑value step‑ups.
* **Audit**: immutable logs with correlation IDs; privacy redaction.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 10) Observability SLOs

* **Prometheus**: p95/p99 latencies by connector; error rates; queue lags.
* **Grafana**: business KPIs (success rate, step‑up rate, approval rate).
* **ELK**: structured logs; trace by `x-corr-id`.

**Targets**

* Auth p95 < 200 ms
* UPI pay p95 < 1.2 s (excluding user auth step)
* Availability ≥ 99.95% (multi‑AZ)
* Kafka durability: ISR≥3, cross‑AZ

---
#### [🔼 Back to Top](#table-of-contents)
---

## 11) Scalability resilience patterns

* **Stateless scale‑out** for services; DB read replicas; NoSQL sharded by `txId`.
* **Redis HA** (Cluster + AOF); TTLs: sessions 30m; idempotency 24–48h; risk windows.
* **Circuit breakers** per bank connector; adaptive rate control by queue depth.
* **Multi‑AZ + DR** (RPO ≤ 15m, RTO ≤ 1h); chaos drills for NPCI/bank/Redis outages.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 12) Developer experience CI/CD

* **Terraform** for infra; per‑env workspaces.
* **Central config** (Spring Cloud Config/Consul) + Vault‑managed secrets.
* **Pipelines**: static analysis, unit/contract tests, ephemeral envs, canaries.
* **Feature flags** to roll new bank connectors safely.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 13) Public API sketches (mobile merchant)

### `POST /v1/payments`

```json
{
  "idempotencyKey":"c7e1-...",
  "payer":{"vpa":"alice@psp","deviceId":"..."},
  "payee":{"vpa":"bob@bank"},
  "amount":5499,
  "note":"Lunch",
  "auth":{"type":"PIN","proof":"<opaque>"}
}
```

**201**

```json
{ "txId":"...","state":"SUCCESS","rrn":"2345...","ts":"..." }
```

### `POST /v1/collect`

```json
{ "merchantOrderId":"ORD-123","payeeVpa":"shop@psp","payerVpa":"alice@psp","amount":9999,"note":"Checkout" }
```

### `GET /v1/transactions/{txId}`

Returns current state (poll‑safe).

### `POST /v1/refunds`

```json
{ "originalTxId":"...","amount":5000,"reason":"PARTIAL_REFUND" }
```

### Merchant webhooks

* `/v1/webhooks/tx` with JWS signature headers; retries with exponential backoff.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 14) Risk controls rate limits (practical defaults)

* Per user/device/VPA velocity buckets in Redis (1m/1h/1d).
* Hard caps per mandate & per merchant category (MCC).
* ML features: time‑of‑day, device age, shared‑counterparty graph, dispute feedback.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 15) Testing strategy

* **Contract tests** for NPCI/bank adapters (pacts, golden payloads).
* **Simulation harness**: NPCI/bank sandboxes with randomized latency/faults.
* **Replay**: run historical tx events through new risk models.
* **Game days**: circuit‑breaking banks; rebalancing Kafka; Redis failover.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 16) Operational playbooks (quick recipes)

* **Stuck PENDING (>5m):** trigger status query →

  * no debit → mark FAILED
  * debit/no credit → initiate REVERSAL per NPCI SOP
* **Bank connector down:** open circuit; queue requests; mark status=DEGRADED; notify merchants; autoswitch if alternate rails exist.
* **Fraud spike:** tighten velocity; force step‑up for risky cohorts; short‑TTL blacklists; inform merchants.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 17) Minimal DDL TTL cheatsheet

**MySQL**

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY,
  phone VARCHAR(15) UNIQUE,
  email VARCHAR(255),
  kyc_status ENUM('PENDING','VERIFIED','REJECTED'),
  created_at DATETIME, updated_at DATETIME
);

CREATE TABLE vpa (
  id BIGINT PRIMARY KEY,
  user_id BIGINT,
  handle VARCHAR(255) UNIQUE,
  status ENUM('ACTIVE','BLOCKED'),
  bank_ref VARCHAR(255),
  INDEX (user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE refunds (
  id BIGINT PRIMARY KEY,
  tx_id CHAR(36),
  rrn VARCHAR(30),
  amount BIGINT,
  status ENUM('REQUESTED','PROCESSED','FAILED'),
  created_at DATETIME,
  UNIQUE(rrn)
);
```

**Redis TTLs**

* `sess:*` 30 min; `idemp:*` 24–48 h; `tx:*` terminal+1 h; `risk:vel:*` per window.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 18) ASCII sequence diagrams

**P2P Push (Intent/Pay)**

```
App → TxSvc: POST /payments (payer, payee, amount, idemp)
TxSvc → Risk: evaluate(device, velocity)
Risk → TxSvc: allow / step-up
TxSvc → UPI Adapter: create pay request
UPI Adapter → NPCI: debit→credit route
NPCI → Issuer: debit
NPCI → Acquirer: credit
NPCI → UPI Adapter: RRN + result
UPI Adapter → TxSvc: success
TxSvc → Kafka: tx.success
TxSvc → Notify: push/SMS both parties
```

**Collect (Payer Approves)**

```
Merchant → MerchantSvc: POST /collect
MerchantSvc → TxSvc: create collect
TxSvc → Notify: push approval to payer
App → TxSvc: approve (PIN)
... then same as push flow ...
TxSvc → Merchant webhook: signed receipt (RRN)
```

**Refund**

```
Merchant → MerchantSvc: POST /refunds (orig RRN)
TxSvc → UPI Adapter → NPCI: refund
NPCI → UPI Adapter: result + RRN
TxSvc: state=REFUND_PROCESSED → notify → webhook
```

**Mandate (Autopay)**

```
Merchant → MerchantSvc: create UMN
App → TxSvc: one-time approval (PIN)
Scheduler → TxSvc (due date): trigger debit
... risk checks ...
TxSvc → UPI Adapter → NPCI: process
→ notify + webhook; failures to retry/notify
```

---

## 19) NPCI specific glossary (quick reference)

* **VPA (Virtual Payment Address):** `user@handle` verbal address for bank account.
* **RRN:** unique reference number returned post authorisation.
* **UMN:** Unique Mandate Number for autopay mandates.
* **Issuer vs Acquirer:** debit bank vs credit bank.
* **Collect:** pull request that payer authorizes.
* **Reversal:** system‑initiated credit back on failures/mismatches.

---
#### [🔼 Back to Top](#table-of-contents)
---

## 20) Ready to build backlog (in order)

1. Contract stubs for UPI Adapter (NPCI + 2 pilot banks).
2. Tx state machine + idempotent APIs + outbox→Kafka.
3. Redis‑backed risk MVP (velocity + rules YAML).
4. Merchant webhooks (JWS) + portal for keys.
5. Recon file parser + daily dashboards.
6. Mobile flows: pay, collect approve, refunds, mandates.

---
#### [🔼 Back to Top](#table-of-contents)
---

### Final note

This design maps cleanly to an NPCI‑compliant UPI platform: clear service boundaries, resilient flows, strong risk posture, and ops playbooks. If you’d like, we can turn this into a printable PDF, add more detailed sequence diagrams, or generate Spring Boot/Kafka/Redis starter code to bootstrap the services.





More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli