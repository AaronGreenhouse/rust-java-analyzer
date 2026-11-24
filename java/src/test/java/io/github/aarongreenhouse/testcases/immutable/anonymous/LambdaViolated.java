package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

// NOTE: IMM-LAMBDA is checked at the declaration of each immutable interface: the
// interface must not be a functional interface (JLS §9.8).

// IMM-LAMBDA violated: the interface has exactly one abstract method
@Immutable
interface Source {
    int next();
}

// IMM-LAMBDA violated: the interface has exactly one abstract method, format. The
// default and static methods, and the methods that match public methods of
// java.lang.Object, do not count.
@Immutable
interface Formatter {
    String format(int value);

    default String formatTwice(int value) {
        return format(value) + format(value);
    }

    static int width() {
        return 10;
    }

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();
}

// IMM-LAMBDA violated: the interface is explicitly annotated @FunctionalInterface
@Immutable
@FunctionalInterface
interface Action {
    void perform();
}

// IMM-LAMBDA violated: the interface inherits exactly one abstract method, next, from
// immutable Source
@Immutable
interface CountingSource
        extends Source {}

// IMM-LAMBDA violated: the interface's one abstract method is generic. A lambda cannot
// implement it, but a method reference can (JLS §15.13.2).
@Immutable
interface Transformer {
    <T> T transform(T input);
}

// IMM-LAMBDA violated: the interface's one abstract method, clone, matches a protected
// method of java.lang.Object. Only public methods of Object do not count (JLS §9.8).
@Immutable
interface Copyable {
    Object clone();
}

// IMM-LAMBDA violated: the interface's one abstract method, equals(Comparer), does not
// match equals(Object) in java.lang.Object, so it counts
@Immutable
interface Comparer {
    boolean equals(Comparer other);
}

// IMM-LAMBDA violated: the interface inherits two abstract methods from immutable Range,
// but declares a default method for high, which leaves exactly one abstract method, low
@Immutable
interface LowOnly
        extends Range {
    @Override
    default int high() {
        return 0;
    }
}

// IMM-LAMBDA violated: the interface has exactly one abstract method
@Immutable
interface Reader {
    int read();
}

// IMM-LAMBDA violated: the interface has exactly one abstract method
@Immutable
interface Stream {
    int read();
}

// IMM-LAMBDA violated: the interface inherits read() from both Reader and Stream. The
// two methods have the same signature, so they count as one abstract method (JLS §9.8).
@Immutable
interface Channel
        extends Reader,
                Stream {}

// IMM-LAMBDA satisfied: the interface has one abstract method, but is sealed
@Immutable
sealed interface Node permits Leaf {
    int size();
}

// IMM-LAMBDA violated: the interface inherits exactly one abstract method from Node, and
// is non-sealed rather than sealed
@Immutable
non-sealed interface Leaf
        extends Node {}

// IMM-LAMBDA violated: the interface has exactly one abstract method. Its private
// method does not count.
@Immutable
interface Printer {
    void print(String text);

    private String pad(String text) {
        return " " + text;
    }
}

// IMM-LAMBDA satisfied: the interface has no abstract methods, only a default method
@Immutable
interface Greeter {
    default String greet() {
        return "hello";
    }
}

// IMM-LAMBDA violated: the interface redeclares the default method greet inherited
// from Greeter as abstract, so it has exactly one abstract method
@Immutable
interface FormalGreeter
        extends Greeter {
    @Override
    String greet();
}
