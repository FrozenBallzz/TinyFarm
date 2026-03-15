# Architecture

TinyFarm follows a layered architecture:

1. `controller`: exposes HTTP endpoints.
2. `service`: contains gameplay and business logic.
3. `repository`: reads and writes persistent data.
4. `entity`: defines the database model.

The initial scaffold includes one sample endpoint at `/api/farm`.
