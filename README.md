<!-- markdownlint-disable MD033 MD041 -->
<p align="center">
  <img src="docs/logo/logo.svg" alt="The rust-java-analyzer logo: an @ sign shaped like a rust-colored gear and magnifying glass, with a coffee bean inside" width="160">
</p>
<!-- markdownlint-enable MD033 MD041 -->

# rust-java-analyzer

A simple static analyzer for Java programs, written in Rust.

## Overview

The goal of this project is to build a simple static analyzer for Java programs, written in Rust. The chosen analysis is *class immutability*: it checks that instances of a class or interface marked with a new `@Immutable` annotation can't change after they are created.

Immutability was chosen because it can be checked from declarations alone: a syntax tree plus name resolution, with no need to analyze method bodies. Anything more complex would need a full Java control-flow graph. As far as I know, no open-source Rust implementation of one exists, but there is an open-source Java parser and AST.

The project has two other purposes:

- **Claude Code experience.** The intent is to have Claude Code do as much of the work as possible, and to keep a record of how that goes (see [Development with Claude](#development-with-claude)).
- **Rust experience.** Writing the analyzer is a way for me to build my Rust skills.

## Status

I'm currently filling out the exact specification of the analysis, in [`analyses/Immutable.md`](analyses/Immutable.md), along with the Java test cases that go with it. This is an interactive process with Claude: Claude reviews the specification and the test cases, and we decide together how to resolve what the reviews find. The reviews and decisions are recorded in [`analyses/reviews/`](analyses/reviews/).

Once the specification is settled, it will drive a Rust implementation of the analyzer. There is no Rust code yet.

## Repository layout

| Path | Contents |
| --- | --- |
| [`rust/`](rust/) | The analyzer, written in Rust. Not started yet. |
| [`java/`](java/) | The Java side: the `@Immutable` annotation and the test cases for it. |
| [`analyses/`](analyses/) | One description per analysis. These drive the implementation, including the annotation classes and the Java test files. |
| [`analyses/reviews/`](analyses/reviews/) | Design reviews of the analysis descriptions and test cases, and the decisions made on them. Each is dated, and pinned to the commit it reviewed. |
| [`docs/`](docs/) | Project documentation. [`docs/logo/`](docs/logo/README.md) has the logo and describes how it is designed. |
| [`claude-prompt-log.md`](claude-prompt-log.md) | The log of every Claude Code prompt and response (see [Development with Claude](#development-with-claude)). |

## Analyses

| Analysis | Description | Reviews |
| --- | --- | --- |
| Immutable | Checks that instances of a class or interface marked `@Immutable` can't change after they are created. See [`analyses/Immutable.md`](analyses/Immutable.md). | [`analyses/reviews/immutable/`](analyses/reviews/immutable/) |

## Development with Claude

This project is developed interactively with Claude Code, and **the interaction is logged**. A Claude Code hook ([`.claude/hooks/prompt-log.sh`](.claude/hooks/prompt-log.sh)) adds every prompt and Claude's response to [**`claude-prompt-log.md`**](claude-prompt-log.md). The log is committed along with the work, so it shows the prompts that brought the repository to each state.

The design documents are also recorded:

- the analysis description, [`analyses/Immutable.md`](analyses/Immutable.md);
- the design reviews of that description and of its test cases, which Claude Code subagents carry out, and the decisions made on them, in [`analyses/reviews/immutable/`](analyses/reviews/immutable/).
