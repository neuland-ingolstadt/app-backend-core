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

## Modules

This is a multi-module Maven (hexagonal architecture) project:

- `adapters-inbound-rest` — JAX-RS REST resources
- `adapters-outbound-persistence` — persistence adapter
- `application` — inbound/outbound ports and use cases
- `bootstrap` — Quarkus application wiring all modules together
- `domain` — framework-free core entities/value objects

## Build & Run

The Maven Wrapper lives at the repo root and always builds the full reactor. Run all commands from here, not from inside a module folder.

Build and test everything:

```shell script
./mvnw verify
```

Run the bootstrap application in dev mode:

```shell script
./mvnw quarkus:dev -pl bootstrap
```

Package the runnable application:

```shell script
./mvnw package -pl bootstrap -am
```

The `-pl` flag targets the bootstrap module specifically, and `-am` ("also make") builds the modules it depends on first.
