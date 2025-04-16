# Security Policy

## Overview
This document outlines the security practices, guidelines, and policies for the mai-service monorepo, including mai-services, rag-service, and mai-app.

## Reporting Vulnerabilities
If you discover a security vulnerability, please report it by emailing the project maintainer or opening a private issue. Do **not** disclose vulnerabilities publicly until they have been addressed.

## Authentication
- All services use secure authentication mechanisms.
- mai-services uses JWT-based authentication with access and refresh tokens.
- Passwords are never stored in plain text.

## Data Protection
- Sensitive data is encrypted in transit (HTTPS enforced).
- User credentials and tokens are never logged or exposed in error messages.
- Environment variables (API keys, secrets) are stored in `.env` files and never committed to version control.

## Dependency Management
- Dependencies are regularly reviewed and updated to patch known vulnerabilities.
- Use trusted sources for all dependencies.

## Access Control
- Role-based access control (RBAC) is enforced in mai-services.
- Least privilege principle is applied to all users and services.

## Secure Coding Practices
- Input validation and sanitization are enforced throughout the codebase.
- Use prepared statements or ORM to prevent SQL injection.
- Cross-Site Scripting (XSS) and Cross-Site Request Forgery (CSRF) protections are implemented in mai-app.

## Auditing & Monitoring
- Security logs are maintained for authentication and authorization events.
- Regular audits are performed to ensure compliance with security policies.

_This document should be reviewed and updated regularly to reflect the latest security practices._
