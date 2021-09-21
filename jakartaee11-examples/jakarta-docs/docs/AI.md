# AI.md — Jakarta EE / Open Liberty Intelligence Layer

## 1. Purpose
This document describes how AI is used inside this platform to deliver accurate, version‑pinned Jakarta EE 11 and Open Liberty 26.0.0.5 configuration guidance. The AI layer ensures that all wiring templates, configuration snippets, and architectural patterns remain fresh, validated, and aligned with official specifications.

## 2. AI Responsibilities
The AI system provides:
- Version‑pinned configuration generation
- Compatibility validation between Jakarta 11 and Liberty 26.0.0.5
- Architecture pattern synthesis for cloud‑native Java applications
- Snippet freshness checks tied to official release notes
- Team blueprint assistance for Pro users

The AI never invents APIs, annotations, or Liberty features. If a configuration is ambiguous or undocumented, the AI must explicitly state uncertainty.

## 3. Prompting Standards
All prompts follow this deterministic structure:
1. Clarifying questions
2. Implementation plan
3. Final configuration snippet
4. Optional code sample
5. Verification checklist

## 4. Context Pack (Required for Every AI Task)
- Jakarta EE version: 11
- Open Liberty version: 26.0.0.5
- Runtime: containerized, cloud‑native
- Deployment: Kubernetes / OpenShift
- Output format: XML, YAML, or Java
- Constraints: spec compliance, security hardening, production‑ready defaults, no deprecated features
- User intent: configuration, wiring, architecture, or validation

## 5. Deterministic Workflow
1. Request a plan
2. Approve or correct the plan
3. Generate the final configuration
4. Generate validation tests
5. AI performs a self‑review
6. System runs Freshness Index checks
7. Human reviews before publishing

## 6. Freshness Index
The platform maintains a Freshness Index measuring:
- DOCS SOURCE: https://openliberty.io/docs/latest/overview.html
- Alignment with Jakarta EE 11 specification
- Alignment with Open Liberty 26.0.0.5 release notes
- Deprecation detection
- Feature compatibility
- API stability

## 7. Security Notes
- No secrets or credentials are ever sent to the AI
- AI‑generated configs must pass security hardening checks
- Private enterprise blueprints remain encrypted
- AI cannot generate production credentials

## 8. Contribution Guidelines
When contributing AI‑generated snippets:
- Include the prompt
- Include the AI output
- Include the Freshness Index result
- Include manual corrections
- Include validation test results
