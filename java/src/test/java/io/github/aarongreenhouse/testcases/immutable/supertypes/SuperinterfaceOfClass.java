package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

interface MutableConstants {
    int[] SHARED = {1, 2};
}

// NOTE: IMM-SUPERINTERFACE applies only to interfaces. An immutable class, enum, or
// record may implement an interface that is not immutable. The constant SHARED that
// these types inherit is not checked, which is a known limitation of the analysis.

@Immutable
class ClassImplementsMutable
        // IMM-SUPERINTERFACE not applicable: ClassImplementsMutable is a class
        implements MutableConstants {}

@Immutable
enum EnumImplementsMutable
        // IMM-SUPERINTERFACE not applicable: EnumImplementsMutable is an enum
        implements MutableConstants { ONLY }

@Immutable
record RecordImplementsMutable(int value)
        // IMM-SUPERINTERFACE not applicable: RecordImplementsMutable is a record
        implements MutableConstants {}
