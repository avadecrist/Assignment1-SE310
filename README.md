# Assignment1-SE310 — SOLID refactor summary

This repository contains a small Java blockchain/ledger exercise. I applied a series of refactors to improve SOLID compliance, testability, and maintainability. This README summarizes the design changes, files touched, how to run tests, verification performed, and suggested next steps.

## Goals
- Improve Single Responsibility, Dependency Inversion, and encapsulation across core domain classes.
- Make value objects immutable where appropriate.
- Separate concerns (validation, processing, hashing) into small collaborators.
- Keep backwards-compatible, minimal API changes where feasible and update call sites.

## High-level changes
- Exceptions
  - `CommandProcessorException` — made immutable, call `super(message)`, removed setters, added constructors (message, message+cause, with lineNumber), `serialVersionUID`, and `toString()`.
  - `LedgerException` — made immutable, call `super(message)`, removed setters; `getReason()` retained but delegates to `getMessage()` for backward compatibility.

- Merkle tree
  - Introduced `HashStrategy` (interface) and `SHA256HashStrategy` implementation.
  - Refactored `MerkleTrees` to be immutable, compute the root at construction, and accept a `HashStrategy` (defaults to SHA-256). This improves DIP and testability.

- Transaction processing & ledger
  - `TransactionProcessor` (existing/new) updated to use the new `MerkleTrees` API (root computed at construction). Several earlier refactors made `Transaction` immutable and decoupled from `Account`, and `Account` exposes `debit`/`credit` for controlled mutation.

- Encapsulation and APIs
  - `Block` API returned unmodifiable views and exposes `addTransaction` (keeps internal state safe).
  - `CommandProcessor` is instance-based and prints errors using the immutable exceptions.

## Files changed / added (summary)
- Edited: `src/main/java/com/se310/ledger/CommandProcessorException.java` — immutable exception
- Edited: `src/main/java/com/se310/ledger/LedgerException.java` — immutable exception, getReason() -> getMessage()
- Added: `src/main/java/com/se310/ledger/HashStrategy.java` — hashing abstraction
- Added: `src/main/java/com/se310/ledger/SHA256HashStrategy.java` — SHA-256 implementation
- Edited: `src/main/java/com/se310/ledger/MerkleTrees.java` — immutable, DI-friendly
- Edited: `src/main/java/com/se310/ledger/TransactionProcessor.java` — consume new MerkleTrees API
- (Earlier) Edited: `Transaction.java`, `Account.java`, `Block.java`, `Ledger.java`, `CommandProcessor.java` — various SOLID-driven refactors applied earlier in the session

> If you want a detailed diff/PR-ready summary, I can generate a concise changelog listing exact hunks per file.

## How to run tests
Requirements: JDK compatible with the project's `pom.xml` (this workspace has previously used Java 21); Maven installed.

From the repo root run:

```bash
mvn test
```

I ran the project's driver test during the refactor process and confirmed `DriverTest` passed (integration-style test). If your environment has a different JDK, update `JAVA_HOME` accordingly or use an SDK manager (e.g., `sdkman`/`jenv`/Homebrew-installed `openjdk@21`).

## Verification performed
- Ran `mvn test` (DriverTest) after each logical change. Tests passed and the integration output (ledger operations) remained consistent.
- Updated call sites that used removed setters/getters (e.g., `CommandProcessor` handling of `CommandProcessorException`) and removed reliance on side-effected exceptions.

## Next recommended steps
- Add focused unit tests:
  - `MerkleTrees` with a fake `HashStrategy` to assert deterministic root computation.
  - `Account`'s `debit`/`credit` edge cases.
  - `TransactionProcessor` happy path and error cases (using mocked Ledger/Block).
- Extract small interfaces for DIP (e.g., `AccountView`, `BlockView`) if you plan to mock ledger internals heavily.
- Prepare a PR with the changes and include this README as the summary for reviewers.

## Contact / notes
If you'd like, I can:
- Create unit tests for `MerkleTrees` and `TransactionProcessor` now.
- Produce a PR-ready changelog with exact patches for each file.

---

Generated on: 2025-09-21
