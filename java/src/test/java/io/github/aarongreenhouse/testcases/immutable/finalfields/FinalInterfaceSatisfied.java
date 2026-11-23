package io.github.aarongreenhouse.testcases.immutable.finalfields;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-FINAL-INTERFACE cannot be violated: every field declared in an interface is
// implicitly final (JLS §9.3). So there are only tests that it is satisfied.

@Immutable
interface Limits {
    // IMM-FINAL-INTERFACE satisfied: the field is implicitly final (JLS §9.3)
    int MIN = 0;

    // IMM-FINAL-INTERFACE satisfied: the field is declared final
    final int MAX = 100;

    // IMM-FINAL-INTERFACE satisfied: the field is declared public, static, and final
    public static final int DEFAULT = 50;

    // IMM-FINAL-INTERFACE satisfied: the field is declared static but is implicitly
    // final (JLS §9.3)
    static int STEP = 5;

    int
            // IMM-FINAL-INTERFACE satisfied: the first of two declarators; the field is
            // implicitly final (JLS §9.3)
            LOW = 10,
            // IMM-FINAL-INTERFACE satisfied: the second of two declarators; the field is
            // implicitly final (JLS §9.3)
            HIGH = 90;
}

class LimitsHolder {
    @Immutable
    interface NestedLimits {
        // IMM-FINAL-INTERFACE satisfied: the field of a nested interface is implicitly
        // final (JLS §9.3)
        int NESTED_MAX = 10;
    }
}
