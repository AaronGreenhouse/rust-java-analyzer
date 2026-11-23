package io.github.aarongreenhouse.testcases.immutable.subtypes;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class FinalLeaf {}

// IMM-SUBCLASS satisfied: a record is implicitly final, so it has no subclasses
// (JLS §8.10)
@Immutable
record FinalRecord(int value) {}

// IMM-SUBCLASS satisfied: an enum whose constants have no class bodies is implicitly
// final, so it has no subclasses (JLS §8.9)
@Immutable
enum FinalEnum { ONE, TWO }

@Immutable
class Vehicle {}

@Immutable
class Car
        // IMM-SUBCLASS satisfied: the subclass of immutable Vehicle is immutable
        extends Vehicle {}

@Immutable
final class SportsCar
        // IMM-SUBCLASS satisfied: the subclass of immutable Car, and through it of
        // immutable Vehicle, is immutable
        extends Car {}

@Immutable
abstract class Engine {}

@Immutable
final class Diesel
        // IMM-SUBCLASS satisfied: the subclass of immutable abstract class Engine is
        // immutable
        extends Engine {}

class Garage {
    @Immutable
    static final class Truck
            // IMM-SUBCLASS satisfied: the static nested subclass of immutable Vehicle is
            // immutable
            extends Vehicle {}
}

@Immutable
sealed class Tree permits TreeLeaf, TreeNode {}

@Immutable
final class TreeLeaf
        // IMM-SUBCLASS satisfied: the permitted subclass of immutable Tree is immutable
        extends Tree {}

@Immutable
non-sealed class TreeNode
        // IMM-SUBCLASS satisfied: the non-sealed permitted subclass of immutable Tree is
        // immutable
        extends Tree {}

@Immutable
class Crate<T> {}

class Apple {
    int weight;
}

@Immutable
final class AppleCrate
        // IMM-SUBCLASS satisfied: the subclass of the immutable generic class Crate is
        // immutable; the type argument does not matter
        extends Crate<Apple> {}
