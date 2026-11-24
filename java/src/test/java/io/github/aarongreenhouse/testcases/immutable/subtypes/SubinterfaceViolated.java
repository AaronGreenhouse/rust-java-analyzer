package io.github.aarongreenhouse.testcases.immutable.subtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
interface Animal {}

@Immutable
interface Mammal
        // IMM-SUBINTERFACE satisfied: the subinterface of immutable Animal is immutable
        extends Animal {}

@Immutable
interface Bag<T> {}

interface Pet
        // IMM-SUBINTERFACE violated: the subinterface of immutable Animal is not marked
        extends Animal {}

class Dog
        // IMM-SUBINTERFACE violated: the implementing class of immutable Animal is not
        // marked
        implements Animal {}

enum Breed
        // IMM-SUBINTERFACE violated: the implementing enum of immutable Animal is not
        // marked
        implements Animal { HOUND }

record Tag(int id)
        // IMM-SUBINTERFACE violated: the implementing record of immutable Animal is not
        // marked
        implements Animal {}

interface HousePet
        // IMM-SUBINTERFACE violated: the subinterface of immutable Mammal is not marked
        // IMM-SUBINTERFACE violated: the indirect subinterface of immutable Animal,
        // through Mammal, is not marked
        extends Mammal {}

class Cat
        // IMM-SUBINTERFACE violated: the implementing class of immutable Mammal is not
        // marked
        // IMM-SUBINTERFACE violated: the class implements immutable Animal through
        // Mammal, but is not marked
        implements Mammal {}

class Puppy
        // IMM-SUBINTERFACE violated: the class implements immutable Animal through its
        // superclass Dog, but is not marked. Dog is reported too. It is not immutable,
        // so it does not count as an immutable supertype of this class.
        extends Dog {}

class Parrot
        // IMM-SUBINTERFACE violated: the class implements immutable Animal through the
        // unmarked subinterface Pet, but is not marked. Pet is reported too.
        implements Pet {}

interface Companion
        // IMM-SUBINTERFACE violated: the indirect subinterface of immutable Animal,
        // through the unmarked subinterface Pet, is not marked. Pet is reported too.
        extends Pet {}

@Immutable
sealed interface Coat permits Fur, Feathers {}

@Immutable
final class Fur
        // IMM-SUBINTERFACE satisfied: the permitted implementing class of immutable Coat
        // is immutable
        implements Coat {}

non-sealed class Feathers
        // IMM-SUBINTERFACE violated: the non-sealed permitted implementing class of
        // immutable Coat is not marked
        implements Coat {}

class Zoo {
    static class Keeper
            // IMM-SUBINTERFACE violated: the static nested implementing class of
            // immutable Animal is not marked
            implements Animal {}

    class Cage
            // IMM-SUBINTERFACE violated: the inner implementing class of immutable
            // Animal is not marked. Marking it is not possible because of IMM-INNER.
            implements Animal {}

    void admit() {
        class Stray
                // IMM-SUBINTERFACE violated: the local implementing class of immutable
                // Animal is not marked. Marking it is not possible because of IMM-INNER.
                implements Animal {}

        record Visit(int day)
                // IMM-SUBINTERFACE violated: the local record implementing immutable
                // Animal is not marked
                implements Animal {}

        enum Feed
                // IMM-SUBINTERFACE violated: the local enum implementing immutable Animal
                // is not marked
                implements Animal { HAY }
    }
}

class IntBag
        // IMM-SUBINTERFACE violated: the implementing class of the immutable generic
        // interface Bag is not marked
        implements Bag<Integer> {}
