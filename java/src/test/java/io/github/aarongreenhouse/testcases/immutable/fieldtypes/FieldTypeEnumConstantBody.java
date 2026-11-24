package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

// NOTE: fields declared in the class body of an enum constant are checked as if they
// were declared in the enum itself.

@Immutable
enum Level {
    // IMM-ANONYMOUS violated: the enum constant has a class body, which is an anonymous
    // class extending the immutable enum Level
    HIGH {
        // IMM-FIELD-TYPE violated: the field in the constant's class body has an array
        // type
        final int[] thresholds = {1, 2};

        // IMM-FIELD-TYPE satisfied: the field in the constant's class body has a
        // primitive type
        final int limit = 10;
    },
    // IMM-FIELD-TYPE satisfied: an enum constant has the type of its own enum, which is
    // immutable
    LOW;
}

enum Volume {
    LOUD {
        // IMM-FIELD-TYPE not applicable: the enum Volume is not immutable
        final int[] steps = {1, 2};
    }
}
