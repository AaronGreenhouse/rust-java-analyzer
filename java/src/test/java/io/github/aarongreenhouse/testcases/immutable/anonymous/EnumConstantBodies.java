package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

// An enum constant with a class body declares an anonymous class whose superclass is
// the enum (JLS §8.9.1).

@Immutable
enum Operation {
    // IMM-ANONYMOUS violated: the enum constant has a class body, which is an anonymous
    // class extending the immutable enum Operation
    PLUS {
        @Override
        int apply(int a, int b) {
            return a + b;
        }
    },
    // IMM-ANONYMOUS violated: the enum constant has a class body, which is an anonymous
    // class extending the immutable enum Operation
    MINUS {
        @Override
        int apply(int a, int b) {
            return a - b;
        }
    };

    abstract int apply(int a, int b);
}

@Immutable
enum Priority {
    // IMM-ANONYMOUS satisfied: the enum constant has no class body
    LOW(1),
    // IMM-ANONYMOUS violated: the enum constant has an empty class body, which still
    // declares an anonymous class extending the immutable enum Priority
    MEDIUM(2) {},
    // IMM-ANONYMOUS violated: the enum constant has arguments and a class body that
    // overrides a method
    HIGH(3) {
        @Override
        int weight() {
            return 10;
        }
    };

    final int level;

    Priority(int level) {
        this.level = level;
    }

    int weight() {
        return level;
    }
}

enum Mode {
    // IMM-ANONYMOUS not applicable: the enum Mode is not immutable
    FAST {},
    SLOW
}
