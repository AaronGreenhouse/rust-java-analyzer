package io.github.aarongreenhouse.testcases.immutable.crosspackage.lib;

import io.github.aarongreenhouse.promises.Immutable;

public class Units {
    // IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
    // IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
    @Immutable
    public static final class Meter {}
}
