package io.github.aarongreenhouse.testcases.immutable.subtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
interface Shape {}

@Immutable
interface Container<T> {}

class MutableContents {
    int count;
}

@Immutable
interface Polygon
        // IMM-SUBINTERFACE satisfied: the subinterface of immutable Shape is immutable
        extends Shape {}

@Immutable
class Circle
        // IMM-SUBINTERFACE satisfied: the implementing class of immutable Shape is
        // immutable
        implements Shape {}

@Immutable
enum Arrow
        // IMM-SUBINTERFACE satisfied: the implementing enum of immutable Shape is
        // immutable
        implements Shape { LEFT, RIGHT }

@Immutable
record Dot(int x, int y)
        // IMM-SUBINTERFACE satisfied: the implementing record of immutable Shape is
        // immutable
        implements Shape {}

@Immutable
final class Square
        // IMM-SUBINTERFACE satisfied: the implementing class of immutable Polygon is
        // immutable
        // IMM-SUBINTERFACE satisfied: the class implements immutable Shape through
        // Polygon, and is immutable
        implements Polygon {}

@Immutable
final class Ring
        // IMM-SUBINTERFACE satisfied: the class implements immutable Shape through its
        // superclass Circle, and is immutable
        extends Circle {}

class ShapeHolder {
    @Immutable
    static class Star
            // IMM-SUBINTERFACE satisfied: the static nested implementing class of
            // immutable Shape is immutable
            implements Shape {}

    void draw() {
        @Immutable
        record Point(int x, int y)
                // IMM-SUBINTERFACE satisfied: the local record implementing immutable
                // Shape is immutable
                implements Shape {}
    }
}

@Immutable
class IntContainer
        // IMM-SUBINTERFACE satisfied: the implementing class of the immutable generic
        // interface Container is immutable; the type argument does not matter
        implements Container<MutableContents> {}
