# Contributing

First, join the [support server](https://minecord.github.io/support) and request the "Developer" role to get up-to-date on what is currently being added.

## Setting Up the Environment

To get started, clone the repository with `git clone https://github.com/Tisawesomeness/Minecord.git` and open in your favorite IDE
([IntelliJ](https://www.jetbrains.com/idea/) is recommended, though any proper IDE will work).

To run the bot from your IDE, run the `Main` class. To build the executable JAR files, use `mvn package`.

## Conventions

Minecord is meant to be a beginner-friendly project. Follow conventions if you can, but don't worry too much if you are a beginner. We will guide you through any stylistic changes when you make a pull request.

### Formatting

This project 4 spaces for indentation and the One True Brace Style (1TBS), meaning that every control statement should have braces as shown below.

```java
if (condition) {
    statement;
}
statement;
```

### Annotations

You are **highly encouraged** to use the `@lombok.NonNull` and `@javax.annotation.Nullable` on class fields and method parameters.
This ensures that code elsewhere in the codebase knows exactly when a null check is necessary and prevents unexpected NullPointerExceptions.

### Documentation

Public methods should usually have Javadocs, which must have all assumptions, parameters, thrown exceptions, and return values documented.
