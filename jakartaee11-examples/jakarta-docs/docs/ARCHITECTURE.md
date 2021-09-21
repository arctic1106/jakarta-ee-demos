# ARCHITECTURE.md — System Architecture

## 1. Purpose
This document describes the end‑to‑end architecture of the platform that delivers
version‑pinned, spec‑accurate Jakarta EE 11 and Open Liberty 26.0.0.5 configuration
intelligence. It covers ingestion, validation, AI reasoning, freshness tracking,
and delivery to end users.

The architecture is designed for:
- determinism
- zero hallucinations
- continuous freshness
- enterprise‑grade reliability
- Pro‑tier team collaboration

---

## 2. High‑Level Architecture Overview

The system consists of five major layers:

1. **Scraping Layer**
   Fetches and parses official documentation.

2. **Normalization Layer**
   Converts raw docs into structured, versioned JSON.

3. **Snapshot & Diff Engine**
   Detects changes between releases.

4. **AI Reasoning Layer**
   Generates, validates, and updates configuration snippets.

5. **Delivery Layer**
   Serves snippets, blueprints, and stacks to users.

---

## 3. Scraping Layer

### Responsibilities
- Fetch Open Liberty documentation:
  - Feature lists
  - server.xml reference
  - Subsystem guides
  - Release notes
- Fetch Jakarta EE 11 specification references
- Fetch MicroProfile documentation (if applicable)

### Output
Raw HTML and extracted text.

### Requirements
- Deterministic scraping
- Daily scheduled runs
- Version‑tagged storage

---

## 4. Normalization Layer

### Responsibilities
Convert scraped content into structured JSON:

- `liberty_26.0.0.5.json`
- `jakarta_ee_11.json`
- `microprofile_x.y.json` (optional)

### Extracted Fields
- Liberty features
- Feature dependencies
- Deprecated features
- XML namespaces
- Required configuration elements
- Behavioral notes from release notes

### Guarantees
- No guessing
- No inferred behavior
- Only documented facts

---

## 5. Snapshot Store

### Purpose
Store normalized JSON snapshots for comparison and historical analysis.

### Structure
snapshots/
liberty/
26.0.0.4.json
26.0.0.5.json
jakarta/
11.json


### Guarantees
- Immutable snapshots
- Full version history
- Reproducible diffs

---

## 6. Diff Engine

### Responsibilities
Compare snapshots to detect:

- Added features
- Removed features
- Deprecated items
- Changed defaults
- Changed configuration rules
- Updated subsystem behavior

### Output
A structured diff consumed by the Freshness Index and AI Reasoning Layer.

---

## 7. AI Reasoning Layer

### Responsibilities
- Generate version‑pinned configuration snippets
- Validate snippets against:
  - Jakarta EE 11 spec
  - Liberty 26.0.0.5 docs
  - Release notes
- Update stale snippets
- Produce verification checklists
- Flag ambiguous or undocumented behavior

### Constraints
- No invented APIs
- No invented features
- No invented namespaces
- Must state uncertainty when docs are unclear

### Inputs
- User prompts
- Context Pack
- Snapshot data
- Diff results

### Outputs
- server.xml wiring
- Jakarta configuration
- Architecture patterns
- Test snippets
- Freshness Index metadata

---

## 8. Freshness Index Engine

### Responsibilities
- Score each snippet (0–100)
- Detect staleness
- Trigger updates
- Provide freshness metadata to the UI

### Inputs
- Diff Engine output
- AI validation
- Human overrides (when needed)

### Outputs
freshness:
score: 94
last_checked: "2026-06-10"
issues:
- "MicroProfile Config default changed in 26.0.0.5"
status: "fresh"


---

## 9. Blueprint Engine (Pro Tier)

### Responsibilities
- Store private team blueprints
- Validate blueprints using the Freshness Index
- Generate update suggestions
- Provide team‑level dashboards

### Guarantees
- Zero data leakage
- Encrypted storage
- Team‑scoped isolation

---

## 10. Delivery Layer

### Components
- Public snippet search
- Pro‑tier blueprint manager
- Configuration stack builder
- Freshness dashboards
- API for CI/CD integration

### Responsibilities
- Serve validated, fresh snippets
- Provide version‑pinned wiring templates
- Expose freshness metadata
- Allow teams to save and share stacks

---

## 11. Security Architecture

### Principles
- No secrets sent to AI
- No production credentials in snippets
- Encrypted blueprint storage
- Strict tenant isolation
- Audit logs for Pro teams

---

## 12. Operational Model

### Scheduled Jobs
- Daily scraping
- Daily normalization
- Daily diffing
- Daily freshness scoring

### On‑Demand Jobs
- User‑requested snippet generation
- Pro‑tier blueprint validation
- Team stack updates

---

## 13. Future Extensions
- Multi‑version support (Jakarta EE 12, Liberty 27.x)
- Plugin system for enterprise vendors
- CI/CD freshness checks
- IDE integration (VS Code, IntelliJ)


