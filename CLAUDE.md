This repo contains both Java and Rust code

* Java code is in the top-level directory `java`.
* Rust code is in the top-level directory `rust`.
* Please do not mix Java and Rust code.
* All Java packages must be under the package prefix `io.github.aarongreenhouse`.
* Annotation types for the analyses go in the package `io.github.aarongreenhouse.promises` or its subpackages. For example, the immutable marker type is `io.github.aarongreenhouse.promises.Immutable`.
* Java source code used to test analysis behavior goes in the package `io.github.aarongreenhouse.testcases`, with one subpackage per analysis. For example, test cases for the immutable analysis go in `io.github.aarongreenhouse.testcases.immutable`. These files go under `java/src/test/java/`, so that Maven compiles them but does not include them in the `promises` jar.
