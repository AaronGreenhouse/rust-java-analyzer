<!-- markdownlint-disable MD033 MD041 -->
<p align="center">
  <img src="docs/logo/logo.svg" alt="The rust-java-analyzer logo: an @ sign shaped like a rust-colored gear and magnifying glass, with a coffee bean inside" width="160">
</p>
<!-- markdownlint-enable MD033 MD041 -->

# rust-java-analyzer

A static analyzer for Java source code, written in Rust.

## Overview

The goal is to build, in Rust, an analyzer that checks Java source code against a new `@Immutable` annotation.

`@Immutable` was chosen because it can be checked with purely syntactic checks. Anything more complex would need a full Java control-flow graph. As far as I know, no open-source Rust implementation of one exists, but there is an open-source Java parser and AST.

## Repository layout

| Path | Contents |
| --- | --- |
| [`rust/`](rust/) | The analyzer, written in Rust. |
| [`java/`](java/) | The Java side: the `@Immutable` annotation and the test cases for it. |
| [`analyses/`](analyses/) | One description per analysis. These drive the implementation, including the annotation classes and the Java test files. |
| [`docs/`](docs/) | Project documentation. [`docs/logo/`](docs/logo/README.md) has the logo and describes how it is designed. |
| [`claude-prompt-log.md`](claude-prompt-log.md) | A log of every Claude Code prompt and response (see [Development with Claude](#development-with-claude)). |

## Analyses

| Analysis | Description |
| --- | --- |
| Immutable | Checks that instances of a class or interface marked `@Immutable` can't change after they are created. See [`analyses/Immutable.md`](analyses/Immutable.md). |

## Status

Early stage. The analysis is specified, but there is no implementation yet.

## Development with Claude

The intent is to have Claude Code do as much of the work as possible. To keep a record of how that goes, a Claude Code hook ([`.claude/hooks/prompt-log.sh`](.claude/hooks/prompt-log.sh)) adds every prompt and response to [`claude-prompt-log.md`](claude-prompt-log.md).
