package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

interface MutableParent {
    // If MutableParent were allowed as a superinterface of an immutable interface, this
    // inherited constant would never have its type checked.
    int[] TABLE = {1, 2};
}

interface MutableGenericParent<T> {}

// IMM-SUPERINTERFACE satisfied: the interface has no superinterfaces
@Immutable
interface ImmutableSibling {}

interface UnmarkedChild
        // IMM-SUBINTERFACE violated: the subinterface of immutable ImmutableSibling is
        // not marked
        extends ImmutableSibling {}

@Immutable
interface ExtendsMutableInterface
        // IMM-SUPERINTERFACE violated: the superinterface MutableParent is not immutable
        extends MutableParent {}

@Immutable
interface MutableListedSecond
        // IMM-SUPERINTERFACE satisfied: the superinterface ImmutableSibling is immutable
        extends ImmutableSibling,
                // IMM-SUPERINTERFACE violated: the superinterface MutableParent is not
                // immutable
                MutableParent {}

@Immutable
interface MutableListedFirst
        // IMM-SUPERINTERFACE violated: the superinterface MutableParent is not immutable
        extends MutableParent,
                // IMM-SUPERINTERFACE satisfied: the superinterface ImmutableSibling is
                // immutable
                ImmutableSibling {}

@Immutable
interface ExtendsMutableGenericInterface
        // IMM-SUPERINTERFACE violated: the generic superinterface MutableGenericParent is
        // not immutable, even though its type argument is
        extends MutableGenericParent<ImmutableSibling> {}

@Immutable
interface ExtendsLibraryInterface
        // IMM-SUPERINTERFACE violated: the superinterface java.lang.Cloneable is outside
        // the analyzed source
        extends Cloneable {}

@Immutable
interface ExtendsUnmarkedChild
        // IMM-SUPERINTERFACE violated: the superinterface UnmarkedChild is not immutable,
        // even though its own superinterface ImmutableSibling is
        extends UnmarkedChild {}
