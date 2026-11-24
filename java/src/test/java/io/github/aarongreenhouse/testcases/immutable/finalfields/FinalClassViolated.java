package io.github.aarongreenhouse.testcases.immutable.finalfields;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class Counter {
    // IMM-FINAL-CLASS violated: the instance field is not final
    int count;

    // IMM-FINAL-CLASS violated: the private instance field is not final
    private int hidden;

    // IMM-FINAL-CLASS violated: the static field is not final
    static int instances;

    // IMM-FINAL-CLASS violated: the static field with an initializer is not final
    static int next = 1;

    // IMM-FINAL-CLASS violated: the volatile instance field is not final
    volatile int flag;

    // IMM-FINAL-CLASS violated: the transient instance field is not final
    transient int cache;

    int
            // IMM-FINAL-CLASS violated: the first of two declarators; the field is not
            // final
            low,
            // IMM-FINAL-CLASS violated: the second of two declarators; the field is not
            // final
            high;
}

@Immutable
abstract class Gauge {
    // IMM-FINAL-CLASS violated: the field of an abstract class is not final
    int reading;
}

@Immutable
record Pair(
        // IMM-FINAL-CLASS satisfied: the field for a record component is implicitly
        // final (JLS §8.10.3)
        int a,
        // IMM-FINAL-CLASS satisfied: the field for a record component is implicitly
        // final (JLS §8.10.3)
        int b) {
    // IMM-FINAL-CLASS violated: the static field of a record is not final
    static int count;
}

@Immutable
enum Phase {
    // IMM-FINAL-CLASS satisfied: an enum constant is implicitly final (JLS §8.9.3)
    SOLID,
    // IMM-FINAL-CLASS satisfied: an enum constant is implicitly final (JLS §8.9.3)
    LIQUID;

    // IMM-FINAL-CLASS violated: the instance field of an enum is not final
    int temperature;

    // IMM-FINAL-CLASS violated: the static field of an enum is not final
    static int changes;
}
