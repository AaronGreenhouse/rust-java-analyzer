package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

// NOTE: IMM-LAMBDA is checked at the declaration of each immutable interface, not at
// each lambda or method reference. So the lambdas and method references here get no
// result. The error for them is reported at the declaration of Generator.

// IMM-LAMBDA violated: the interface has exactly one abstract method
@Immutable
interface Generator {
    int next();
}

class Pipeline {
    // IMM-LAMBDA not applicable: the lambda targets immutable Generator, but the error
    // is reported at the declaration of Generator
    final Generator zeros = () -> 0;

    // IMM-LAMBDA not applicable: the method reference targets immutable Generator, but
    // the error is reported at the declaration of Generator
    final Generator fromMethod = Pipeline::zero;

    // IMM-LAMBDA not applicable: the lambda targets java.lang.Runnable, which is not
    // immutable
    final Runnable task = () -> {};

    static int zero() {
        return 0;
    }

    void count() {
        int[] state = {0};
        // IMM-LAMBDA not applicable: the lambda targets immutable Generator and captures
        // mutable state, but the error is reported at the declaration of Generator
        Generator counter = () -> state[0]++;
    }
}
