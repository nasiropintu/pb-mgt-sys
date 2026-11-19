# ADR 0001: Platform boundaries

- Status: Accepted for v0.1
- Date: 2026-11-18

## Decision

Use Kotlin/Jetpack Compose for Android capture and daily organization, and Electron with React/TypeScript for an optional Windows worker shell. Android remains independently useful; Windows is not a required server.

## Context

The target phones are low-memory Android devices and the PC is optional. Core capture must work when the PC and internet are unavailable.

## Consequences

Domain and interchange contracts must remain platform-neutral. UI and persistence implementations are platform-specific. The Windows worker cannot own the only copy of user data.
