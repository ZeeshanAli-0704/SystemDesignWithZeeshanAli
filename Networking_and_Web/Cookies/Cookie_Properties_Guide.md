# 🍪 The Ultimate Guide to Cookie Properties in the Browser

Cookies are the backbone of **state management on the web**. They help websites remember who you are, what’s in your shopping cart, or what theme you prefer. But beyond the basics of `document.cookie`, cookies have **many properties** that control their **scope, security, and behavior across domains**.

This blog is a **deep dive into cookie properties** — from `Expires` to `SameSite` and **how cross-origin rules affect them**.

---

# 📑 Table of Contents

1. [Introduction](#introduction)
2. [Basic Structure of a Cookie](#basic-structure-of-a-cookie)
3. [Cookie Properties Explained](#cookie-properties-explained)

   * [Name=Value](#namevalue)
   * [Expires & Max-Age](#expires--max-age)
   * [Domain](#domain)
   * [Path](#path)
   * [Secure](#secure)
   * [HttpOnly](#httponly)
   * [SameSite](#samesite)
   * [Priority](#priority)
   * [Partitioned Cookies (CHIPS)](#partitioned-cookies-chips)
4. [Cookies and Cross-Origin Rules](#cookies-and-cross-origin-rules)

   * [First-Party vs Third-Party Cookies](#first-party-vs-third-party-cookies)
   * [Cross-Site Request Forgery (CSRF) & SameSite](#cross-site-request-forgery-csrf--samesite)
   * [Cross-Origin Resource Sharing (CORS) with Cookies](#cross-origin-resource-sharing-cors-with-cookies)
5. [Real-World Examples](#real-world-examples)
6. [Best Practices](#best-practices)
7. [Conclusion](#conclusion)

---

## 🔹 Introduction

A cookie is a small **key-value pair** stored in the browser. While the basics are simple (`name=value`), the true power of cookies lies in their **properties**. These properties control **lifetime, accessibility, domain scope, cross-site rules, and security**.

---

## 🔹 Basic Structure of a Cookie

Cookies are set using the `Set-Cookie` HTTP response header:

```http
Set-Cookie: sessionId=abc123; Expires=Wed, 13 Jan 2027 22:23:01 GMT; Path=/; Domain=example.com; Secure; HttpOnly; SameSite=Strict;
```

Here:

* `sessionId=abc123` → Name & value
* `Expires` → Expiration date
* `Path=/` → Cookie available across entire domain
* `Domain=example.com` → Scoped to this domain
* `Secure` → Sent only via HTTPS
* `HttpOnly` → Not accessible from JavaScript
* `SameSite=Strict` → Prevents cross-site usage

---

## 🔹 Cookie Properties Explained

### 1. **Name=Value**

* The fundamental unit of a cookie.
* Name must be unique within its scope (domain + path).

Example:

```http
Set-Cookie: username=Zeeshan
```

---

### 2. **Expires & Max-Age**

* **Expires**: A fixed date/time after which the cookie is invalid.
* **Max-Age**: Lifetime in seconds (takes precedence over Expires).

Examples:

```http
Set-Cookie: theme=dark; Expires=Wed, 01 Jan 2025 00:00:00 GMT
Set-Cookie: token=xyz; Max-Age=3600
```

⚡ Session cookies are created if neither `Expires` nor `Max-Age` is set (deleted when browser closes).

---

### 3. **Domain**

* Controls which **hosts** can receive the cookie.
* By default, cookies are scoped to the **exact domain** that set them.
* If specified, subdomains can also access it.

Example:

```http
Set-Cookie: userId=123; Domain=example.com
```

* Accessible to `www.example.com`, `shop.example.com`.
* Not accessible to `evil.com`.

❌ **Security risk**: Overly broad domain cookies (like `.com`) are rejected by browsers.

---

### 4. **Path**

* Restricts cookie to specific paths.

Example:

```http
Set-Cookie: cart=5; Path=/shop
```

* Sent with requests to `/shop` or `/shop/products`.
* Not sent to `/profile`.

---

### 5. **Secure**

* Cookie only sent over **HTTPS**.
* Prevents exposure on unencrypted connections.

```http
Set-Cookie: sessionToken=abc; Secure
```

---

### 6. **HttpOnly**

* Cookie is **inaccessible to JavaScript** (`document.cookie`).
* Helps prevent **XSS attacks** from stealing cookies.

```http
Set-Cookie: auth=abc; HttpOnly
```

---

### 7. **SameSite**

Controls whether cookies are sent with **cross-site requests**.

* `Strict`: Sent only for same-site navigation. Strongest CSRF protection.
* `Lax`: Sent for top-level navigations (safe by default).
* `None`: Sent for all requests, must be `Secure`.

Example:

```http
Set-Cookie: csrftoken=xyz; SameSite=Strict
```

⚠️ Modern browsers **default to `SameSite=Lax`** if not specified.

---

### 8. **Priority** (Chrome-specific)

Determines eviction order when hitting browser cookie limits:

* `Low`, `Medium`, `High`.

```http
Set-Cookie: pref=light; Priority=High
```

---

### 9. **Partitioned Cookies (CHIPS)**

A new spec: **Cookies Having Independent Partitioned State (CHIPS)**.

* Each third-party cookie is **isolated per top-level site**.
* Improves privacy by preventing tracking across sites.

```http
Set-Cookie: id=123; Secure; SameSite=None; Partitioned
```

---

## 🔹 Cookies and Cross-Origin Rules

### 1. **First-Party vs Third-Party Cookies**

* **First-party cookies**: Set by the site you are visiting.
* **Third-party cookies**: Set by external domains (e.g., ad networks).
* Browsers are phasing out **third-party cookies** due to privacy concerns.

---

### 2. **Cross-Site Request Forgery (CSRF) & SameSite**

* Without `SameSite`, attackers can force a victim’s browser to send cookies to your site via a malicious request.
* `SameSite=Strict` or `Lax` mitigates this.

---

### 3. **Cross-Origin Resource Sharing (CORS) with Cookies**

When making cross-origin AJAX requests:

* Server must allow credentials with `Access-Control-Allow-Credentials: true`.
* Browser must send cookies with `fetch(..., { credentials: "include" })`.

Example:

```js
fetch("https://api.example.com/data", {
  credentials: "include"
});
```

Server response must include:

```http
Access-Control-Allow-Origin: https://yourdomain.com
Access-Control-Allow-Credentials: true
```

---

## 🔹 Real-World Examples

1. **Authentication** → Secure + HttpOnly session tokens.
2. **E-commerce** → Shopping cart with `Path=/cart`.
3. **Multi-subdomain apps** → `Domain=example.com` for SSO.
4. **Analytics** → Third-party cookies (phasing out → CHIPS).

---

## 🔹 Best Practices

✅ Use `Secure` + `HttpOnly` for auth cookies.
✅ Use `SameSite=Lax` or `Strict` to mitigate CSRF.
✅ Avoid overly broad `Domain` values.
✅ Keep cookies small (<4KB).
✅ Prefer server-set cookies over client-side JS.
✅ For cross-origin APIs, configure **CORS properly**.

---

## 🔹 Conclusion

Cookies may seem simple, but **their properties define how they behave in terms of security, scope, and cross-origin interactions**. By mastering these attributes — especially `SameSite`, `Secure`, and `HttpOnly` — you can **secure your apps, prevent attacks, and ensure a smooth user experience**.

The future is moving towards **privacy-first cookies** (CHIPS, partitioned cookies), so understanding these fundamentals is more important than ever.

---



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli