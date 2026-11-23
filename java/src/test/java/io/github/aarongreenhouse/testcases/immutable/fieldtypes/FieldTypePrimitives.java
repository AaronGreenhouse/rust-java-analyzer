package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class AllPrimitives {
    // IMM-FIELD-TYPE satisfied: primitive type boolean
    final boolean flag = false;

    // IMM-FIELD-TYPE satisfied: primitive type byte
    final byte octet = 0;

    // IMM-FIELD-TYPE satisfied: primitive type short
    final short small = 0;

    // IMM-FIELD-TYPE satisfied: primitive type char
    final char letter = 'a';

    // IMM-FIELD-TYPE satisfied: primitive type int
    final int number = 0;

    // IMM-FIELD-TYPE satisfied: primitive type long
    final long big = 0L;

    // IMM-FIELD-TYPE satisfied: primitive type float
    final float ratio = 0.0f;

    // IMM-FIELD-TYPE satisfied: primitive type double
    final double precise = 0.0;

    // IMM-FIELD-TYPE satisfied: primitive type int, in a static field
    static final int LIMIT = 10;
}

@Immutable
interface PrimitiveConstants {
    // IMM-FIELD-TYPE satisfied: primitive type int, in an interface constant
    int ANSWER = 42;
}

@Immutable
record PrimitiveRecord(
        // IMM-FIELD-TYPE satisfied: primitive type int, in a record component
        int count,
        // IMM-FIELD-TYPE satisfied: primitive type double, in a record component
        double weight) {}
