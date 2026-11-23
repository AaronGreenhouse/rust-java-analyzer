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
        // IMM-SUBINTERFACE violated: the subinterface of immutable Mammal, and through it
        // of immutable Animal, is not marked
        extends Mammal {}

class Cat
        // IMM-SUBINTERFACE violated: the class implements immutable Mammal, and through
        // it immutable Animal, but is not marked
        implements Mammal {}

class Puppy
        // IMM-SUBINTERFACE violated: the class implements immutable Animal through its
        // superclass Dog, but is not marked. Dog is reported too.
        extends Dog {}

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
    }
}

class IntBag
        // IMM-SUBINTERFACE violated: the implementing class of the immutable generic
        // interface Bag is not marked
        implements Bag<Integer> {}
