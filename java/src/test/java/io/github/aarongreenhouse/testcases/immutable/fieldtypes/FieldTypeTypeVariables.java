package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class Label {}

@Immutable
class Sack<T> {}

@Immutable
class Holder<T> {
    // IMM-FIELD-TYPE violated: the type variable T is treated as a mutable type
    final T value = null;
}

@Immutable
class BoundedHolder<T extends Label> {
    // IMM-FIELD-TYPE violated: the type variable T is treated as a mutable type, even
    // though its bound Label is immutable
    final T value = null;
}

@Immutable
class PairHolder<A, B> {
    // IMM-FIELD-TYPE violated: the type variable A is treated as a mutable type
    final A first = null;

    // IMM-FIELD-TYPE violated: the type variable B is treated as a mutable type
    final B second = null;
}

@Immutable
class ArrayHolder<T> {
    // IMM-FIELD-TYPE violated: array type T[], whose element type is also a type variable
    final T[] values = null;
}

@Immutable
record Wrapper<T>(
        // IMM-FIELD-TYPE violated: the type variable T, in a record component, is treated
        // as a mutable type
        T value) {}

@Immutable
class SackHolder<T> {
    // IMM-FIELD-TYPE satisfied: Sack is an immutable generic class; a type variable as
    // its type argument does not matter
    final Sack<T> sack = null;
}
