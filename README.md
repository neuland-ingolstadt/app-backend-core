# app-backend-core

Quarkus Core API for **Neuland Backend 2.0** — auth, content, integrations, audit.

## Role

- JWT security via JWKS and role-based access
- Public REST API (app clients) and Admin REST API (dashboard)
- Content modules: Announcements, University Sports, Neuland Events, Room Reports
- Integrations: Campus Life API, Career Service
- Audit logging
- PostgreSQL persistence

## Related repos

- [app-backend-contracts](https://github.com/neuland-ingolstadt/app-backend-contracts) — OpenAPI specs
- [app-backend-food](https://github.com/neuland-ingolstadt/app-backend-food) — food data (called by Public API)
- [app-backend-cloud-gateway](https://github.com/neuland-ingolstadt/app-backend-cloud-gateway) — external entry point
- [neuland.app-backend](https://github.com/neuland-ingolstadt/neuland.app-backend) — legacy GraphQL backend (to be retired)
