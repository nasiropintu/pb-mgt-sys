# ADR 0002: Identifiers and time representation

- Status: Accepted for v0.1
- Date: 2026-11-18

## Decision

Use UUID strings for entity identifiers. Represent instants as ISO-8601 UTC timestamps; use `YYYY-MM-DD` for date-only values. Interpret and display user-entered local dates in the device timezone, initially Asia/Dhaka. Store money as integer minor units.

## Consequences

Interchange avoids machine-local timestamp ambiguity. Date-only reminders must not be shifted through UTC conversion. Cross-platform implementations must validate UUID/timestamp formats and integer currency bounds.
