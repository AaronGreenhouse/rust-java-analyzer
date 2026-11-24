package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
@Immutable
interface ImmutableParent {}

// IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
@Immutable
interface OtherImmutableParent {}

// IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
@Immutable
interface ImmutableGenericParent<T> {}

class InterfaceTypeArgument {
    int count;
}

class InterfaceHolder {
    // IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
    @Immutable
    interface ImmutableNestedParent {}
}

// IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
@Immutable
interface NoSuperinterfaces {}

@Immutable
interface ExtendsOneImmutable
        // IMM-SUPERINTERFACE satisfied: the superinterface ImmutableParent is immutable
        extends ImmutableParent {}

@Immutable
interface ExtendsTwoImmutable
        // IMM-SUPERINTERFACE satisfied: the superinterface ImmutableParent is immutable
        extends ImmutableParent,
                // IMM-SUPERINTERFACE satisfied: the superinterface OtherImmutableParent
                // is immutable
                OtherImmutableParent {}

@Immutable
interface ExtendsImmutableGenericParent
        // IMM-SUPERINTERFACE satisfied: the generic superinterface ImmutableGenericParent
        // is immutable; its type argument does not matter, even though it is mutable
        extends ImmutableGenericParent<InterfaceTypeArgument> {}

@Immutable
interface ExtendsImmutableNestedParent
        // IMM-SUPERINTERFACE satisfied: the superinterface
        // InterfaceHolder.ImmutableNestedParent, a nested interface named by a qualified
        // name, is immutable
        extends InterfaceHolder.ImmutableNestedParent {}
