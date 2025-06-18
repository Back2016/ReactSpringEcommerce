# E-Commerce Backend

This is the backend of a full-stack e-commerce application built with **Spring Boot**. It provides RESTful APIs for user authentication, product browsing, cart and order management, address book functionality, and Stripe-based payments. It features robust **JWT authentication** with access and refresh tokens, admin capabilities, and full support for **guest checkout**.

---

## Tech Stack

- Spring Boot 3
- Spring Security + JWT (Access & Refresh token strategy)
- Spring Data JPA
- MySQL
- Stripe (Payment Intents API)
- ModelMapper (for entity–DTO mapping)
- Lombok (for boilerplate reduction)
- Javax Validation + Spring `@Valid`
- Docker-ready

---

## Authentication & Authorization

- Secure login system using Spring Security
- JWT-based access token for authentication
- Refresh token endpoint to renew access tokens
- Role-based access:
  - User: can browse, place orders, manage addresses
  - Admin: can manage products and orders

---

## Stripe Integration

- Uses Stripe Payment Intents API
- Supports:
  - Creating client secret for secure frontend payments
  - Linking Stripe payments with internal order system
- Seamlessly integrates with both user and guest checkout flows

---

## Core Features

| Feature             | Description |
|---------------------|-------------|
| User Authentication | Register, login, refresh token |
| Product Catalog     | Search, browse, and filter products |
| Product Name Search | Supports partial and case-insensitive name-based product search  |
| Shopping Cart       | Guest and user cart merge on login |
| Order Placement     | Handles both user and guest orders |
| Address Book        | CRUD for user addresses |
| Guest Checkout      | Checkout without requiring registration |
| Stripe Payments     | Payment Intent workflow for secure payments |
| Role-Based Access   | Admin endpoints protected by role |
| DTO Mapping         | Entity-to-DTO conversion using ModelMapper |

---

## Guest Checkout Support

The backend fully supports guest users placing orders without creating an account. A guest can:
- Add items to cart
- Provide shipping and billing information at checkout
- Receive a unique order number for post-checkout order lookup

---

## Validation and Error Handling

- All input is validated using `@Valid` and DTOs
- Errors are handled with centralized `@ControllerAdvice` logic
- Returns structured error responses for invalid input or unauthorized access

---

## Docker Setup

To build and run locally via Docker:

```bash
docker build -t ecommerce-backend .
docker run -p 8080:8080 ecommerce-backend
