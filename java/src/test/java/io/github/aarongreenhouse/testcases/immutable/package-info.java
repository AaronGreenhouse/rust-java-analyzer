/**
 * Test cases for the immutable analysis, described in {@code analyses/Immutable.md}.
 *
 * <p>Each subpackage holds the test cases for one group of rules, following the
 * sections of the analysis description:
 *
 * <ul>
 *   <li>{@code marker}: IMM-MARK
 *   <li>{@code supertypes}: IMM-SUPERCLASS, IMM-SUPERINTERFACE
 *   <li>{@code subtypes}: IMM-SUBINTERFACE, IMM-SUBCLASS
 *   <li>{@code finalfields}: IMM-FINAL-INTERFACE, IMM-FINAL-CLASS
 *   <li>{@code fieldtypes}: IMM-FIELD-TYPE
 *   <li>{@code nested}: IMM-INNER
 *   <li>{@code anonymous}: IMM-ANONYMOUS, IMM-LAMBDA
 * </ul>
 *
 * <p>A line comment states the expected result of each test. It is placed directly
 * before the code that the result is about: a type declaration, an {@code extends} or
 * {@code implements} clause, a field, a variable declarator, a record component, an enum
 * constant, or a class instance creation expression. A comment before a declaration also
 * covers a {@code new} expression in its initializer. The comments have these forms:
 *
 * <ul>
 *   <li>{@code // IMM-RULE satisfied: <case>} expects a positive result for the rule.
 *   <li>{@code // IMM-RULE violated: <how>} expects an error for the rule.
 *   <li>{@code // IMM-RULE not applicable: <why>} expects no result for the rule. These
 *       mark code that looks like it might be checked by the rule, but is not.
 *   <li>{@code // IMM-MARK immutable: <why>} and {@code // IMM-MARK not immutable: <why>}
 *       state whether a type counts as immutable. IMM-MARK has no results of its own.
 * </ul>
 *
 * <p>Every expected error in these files is marked, including errors for rules other
 * than the one a file is testing. Positive results are marked only for the rules a file
 * is testing. Positive results for other rules, such as the IMM-FIELD-TYPE result for an
 * {@code int} field in a test of IMM-FINAL-CLASS, are expected but not marked.
 *
 * <p>These points from the analysis description affect where results are expected:
 *
 * <ul>
 *   <li>IMM-SUBCLASS and IMM-SUBINTERFACE cover indirect subtypes as well as direct ones.
 *       An unmarked indirect subtype is reported even when a type between it and the
 *       immutable type is also reported.
 *   <li>An anonymous class, including an enum constant with a class body, is reported
 *       only under IMM-ANONYMOUS, not also under IMM-SUBCLASS or IMM-SUBINTERFACE.
 *   <li>A marked inner class that extends an immutable class satisfies IMM-SUBCLASS, and
 *       is reported under IMM-INNER.
 *   <li>IMM-LAMBDA is checked at the declaration of the immutable interface, so a lambda
 *       or method reference itself gets no result.
 * </ul>
 */
package io.github.aarongreenhouse.testcases.immutable;
