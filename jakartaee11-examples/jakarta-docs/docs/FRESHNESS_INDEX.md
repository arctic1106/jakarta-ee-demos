# FRESHNESS_INDEX.md — Specification & Validation System

## 1. Purpose
The Freshness Index is the platform’s automated system for ensuring that all Jakarta EE 11 and Open Liberty 26.0.0.5 configuration snippets, wiring templates, and architectural patterns remain accurate, up‑to‑date, and aligned with official specifications and release notes.

It prevents stale, deprecated, or incompatible configurations from being shown to users.

---

## 2. What the Freshness Index Measures
Each configuration snippet is evaluated across five dimensions:

### 1. Specification Alignment
- Matches Jakarta EE 11 APIs
- Uses correct namespaces
- Uses correct annotations
- Avoids removed or deprecated APIs

### 2. Liberty Feature Compatibility
- Uses valid Liberty 26.0.0.5 features
- No deprecated features
- No removed features
- Correct feature combinations

### 3. Release Note Compliance
- Matches the latest Open Liberty release notes
- Flags changes in defaults, behavior, or subsystem wiring

### 4. Configuration Stability
- Detects breaking changes
- Detects incompatible feature sets
- Detects changes in required dependencies

### 5. Documentation Sync
- Confirms alignment with:
  - Liberty docs
  - Jakarta EE 11 specification
  - MicroProfile references (if applicable)

Each dimension contributes to the final Freshness Score.

---

## 3. Freshness Score
Each snippet receives a score from **0 to 100**, calculated as:

- 100 = fully aligned, no issues
- 80–99 = minor updates available
- 50–79 = partially outdated
- 0–49 = invalid or incompatible

Snippets below 80 are flagged for review.

---

## 4. Freshness Workflow

### Step 1 — Scrape
The system fetches:
- Open Liberty docs
- Liberty feature lists
- Liberty release notes
- Jakarta EE 11 specification references

### Step 2 — Normalize
Extracted data is converted into structured JSON:
- `liberty_26.0.0.5.json`
- `jakarta_ee_11.json`

### Step 3 — Snapshot
Snapshots are stored and versioned for diffing.

### Step 4 — Diff Engine
The diff engine detects:
- Added features
- Removed features
- Deprecated items
- Changed defaults
- Updated configuration rules

### Step 5 — AI Reasoning Layer
The AI evaluates:
- Whether snippets remain valid
- Whether wiring patterns need updates
- Whether new features should replace old ones

### Step 6 — Human Review
Editors approve or reject updates.

### Step 7 — Publish
Updated snippets are marked as “Fresh” and made available to users.

---

## 5. Freshness Rules

### Rule 1 — No Deprecated Features
Any snippet using deprecated Liberty features is automatically marked stale.

### Rule 2 — No Removed APIs
Any snippet referencing removed Jakarta EE APIs receives a failing score.

### Rule 3 — Namespace Accuracy
All XML and annotation namespaces must match Jakarta EE 11.

### Rule 4 — Feature Set Validation
Liberty features must:
- Exist in 26.0.0.5
- Be compatible with each other
- Match subsystem requirements

### Rule 5 — Behavior Changes
If release notes indicate:
- changed defaults
- changed behavior
- changed wiring requirements

The snippet must be updated.

---

## 6. Freshness Index Output Format
Each snippet includes:

freshness:
score: 94
last_checked: "2026-06-10"
issues:
- "MicroProfile Config default changed in 26.0.0.5"
status: "fresh"

Code

Statuses:
- `fresh`
- `stale`
- `deprecated`
- `invalid`

---

## 7. Manual Override Rules
Editors may override the Freshness Index when:
- Liberty documentation is ambiguous
- Behavior differs between environments
- Vendor extensions apply

Overrides must include justification.

---

## 8. Pro Tier Enhancements
Pro users receive:
- Freshness Index for private blueprints
- Team‑level freshness dashboards
- Alerts when their stacks become stale
- Automated PRs to update wiring templates

---

## 9. Contribution Guidelines
When submitting new snippets:
- Include the prompt used
- Include the AI output
- Include the Freshness Index result
- Include manual corrections
- Include validation test results

This ensures reproducibility and auditability.


