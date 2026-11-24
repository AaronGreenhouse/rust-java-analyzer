package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

// NOTE: IMM-LAMBDA is checked at the declaration of each immutable interface: the
// interface must not be a functional interface (JLS §9.8).

// IMM-LAMBDA satisfied: the interface has no abstract methods
@Immutable
interface Marker {}

// IMM-LAMBDA satisfied: the interface has two abstract methods
@Immutable
interface Range {
    int low();

    int high();
}

// IMM-LAMBDA satisfied: the interface has two abstract methods, both inherited from
// immutable Range
@Immutable
interface Span
        extends Range {}

// IMM-LAMBDA satisfied: the interface has only default and static methods, which are
// not abstract
@Immutable
interface Defaults {
    default int size() {
        return 0;
    }

    static int limit() {
        return 10;
    }
}

// IMM-LAMBDA satisfied: the interface's abstract methods all match public methods of
// java.lang.Object, so they do not count (JLS §9.8)
@Immutable
interface Identity {
    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

    @Override
    String toString();
}

// IMM-LAMBDA satisfied: the interface has one abstract method, but is sealed, and a
// sealed interface is not a functional interface (JLS §9.8)
@Immutable
sealed interface Expr permits Literal {
    int eval();
}

@Immutable
record Literal(int value)
        implements Expr {
    @Override
    public int eval() {
        return value;
    }
}

// IMM-LAMBDA satisfied: the interface has one abstract method, but is sealed. It has no
// permits clause, so its permitted subtypes are those declared in this file
// (JLS §9.1.1.4).
@Immutable
sealed interface Token {
    int length();
}

@Immutable
final class Word
        implements Token {
    @Override
    public int length() {
        return 1;
    }
}
