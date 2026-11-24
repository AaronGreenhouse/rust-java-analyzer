package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

class MutableBase {
    int count;
}

class MutableHolder {
    static class MutableNestedBase {
        int count;
    }
}

class MutableGenericBase<T> {
    T value;
}

// IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
@Immutable
class ImmutableRoot {}

// IMM-SUBCLASS violated: the subclass of immutable ImmutableRoot is not marked
class UnmarkedMiddle extends ImmutableRoot {}

@Immutable
class ExtendsMutable
        // IMM-SUPERCLASS violated: the superclass MutableBase is not immutable
        extends MutableBase {}

@Immutable
class ExtendsMutableNested
        // IMM-SUPERCLASS violated: the superclass MutableHolder.MutableNestedBase is not
        // immutable
        extends MutableHolder.MutableNestedBase {}

@Immutable
class ExtendsMutableGeneric
        // IMM-SUPERCLASS violated: the generic superclass MutableGenericBase is not
        // immutable, even though its type argument is
        extends MutableGenericBase<ImmutableRoot> {}

@Immutable
class ExtendsLibraryClass
        // IMM-SUPERCLASS violated: the superclass java.lang.Thread is outside the
        // analyzed source
        extends Thread {}

@Immutable
class ExtendsUnmarkedMiddle
        // IMM-SUPERCLASS violated: the superclass UnmarkedMiddle is not immutable, even
        // though its own superclass ImmutableRoot is
        extends UnmarkedMiddle {}
