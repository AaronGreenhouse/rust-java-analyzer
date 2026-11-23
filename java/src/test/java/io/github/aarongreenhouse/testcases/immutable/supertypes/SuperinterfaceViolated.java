package io.github.aarongreenhouse.testcases.immutable.supertypes;

import io.github.aarongreenhouse.promises.Immutable;

interface MutableParent {
    // If MutableParent were allowed as a superinterface of an immutable interface, this
    // inherited constant would never have its type checked.
    int[] TABLE = {1, 2};
}

interface MutableGenericParent<T> {}

@Immutable
interface ImmutableSibling {}

// IMM-SUBINTERFACE violated: the subinterface of immutable ImmutableSibling is not marked
interface UnmarkedChild extends ImmutableSibling {}

@Immutable
interface ExtendsMutableInterface
        // IMM-SUPERINTERFACE violated: MutableParent is not immutable
        extends MutableParent {}

@Immutable
interface MutableListedSecond
        // IMM-SUPERINTERFACE satisfied: ImmutableSibling is immutable
        extends ImmutableSibling,
                // IMM-SUPERINTERFACE violated: MutableParent is not immutable
                MutableParent {}

@Immutable
interface MutableListedFirst
        // IMM-SUPERINTERFACE violated: MutableParent is not immutable
        extends MutableParent,
                // IMM-SUPERINTERFACE satisfied: ImmutableSibling is immutable
                ImmutableSibling {}

@Immutable
interface ExtendsMutableGenericInterface
        // IMM-SUPERINTERFACE violated: the generic interface MutableGenericParent is not
        // immutable, even though its type argument is
        extends MutableGenericParent<ImmutableSibling> {}

@Immutable
interface ExtendsLibraryInterface
        // IMM-SUPERINTERFACE violated: java.lang.Cloneable is outside the analyzed source
        extends Cloneable {}

@Immutable
interface ExtendsUnmarkedChild
        // IMM-SUPERINTERFACE violated: UnmarkedChild is not immutable, even though its
        // own superinterface ImmutableSibling is
        extends UnmarkedChild {}
