## 📋 Quick Reference: Spotless, JaCoCo & PMD

---

## 🔵 SPOTLESS — Code Formatter

| Aspect | Quick Info |
|--------|------------|
| **What** | Automatically formats code to Google Java Format |
| **When** | Every build (checks formatting) |
| **Where** | Local + GitHub Actions |

**Commands:**
```bash
mvn spotless:apply    # Fix formatting automatically
mvn spotless:check    # Check if formatting is correct
```

**The Fix:** If `spotless:check` fails → run `spotless:apply` → commit the changes.

---

## 🟢 JACOCO — Code Coverage

| Aspect | Quick Info |
|--------|------------|
| **What** | Measures how much code is covered by tests |
| **When** | After tests run (`mvn verify`) |
| **Where** | Local + GitHub Actions |

**Commands:**
```bash
mvn verify           # Runs tests + generates coverage report
mvn jacoco:report    # Generate report only
```

**View Report:**
```bash
open target/site/jacoco/index.html   # Mac
start target/site/jacoco/index.html  # Windows
```
Green = covered, Red = not covered. Aim for 80%+ coverage.

---

## 🟡 PMD — Code Quality Analysis

| Aspect | Quick Info |
|--------|------------|
| **What** | Finds bugs, unused code, dead code, performance issues |
| **When** | During build (`mvn pmd:check`) |
| **Where** | Local + GitHub Actions |

**Commands:**
```bash
mvn pmd:check        # Check for violations (fails if found)
mvn pmd:pmd          # Generate HTML report
```

**View Report:**
```bash
open target/site/pmd.html   # Mac
start target/site/pmd.html  # Windows
```
Red = critical issues, Orange = warnings.

---

## ✅ One Command to Run Everything

```bash
mvn verify
```
This runs: Spotless check → Tests → JaCoCo report → PMD check

If it passes, your code is: formatted ✅ + tested ✅ + covered ✅ + quality checked ✅