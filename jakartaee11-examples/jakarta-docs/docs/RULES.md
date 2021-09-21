# AI Agent Rules

These rules apply to all AI sessions working on this project.

## Version Accuracy

1. **Versions must be exact — no hallucination.** If a version is uncertain or unknown, say so explicitly to the user. Never guess or fabricate version numbers.

2. **If the latest real version of something is unknown and not documented in this project**, search exhaustively on the internet (Maven Central, official docs, GitHub releases) before stating any version. Verify every version against a real source.

3. **When versions don't match expectations** (e.g., a server doesn't support the target EE version), flag it clearly — don't silently downgrade or assume compatibility.

## How to Verify Versions

1. **First**: DuckDuckGo HTML search: `https://html.duckduckgo.com/html/?q=latest+version+product-name`
2. **Then**: Maven Central for coordinates: `https://search.maven.org/solrsearch/select?q=g:{groupId}+AND+a:{artifactId}&rows=1&wt=json`
3. **Official docs**: Go directly to the product's website
4. **If mismatch**: Document it in VERSIONES-FIJADAS.md

## What NOT to Do

- Don't copy-paste version numbers from old documentation without verifying they still exist on Maven Central
- Don't assume a version exists because "it should"
- Don't silently use an older version if the target version isn't available

## Project Targets

- Jakarta EE 11
- Java 25 (`<release>25</release>`)
- Eclipse Krazo 4.0.0 (MVC 3.0)
