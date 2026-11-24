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

// IMM-SUBCLASS satisfied: no class declaration can extend an enum (JLS §8.9). Its only
// subclasses are its constant bodies, which are reported under IMM-ANONYMOUS.
@Immutable
enum EnumWithBody {
    // IMM-ANONYMOUS violated: the enum constant has a class body, which is an anonymous
    // class extending the immutable enum EnumWithBody
    ONE {},
    TWO
}

@Immutable
class Vehicle {}

@Immutable
class Car
        // IMM-SUBCLASS satisfied: the subclass of immutable Vehicle is immutable
        extends Vehicle {}

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class SportsCar
        // IMM-SUBCLASS satisfied: the subclass of immutable Car is immutable
        // IMM-SUBCLASS satisfied: the indirect subclass of immutable Vehicle, through
        // Car, is immutable
        extends Car {}

@Immutable
abstract class Engine {}

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class Diesel
        // IMM-SUBCLASS satisfied: the subclass of immutable abstract class Engine is
        // immutable
        extends Engine {}

class Garage {
    // IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
    @Immutable
    static final class Truck
            // IMM-SUBCLASS satisfied: the static nested subclass of immutable Vehicle is
            // immutable
            extends Vehicle {}
}

@Immutable
sealed class Tree permits TreeLeaf, TreeNode {}

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
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

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class AppleCrate
        // IMM-SUBCLASS satisfied: the subclass of the immutable generic class Crate is
        // immutable; the type argument does not matter
        extends Crate<Apple> {}
