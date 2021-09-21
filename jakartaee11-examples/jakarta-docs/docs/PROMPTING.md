# PROMPTING.md — Jakarta EE / Open Liberty Prompting Standards

## 1. Purpose
This document defines the prompting rules used across the platform to ensure that all AI‑generated configuration snippets, wiring templates, and architectural patterns are version‑pinned, spec‑accurate, production‑ready, and validated against the Freshness Index.

## 2. Core Principles
- Correctness over creativity
- Explicit versioning (Jakarta EE 11, Open Liberty 26.0.0.5)
- No invented APIs, annotations, or Liberty features
- Ask clarifying questions before generating output
- Always produce a verification checklist
- Always state uncertainty when documentation is ambiguous

## 3. Required Context Pack
Every prompt must include:
- Jakarta EE version: 11
- Open Liberty version: 26.0.0.5
- Runtime: containerized, cloud‑native
- Deployment: Kubernetes / OpenShift
- Output format: XML, YAML, or Java
- Constraints: spec compliance, security hardening, production‑ready defaults, no deprecated features
- User intent: configuration, wiring, architecture, or validation

## 4. Prompt Structure
### Step 1 — Clarifying Questions
The AI must ask all necessary questions before generating anything.
DOCS SOURCE: https://openliberty.io/docs/latest/overview.html

### Step 2 — Implementation Plan
A step‑by‑step plan describing:
- Jakarta APIs involved
- Liberty features required
- Configuration files to be produced
- Compatibility considerations

### Step 3 — Final Output
May include:
- server.xml wiring
- Jakarta configuration (CDI, JPA, JAX‑RS, Security)
- MicroProfile Config
- Kubernetes/OpenShift wiring
- Architecture patterns

### Step 4 — Test Snippet
Examples:
- Liberty feature validation
- Health endpoint checks
- Integration test scaffolding

### Step 5 — Verification Checklist
Checklist must confirm:
- Version alignment
- Spec compliance
- No deprecated features
- Correct Liberty feature set
- Correct namespaces
- Correct subsystem wiring

## 5. Prompt Types
### A. Configuration Prompts
Used for generating wiring snippets. Must specify:
- Exact feature names
- Exact namespaces
- Exact versions

### B. Architecture Prompts
Used for generating cloud‑native Jakarta EE patterns. Must include:
- Deployment model
- Scaling constraints
- Resilience requirements
- Observability stack

### C. Validation Prompts
Used for Freshness Index checks. Must:
- Compare against Jakarta EE 11 spec
- Compare against Liberty 26.0.0.5 release notes
- Detect deprecated features
- Flag incompatibilities

## 6. Pro Tier Prompting Rules
For Pro users:
- Prompts may reference private team blueprints
- AI must never leak private data
- AI must respect team‑scoped boundaries

## 7. Anti‑Hallucination Rules
The AI must never invent:
- Jakarta APIs
- Liberty features
- XML namespaces
- MicroProfile config keys
- Annotations
- Version numbers

If unsure, the AI must respond:
“This configuration is not documented in Jakarta EE 11 or Open Liberty 26.0.0.5. Please confirm the intended subsystem.”

## 8. Example Prompt Template
You are generating Jakarta EE 11 + Open Liberty 26.0.0.5 configuration.

Before generating anything:
1. Ask clarifying questions.
2. Wait for my answers.

After clarification, produce:
A. Implementation plan
B. Final configuration snippet
C. Test snippet
D. Verification checklist

Constraints:
- No invented APIs or features
- Must be version‑pinned
- Must follow spec‑compliant defaults
- Must be production‑ready

## 9. Contribution Guidelines
When adding new prompt templates:
- Include the prompt
- Include the AI output
- Include the Freshness Index result
- Include manual corrections
- Include validation test results
