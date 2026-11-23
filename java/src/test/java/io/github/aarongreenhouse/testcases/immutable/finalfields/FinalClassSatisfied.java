package io.github.aarongreenhouse.testcases.immutable.finalfields;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class Temperature {
    // IMM-FINAL-CLASS satisfied: the instance field is declared final
    final int degrees;

    // IMM-FINAL-CLASS satisfied: the private instance field is declared final
    private final int precision;

    // IMM-FINAL-CLASS satisfied: the static field is declared final
    static final int ABSOLUTE_ZERO = -273;

    // IMM-FINAL-CLASS satisfied: the transient instance field is declared final
    transient final int cached = 0;

    final int
            // IMM-FINAL-CLASS satisfied: the first of two declarators; the field is
            // declared final
            low = 0,
            // IMM-FINAL-CLASS satisfied: the second of two declarators; the field is
            // declared final
            high = 100;

    Temperature(int degrees, int precision) {
        this.degrees = degrees;
        this.precision = precision;
    }

    int scaled(int factor) {
        // IMM-FINAL-CLASS not applicable: a local variable is not a field
        int result = degrees * factor;
        result += precision;
        return result;
    }

    static class Scale {
        // IMM-FINAL-CLASS not applicable: the field belongs to the static nested class
        // Scale, which is not immutable and not part of the state of Temperature
        int divisions;
    }
}

@Immutable
abstract class Measurement {
    // IMM-FINAL-CLASS satisfied: the field of an abstract class is declared final
    final int units = 1;
}

@Immutable
record Reading(
        // IMM-FINAL-CLASS satisfied: the field for a record component is implicitly
        // final (JLS §8.10.3)
        int value,
        // IMM-FINAL-CLASS satisfied: the field for a record component is implicitly
        // final (JLS §8.10.3)
        long time) {

    // IMM-FINAL-CLASS satisfied: the static field of a record is declared final
    static final int MAX = 10;
}

@Immutable
enum Level {
    // IMM-FINAL-CLASS satisfied: an enum constant is implicitly final (JLS §8.9.3)
    LOW(1),
    // IMM-FINAL-CLASS satisfied: an enum constant is implicitly final (JLS §8.9.3)
    HIGH(2);

    // IMM-FINAL-CLASS satisfied: the instance field of an enum is declared final
    final int weight;

    // IMM-FINAL-CLASS satisfied: the static field of an enum is declared final
    static final int COUNT = 2;

    Level(int weight) {
        this.weight = weight;
    }
}
