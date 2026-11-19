# Local Peer Sync Protocol Draft

Status: design draft only. No discovery, pairing, authentication, or synchronization code is implemented in v0.1.

## Goals and boundaries

- Direct local-network exchange between the two phones when reachable; no required relay or hosted service.
- Each device retains a complete local copy and remains useful while peers are offline.
- Sync is replication, not backup. Backup and verified restore remain separate requirements.
- Preserve source identifiers and provenance. Conflicts are surfaced for resolution rather than silently discarded.

## Draft exchange

1. User explicitly opens pairing on both devices and confirms a short-lived pairing code or QR challenge.
2. Peers authenticate a device identity using a key agreement mechanism selected after a threat-model review.
3. Each peer exchanges protocol/schema versions and per-entity change cursors.
4. Peers request bounded batches of versioned changes and referenced attachment hashes.
5. Receiver validates identity, schema version, field bounds, UUIDs, timestamps, attachment size/type, and checksum before committing.
6. Conflicts are recorded with both versions and shown for user resolution. Retry is idempotent by change ID.

## Provisional envelope

`{ protocolVersion, senderDeviceId, changeId, sequenceNumber, entityType, entityId, operation, createdAt, payload, attachmentHashes }`

This is not a stable wire contract. Authentication, encryption, replay protection, deletion/tombstone retention, cursor recovery, and conflict semantics require a reviewed ADR before v1.2 implementation. LAN discovery and Android vendor behavior must be validated on the actual phones.
