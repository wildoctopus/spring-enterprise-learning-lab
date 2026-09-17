# Contributing

Thank you for helping improve this Java and Spring Boot interview preparation lab.
Contributions should make a concept easier to understand, run, test, or discuss in
an interview.

## Before you start

- Search existing issues and pull requests before proposing a duplicate.
- For study questions and open-ended ideas, use GitHub Discussions when enabled.
- Do not include leaked, confidential, or company-specific interview questions.
- Never include secrets, credentials, private source code, or real customer data.

## Good contribution types

- A runnable Java, Spring Boot, JPA, SQL, REST, concurrency, or system-design example.
- A focused interview question with a short answer and production follow-ups.
- A failure scenario that makes an incorrect assumption observable.
- A test that documents behavior or prevents a regression.
- A correction to an explanation, link, command, or misleading example.

## How to contribute

1. Fork the repository and create a focused branch.
2. Make the smallest change that clearly demonstrates the idea.
3. Add or update a focused test where behavior is executable.
4. Update the relevant README or documentation with the use case, tradeoffs, and
   when not to use the approach.
5. Run the relevant focused test and then the full suite:

   ```bash
   mvn clean test
   ```

6. Open a pull request describing the interview question, implementation choice,
   failure mode, and verification performed.

## Code guidelines

- Use Java 21 and the existing Spring Boot and Maven conventions.
- Prefer clear names and small examples over clever abstractions.
- Keep examples deterministic and runnable without external services when practical.
- Explain complexity, data volume assumptions, concurrency behavior, and operational
  tradeoffs when they matter.
- Keep comments focused on decisions or traps that are not obvious from the code.

## Pull request checklist

- [ ] The change has a clear learning or interview-preparation purpose.
- [ ] Tests cover the important behavior or the documentation explains why tests are
      not practical.
- [ ] README or related documentation is updated.
- [ ] `mvn clean test` passes locally.
- [ ] No secrets, private data, or leaked interview material are included.
