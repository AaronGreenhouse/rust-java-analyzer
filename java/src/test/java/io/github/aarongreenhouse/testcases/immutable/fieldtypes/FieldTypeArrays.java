package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class Tile {}

@Immutable
class Board {
    // IMM-FIELD-TYPE violated: array type int[]; array elements can always be assigned
    // (JLS §10)
    final int[] cells = {};

    // IMM-FIELD-TYPE violated: array type Tile[], even though the element type Tile is
    // immutable
    final Tile[] tiles = {};

    // IMM-FIELD-TYPE violated: multidimensional array type int[][]
    final int[][] grid = {};

    // IMM-FIELD-TYPE violated: the brackets after the variable name make the type int[]
    final int legacy[] = {};

    final int
            // IMM-FIELD-TYPE satisfied: primitive type int
            width = 0,
            // IMM-FIELD-TYPE violated: the brackets after the variable name make the type
            // int[], although the declaration starts with int
            heights[] = {};

    final int[]
            // IMM-FIELD-TYPE violated: array type int[]
            row = {},
            // IMM-FIELD-TYPE violated: the brackets after the variable name make the type
            // int[][]
            rows[] = {};

    // IMM-FIELD-TYPE violated: array type int[], in a static field
    static final int[] SIZES = {};
}

@Immutable
interface Tables {
    // IMM-FIELD-TYPE violated: array type int[], in an interface constant
    int[] TABLE = {1, 2};
}

@Immutable
record Route(
        // IMM-FIELD-TYPE violated: array type int[], in a record component
        int[] steps) {}

@Immutable
record Polyline(
        // IMM-FIELD-TYPE violated: a variable arity record component has an array type,
        // here int[] (JLS §8.10.1)
        int... points) {}

@Immutable
enum Direction {
    // IMM-FIELD-TYPE satisfied: an enum constant has the type of its enum, Direction,
    // which is immutable
    NORTH;

    // IMM-FIELD-TYPE violated: array type int[], in an enum
    final int[] delta = {0, 1};
}
