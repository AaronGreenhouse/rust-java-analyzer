package io.github.aarongreenhouse.testcases.immutable.marker;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-MARK not immutable: the class has no annotation
class UnmarkedClass {
    // IMM-FINAL-CLASS not applicable: UnmarkedClass is not immutable
    int count;

    // IMM-FIELD-TYPE not applicable: UnmarkedClass is not immutable
    final int[] values = {};
}

// IMM-MARK immutable: class annotated through a single-type import
@Immutable
class MarkedBase {}

// IMM-MARK not immutable: @Immutable is not @Inherited, so it is not inherited from
// MarkedBase
class UnmarkedSubclass
        // IMM-SUBCLASS violated: the subclass of immutable MarkedBase is not marked
        extends MarkedBase {
    // IMM-FINAL-CLASS not applicable: UnmarkedSubclass is not immutable
    int count;
}
