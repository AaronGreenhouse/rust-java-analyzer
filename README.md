# rust-java-analyzer

## Overview

Simple analyzer for Java programs written in Rust.

More specficially, the plan here is to create in Rust an analyzer for Java source code that checks a new @Immutable annotation.  This is an annotation that can be checked purely by syntactic checks.  I cannot do anything more complicated because I would have to have a full Java control flow graph, and to the best of my knowledge there is no open source implementation of such a thing, but there is a an open source parser and AST.

This repo will have both Rust (`rust/`) and Java ('java/') source trees.  In particular, the Java source tree will contain the Immutable annotation as well as the test cases for the annotation.

The directory `analyses` contains the descriptions of all the implemented analyses; right now there is 1. The idea is to use the descriptions to drive the implementation of the analyses, including the derivation of any necessaray annotation classes and the generation of source java files to use as test cases.

## About `Immutable`

Please see the file `Immutable.md` for a description of the immutable analysis.
