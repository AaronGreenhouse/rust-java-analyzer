package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
@Immutable
class ImmutableBase {}

// IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
@Immutable
abstract class ImmutableAbstractBase {}

// IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
@Immutable
class ImmutableGenericBase<T> {}

class MutableTypeArgument {
    int count;
}

class MutableAncestor {
    int count;
}

@Immutable
class FlawedBase
        // IMM-SUPERCLASS violated: the superclass MutableAncestor is not immutable
        extends MutableAncestor {}

@Immutable
class ExtendsFlawedBase
        // IMM-SUPERCLASS satisfied: the superclass FlawedBase is declared immutable. It
        // counts as immutable even though it has errors of its own.
        extends FlawedBase {}

class SuperclassHolder {
    // IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
    @Immutable
    static class ImmutableNestedBase {}
}

// IMM-SUPERCLASS satisfied: the superclass is implicitly java.lang.Object
@Immutable
class ExtendsImplicitObject {}

@Immutable
class ExtendsExplicitObject
        // IMM-SUPERCLASS satisfied: the superclass is java.lang.Object
        extends Object {}

@Immutable
class ExtendsQualifiedObject
        // IMM-SUPERCLASS satisfied: the superclass is java.lang.Object, named by its
        // fully qualified name
        extends java.lang.Object {}

// IMM-SUPERCLASS satisfied: the superclass of an enum is java.lang.Enum
@Immutable
enum EnumSuperclass { ONLY }

// IMM-SUPERCLASS satisfied: the superclass of a record is java.lang.Record
@Immutable
record RecordSuperclass(int value) {}

@Immutable
class ExtendsImmutable
        // IMM-SUPERCLASS satisfied: the superclass ImmutableBase is immutable
        extends ImmutableBase {}

@Immutable
class ExtendsImmutableAbstract
        // IMM-SUPERCLASS satisfied: the abstract superclass ImmutableAbstractBase is
        // immutable
        extends ImmutableAbstractBase {}

@Immutable
class ExtendsImmutableGeneric
        // IMM-SUPERCLASS satisfied: the generic superclass ImmutableGenericBase is
        // immutable; its type argument does not matter, even though it is mutable
        extends ImmutableGenericBase<MutableTypeArgument> {}

@Immutable
class ExtendsImmutableNested
        // IMM-SUPERCLASS satisfied: the superclass SuperclassHolder.ImmutableNestedBase,
        // a static nested class named by a qualified name, is immutable
        extends SuperclassHolder.ImmutableNestedBase {}

@Immutable
class ExtendsImmutableQualified
        // IMM-SUPERCLASS satisfied: the superclass ImmutableBase, named by its fully
        // qualified name, is immutable
        extends io.github.aarongreenhouse.testcases.immutable.supertypes.ImmutableBase {}
