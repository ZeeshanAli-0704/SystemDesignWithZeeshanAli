# System Design: How to Avoid Single Points of Failure (SPOFs)

- [Executive Summary](#executive-summary)
- [Understanding SPOFs](#understanding-spofs)
- [Core Theory You Should Know](#core-theory-you-should-know)
- [How to Identify SPOFs in a Distributed System](#how-to-identify-spofs-in-a-distributed-system)
- [Strategies to Avoid Single Points of Failure](#strategies-to-avoid-single-points-of-failure)
  - [Redundancy](#redundancy)
  - [Load Balancing](#load-balancing)
  - [Data Replication](#data-replication)
  - [Geographic Distribution](#geographic-distribution)
  - [Graceful Handling of Failures](#graceful-handling-of-failures)
  - [Monitoring and Alerting](#monitoring-and-alerting)
- [How DNS Helps Avoid SPOFs](#how-dns-helps-avoid-spofs)
- [Stateless vs Stateful Components](#stateless-vs-stateful-components)
- [Bulkheads and Resiliency Patterns](#bulkheads-and-resiliency-patterns)
- [Hidden and Often Overlooked SPOFs](#hidden-and-often-overlooked-spofs)
- [Testing Your HA Design](#testing-your-ha-design)
- [Operational Playbook and Governance](#operational-playbook-and-governance)
- [Cloud Specific Guidance](#cloud-specific-guidance)
- [Practical Checklist](#practical-checklist)
- [Reference Architecture for a Real System](#reference-architecture-for-a-real-system)
- [Conclusion](#conclusion)

## Executive Summary
- A SPOF (Single Point of Failure) is one part of the system that can break everything if it fails.
- In real systems, failures will happen. The goal is to make failures small, not system-wide.
- Reliable systems use three layers together:
  - Design layer: redundancy, isolation, no single critical dependency.
  - Runtime layer: health checks, automatic failover, graceful degradation.
  - Operations layer: observability, runbooks, drills, and ownership.

## Understanding SPOFs
- Simple definition: if one thing fails and your service stops, that thing is a SPOF.
- Easy example: one bridge to a city. If the bridge closes, no one can enter.
- Common SPOFs:
  - One app server
  - One database instance
  - One cache node
  - One load balancer
  - One DNS/provider account
  - One CI/CD runner
- Important point: many SPOFs are hidden in control-plane systems (secrets, config, CI, DNS), not only in app servers.

## Core Theory You Should Know
- Failure domains:
  - Process, host, rack, availability zone, region, provider.
  - Good design avoids putting all critical components in one domain.
- SLO, RTO, RPO:
  - SLO defines target reliability (example: 99.95% uptime).
  - RTO is how fast service must recover after failure.
  - RPO is how much data loss is acceptable.
- CAP and consistency trade-off:
  - During network partitions, distributed systems choose trade-offs between consistency and availability.
  - Strong consistency usually increases latency; eventual consistency improves availability and speed.
- Blast radius:
  - Design so one component failure affects only a small area.
  - This is the practical meaning of resilience.

## How to Identify SPOFs in a Distributed System
- Draw your dependency map.
  - Include app services, database, cache, queue, DNS, secrets, CI/CD, and logging.
- Ask one question for each component.
  - If this goes down, what stops?
- Check failure levels.
  - Process, VM/container, host, rack, AZ, region.
- Validate by testing.
  - Kill one dependency and verify if users are still served.
- Practical method used by strong teams:
  - Step 1: list top 10 user journeys (login, checkout, payment, search).
  - Step 2: map all dependencies for each journey.
  - Step 3: mark each dependency as single, redundant, or unknown.
  - Step 4: fix unknown first, then remove top-risk single dependencies.

## Strategies to Avoid Single Points of Failure

### Redundancy
- Run more than one instance of critical components.
- Spread instances across different AZs.
- Keep spare capacity so traffic can shift during failures.
- For people/processes, avoid one-person ownership. Use shared runbooks and rotation.
- Practical implementation:
  - Minimum baseline for production: N+1 capacity and multi-AZ deployment.
  - Keep independent autoscaling groups so one failure does not remove all capacity.
- Real example:
  - E-commerce checkout service runs 8 pods across 3 AZs.
  - If one AZ fails, remaining AZs continue serving with reduced but acceptable performance.

### Load Balancing
- Put multiple app instances behind a load balancer.
- Use health checks so bad instances are removed quickly.
- Use connection draining so old instances finish requests safely during deploys.
- Keep load balancers highly available too (managed multi-AZ is easiest in cloud).
- Practical implementation:
  - Use two layers:
    - Global traffic manager for region selection.
    - Regional load balancer for pod or VM routing.
  - Tune health check interval and timeout to avoid slow detection.
- Real example:
  - A video platform routes users to nearest healthy region.
  - If region A degrades, DNS or global traffic manager shifts new sessions to region B.

### Data Replication
- Keep multiple copies of important data.
- Choose replication mode by business need:
  - Synchronous: safer consistency, usually more latency.
  - Asynchronous: faster writes, but can lose latest data on failover.
- Quorum and failover:
  - What: Quorum is the minimum majority needed to accept writes or leader decisions.
  - Why: It prevents split-brain and keeps data consistent during network failures.
- Practical implementation:
  - Use odd-number voting groups (3 or 5 nodes).
  - Track replication lag and block failover if lag exceeds business threshold.
  - Test restore monthly, not only failover.
- Real example:
  - Payment ledger database uses primary plus replicas across AZs.
  - Primary failure triggers automatic leader election, while idempotency keys stop duplicate charges.

### Geographic Distribution
- Multi-AZ is the normal baseline for production.
- Multi-region is for higher resilience and global users, but adds complexity and cost.
- Decide based on business impact, compliance, and latency needs.
- Practical implementation:
  - Start with active-active across AZs in one region.
  - Add second region only when RTO, compliance, or customer distribution requires it.
  - Keep data residency and legal boundaries in design documents.
- Real example:
  - A bank keeps primary operations in one region and warm standby in another region for disaster recovery.

### Graceful Handling of Failures
- Set timeouts for every remote call.
- Retry carefully (with backoff + jitter), not forever.
- Use circuit breakers to stop cascading failures.
- Use fallbacks (cached data, partial response) when possible.
- Practical implementation:
  - Define per-dependency timeout budgets (for example 200ms cache, 800ms payment gateway).
  - Use idempotency tokens for retried write operations.
  - Return partial experience rather than total error whenever safe.
- Real example:
  - During recommendation-service outage, home page still loads with cached popular items.

### Monitoring and Alerting
- Track the basics: latency, traffic, errors, and resource usage.
- Alert for user-impact, not every small internal event.
- Keep clear runbooks linked from alerts.
- Practical implementation:
  - Alert on SLO burn rate, not only CPU and memory.
  - Add synthetic probes from multiple regions for login, search, and checkout.
  - Define one owner per critical alert.
- Real example:
  - If checkout success rate drops below threshold for 5 minutes, on-call is paged with direct runbook steps.

## How DNS Helps Avoid SPOFs
- DNS translates names (like www.example.com) to IP addresses.
- DNS can return multiple IPs for one name.
- If one IP path fails, clients can try another.

- DNS request flow (simple):
  - Your app or browser asks for www.example.com.
  - A resolver asks authoritative DNS.
  - DNS may return aliases (CNAMEs), then final A/AAAA records.
  - Final answer can contain multiple IPs across AZs.

- Why this helps:
  - No single fixed endpoint.
  - Traffic can move to healthy targets.

- Caveat:
  - Failover speed depends on TTL, resolver cache, and health checks.
- Practical implementation:
  - Keep TTL moderate (for example 30 to 120 seconds for critical endpoints).
  - Use health-checked DNS records and test failover from different ISPs.
- Real example:
  - SaaS API exposes api.company.com with multiple regional backends.
  - DNS removes unhealthy endpoint and clients reconnect to healthy region.

## Stateless vs Stateful Components
- Stateless services:
  - Do not store session/data locally.
  - Easy to scale and replace.
- Stateful services:
  - Store durable data.
  - Need replication, leader election/failover, and backup/restore plans.
- Practical implementation:
  - Keep auth/session state in external store (Redis or database), not inside app memory.
  - Define startup recovery runbooks for stateful services.

## Bulkheads and Resiliency Patterns
- Bulkhead idea: isolate failures so one bad dependency does not break everything.
- Practical methods:
  - Separate thread/connection pools per dependency.
  - Use rate limits per tenant/dependency.
  - Apply CPU/memory limits per service.
- Real example:
  - If email provider is slow, only email worker pool degrades.
  - Checkout API thread pool remains healthy, so purchases continue.

## Hidden and Often Overlooked SPOFs
- Single secrets manager path
- Single identity provider dependency
- Single artifact registry
- Single observability pipeline
- Single NAT/gateway route
- One key person with undocumented steps
- Practical tip:
  - Run quarterly dependency audit for platform services, not only business services.

## Testing Your HA Design
- Run game days regularly.
- Test: instance crash, DNS issue, network partition, disk full.
- Practice backups and restore.
- Measure and track RTO (recovery time) and RPO (data loss window).
- Practical implementation:
  - Convert every major incident into a test case.
  - Run one planned failure scenario each sprint.
  - Record measured RTO and RPO values in a reliability dashboard.

## Operational Playbook and Governance
- Keep runbooks short and actionable.
- Use safe deployment methods (canary/blue-green).
- Define incident severity and ownership clearly.
- Match HA level with business value and budget.
- Practical implementation:
  - Add readiness checklist before production release.
  - Require rollback plan in every high-risk change request.
  - Keep post-incident actions tracked to closure.

## Cloud Specific Guidance
- Distribute across AZs by default.
- Use managed services with built-in failover where possible.
- Validate storage/database replication SLAs.
- Use multi-region only when needed.
- Practical implementation:
  - Avoid single NAT, single bastion, and single ingress gateway designs.
  - Verify cloud quota limits before incidents, not during incidents.

## Practical Checklist
- Architecture
  - [ ] No critical service runs as a single instance
  - [ ] Instances are spread across AZs
  - [ ] Stateful stores have tested failover
  - [ ] Load balancing and health checks are enabled
- Resiliency
  - [ ] Timeouts, retries, and circuit breakers are implemented
  - [ ] Fallback behavior exists for key user flows
- Operations
  - [ ] Monitoring and alerting focus on user impact
  - [ ] Backup + restore drills are done regularly
  - [ ] Runbooks are current and owned
- Governance
  - [ ] No single person/process is a bottleneck
  - [ ] HA decisions are aligned with business goals

## Reference Architecture for a Real System
- Example: large e-commerce platform
  - Edge: DNS plus CDN plus Web Application Firewall.
  - Traffic: global traffic manager to nearest healthy region.
  - Region: regional load balancer to stateless app services across AZs.
  - Data: primary database with replicas, cache cluster, message queue.
  - Platform: redundant secrets manager path, observability pipeline, CI/CD runners.
- How this handles failures:
  - One app node fails: health check removes it, traffic shifts automatically.
  - One AZ fails: load balancer routes to healthy AZs.
  - Database primary fails: automated failover promotes replica.
  - External payment dependency fails: circuit breaker opens and retry queue stores requests safely.
  - Region fails: traffic manager sends new requests to secondary region based on policy.
- Why this is realistic:
  - It balances cost and reliability.
  - It uses common managed cloud services.
  - It supports gradual improvement from single-region to multi-region.

## Conclusion
- Avoiding SPOFs is not about adding random extra servers.
- It is about designing alternative paths, isolating failures, and proving recovery through practice.
- Real systems should treat resilience as an engineering product with architecture, runtime controls, and operational discipline.
- Start simple: remove top SPOFs in critical user journeys, then improve step by step with measurable reliability goals.

## More Details

Get all articles related to system design.

Hashtag: #SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

GitHub: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
