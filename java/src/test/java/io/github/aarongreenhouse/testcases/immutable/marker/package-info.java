/**
 * Test cases for rule IMM-MARK: a type is immutable if and only if its declaration is
 * annotated with {@link io.github.aarongreenhouse.promises.Immutable}. The tests check
 * how the annotation's name is resolved, and that an unrelated annotation with the
 * simple name {@code Immutable} is not mistaken for it.
 */
package io.github.aarongreenhouse.testcases.immutable.marker;
