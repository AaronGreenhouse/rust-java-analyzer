package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
interface ImmutableParent {}

@Immutable
interface OtherImmutableParent {}

@Immutable
interface ImmutableGenericParent<T> {}

class InterfaceTypeArgument {
    int count;
}

class InterfaceHolder {
    @Immutable
    interface ImmutableNestedParent {}
}

// IMM-SUPERINTERFACE not applicable: the interface has no superinterfaces
@Immutable
interface NoSuperinterfaces {}

@Immutable
interface ExtendsOneImmutable
        // IMM-SUPERINTERFACE satisfied: ImmutableParent is immutable
        extends ImmutableParent {}

@Immutable
interface ExtendsTwoImmutable
        // IMM-SUPERINTERFACE satisfied: ImmutableParent is immutable
        extends ImmutableParent,
                // IMM-SUPERINTERFACE satisfied: OtherImmutableParent is immutable
                OtherImmutableParent {}

@Immutable
interface ExtendsImmutableGenericParent
        // IMM-SUPERINTERFACE satisfied: the generic interface ImmutableGenericParent is
        // immutable; its type argument does not matter, even though it is mutable
        extends ImmutableGenericParent<InterfaceTypeArgument> {}

@Immutable
interface ExtendsImmutableNestedParent
        // IMM-SUPERINTERFACE satisfied: InterfaceHolder.ImmutableNestedParent, a nested
        // interface named by a qualified name, is immutable
        extends InterfaceHolder.ImmutableNestedParent {}
