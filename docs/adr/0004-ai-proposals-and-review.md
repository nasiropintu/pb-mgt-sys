# ADR 0004: AI output remains a proposal

- Status: Accepted for the product baseline
- Date: 2026-11-18

## Decision

Machine-generated extraction is stored separately from confirmed domain records. Every proposal retains a source link and status. Users can edit, approve, or reject it; money-related records require explicit confirmation.

## Context

Bengali, English, and mixed-language recognition can be uncertain, and date, amount, or project matching errors can have real consequences.

## Consequences

Schemas carry provenance and review status. Automation cannot silently commit consequential data. Evaluation quality and human correction rates are release evidence, not assumed properties.
