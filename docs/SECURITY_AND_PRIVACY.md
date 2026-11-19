# Security and Privacy Baseline

This baseline is for the v0.1 foundation and does not claim that encryption or sync is already implemented.

- Core capture must work offline and must not require an account, paid API, or hosted database.
- Android system backup is disabled for the local capture database and audio files; a later backup feature must be explicit, encrypted, and user-controlled.
- Keep original captures and attachments in app-managed storage; validate imported type, size and checksum before use.
- Do not log capture contents, transcripts, credentials, keys, or financial details.
- Keep secrets out of source control and build metadata. Use platform-protected storage for any future device keys.
- Treat worker output and future peer data as untrusted; validate schema, identifiers, bounds, and provenance.
- AI output is a proposal. Require explicit user confirmation for financial records and consequential actions.
- Backup encryption, retention, deletion, restore and key-recovery behavior must be defined before a backup feature is relied on.
- Network sync is out of v0.1 scope. Pairing, authentication, replay defense, conflict handling, and threat model need an ADR before implementation.
- No telemetry is planned for the baseline. Any future diagnostics must be local-first and opt-in.
