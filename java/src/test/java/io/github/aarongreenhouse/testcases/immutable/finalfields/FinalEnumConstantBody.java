package io.github.aarongreenhouse.testcases.immutable.finalfields;

import io.github.aarongreenhouse.promises.Immutable;

// NOTE: fields declared in the class body of an enum constant are checked as if they
// were declared in the enum itself.

@Immutable
enum Signal {
    // IMM-ANONYMOUS violated: the enum constant has a class body, which is an anonymous
    // class extending the immutable enum Signal
    RED {
        // IMM-FINAL-CLASS violated: the field in the constant's class body is not final
        int flashes;

        // IMM-FINAL-CLASS satisfied: the field in the constant's class body is declared
        // final
        final int delay = 3;
    },
    // IMM-FINAL-CLASS satisfied: an enum constant is implicitly final (JLS §8.9.3)
    GREEN;
}

enum Lamp {
    ON {
        // IMM-FINAL-CLASS not applicable: the enum Lamp is not immutable
        int level;
    }
}
