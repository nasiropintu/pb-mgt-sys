# Architecture Baseline

## Runtime shape

- **Android (primary):** Kotlin/Jetpack Compose app. Each phone owns a complete local copy and remains usable offline. Room/SQLite is the durable store; media is kept in app-managed files with metadata and checksums.
- **Windows (optional worker):** Electron shell with React/TypeScript UI. It may process work explicitly transferred from a phone; it is not a required server or source of truth.
- **Shared contracts:** TypeScript contracts document interchange and desktop-side validation. Android equivalents are mapped explicitly; JSON wire fields use stable UUID strings and UTC ISO-8601 timestamps.
- **Database:** SQLite with forward-only numbered migrations. Store timestamps in UTC; render using the device timezone (initial default Asia/Dhaka). Monetary amounts use integer minor units.

## Data flow

Capture is written to local durable storage first. Processing jobs refer to source records and attachments. A worker may return validated suggestions, which remain pending human review. Sync is deferred to a later milestone and must not be treated as backup.

## Trust boundaries

- Android app-managed storage and the local database are the primary capture boundary.
- Imported files and future peer/worker responses are untrusted input: validate size, type, hashes, schema and identifiers.
- No network service or cloud account is required for core operation.
- Financial suggestions and other consequential actions require user confirmation.

## Phase boundaries

v0.1 establishes contracts, shells, migrations and build verification. v0.2 implements local capture and durability. AI processing and two-phone sync are later milestones; this baseline does not claim either is implemented.
