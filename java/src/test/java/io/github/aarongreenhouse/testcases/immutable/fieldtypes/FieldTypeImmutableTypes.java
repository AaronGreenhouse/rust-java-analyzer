package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
interface Color {}

@Immutable
class Money {}

@Immutable
enum Suit {
    // IMM-FIELD-TYPE satisfied: an enum constant has the type of its enum, Suit, which
    // is immutable
    HEARTS,
    // IMM-FIELD-TYPE satisfied: an enum constant has the type of its enum, Suit, which
    // is immutable
    SPADES
}

@Immutable
record Point(
        // IMM-FIELD-TYPE satisfied: primitive type int, in a record component
        int x,
        // IMM-FIELD-TYPE satisfied: primitive type int, in a record component
        int y) {}

@Immutable
class Box<T> {}

class Loose {
    int count;
}

class Cabinet {
    @Immutable
    static class Shelf {}
}

@Immutable
class Wallet {
    // IMM-FIELD-TYPE satisfied: Color is an immutable interface
    final Color color = null;

    // IMM-FIELD-TYPE satisfied: Money is an immutable class
    final Money money = null;

    // IMM-FIELD-TYPE satisfied: Suit is an immutable enum
    final Suit suit = Suit.HEARTS;

    // IMM-FIELD-TYPE satisfied: Point is an immutable record
    final Point point = null;

    // IMM-FIELD-TYPE satisfied: Wallet, the type being declared, is immutable
    final Wallet next = null;

    // IMM-FIELD-TYPE satisfied: Cabinet.Shelf, a static nested class named by a
    // qualified name, is immutable
    final Cabinet.Shelf shelf = null;

    // IMM-FIELD-TYPE satisfied: Money, named by its fully qualified name, is immutable
    final io.github.aarongreenhouse.testcases.immutable.fieldtypes.Money cash = null;

    // IMM-FIELD-TYPE satisfied: Box is an immutable generic class; its type argument
    // does not matter, even though Loose is mutable
    final Box<Loose> box = null;

    // IMM-FIELD-TYPE satisfied: Box is an immutable generic class, with an unbounded
    // wildcard type argument
    final Box<?> anyBox = null;

    // IMM-FIELD-TYPE satisfied: Box is an immutable generic class, with a bounded
    // wildcard type argument
    final Box<? extends Loose> boundedBox = null;

    // IMM-FIELD-TYPE satisfied: Money is an immutable class, in a static field
    static final Money ZERO = null;
}

@Immutable
interface Palette {
    // IMM-FIELD-TYPE satisfied: Color is an immutable interface, in an interface constant
    Color PRIMARY = null;
}

@Immutable
record Hand(
        // IMM-FIELD-TYPE satisfied: Suit is an immutable enum, in a record component
        Suit suit,
        // IMM-FIELD-TYPE satisfied: Box is an immutable generic class, in a record
        // component
        Box<Loose> box) {}
