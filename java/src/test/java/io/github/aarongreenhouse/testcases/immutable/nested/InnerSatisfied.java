package io.github.aarongreenhouse.testcases.immutable.nested;

import io.github.aarongreenhouse.promises.Immutable;

class Library {
    // IMM-INNER satisfied: a member class declared static is not an inner class
    @Immutable
    static class Catalog {}

    // IMM-INNER satisfied: a member enum is implicitly static (JLS §8.9)
    @Immutable
    enum Genre { FICTION }

    // IMM-INNER satisfied: a member record is implicitly static (JLS §8.10)
    @Immutable
    record Isbn(long value) {}

    // IMM-INNER satisfied: a member interface is implicitly static (JLS §9.1.1.3)
    @Immutable
    interface Lendable {}

    void open() {
        // IMM-INNER satisfied: a local record is implicitly static (JLS §14.3)
        @Immutable
        record Visit(int day) {}

        // IMM-INNER satisfied: a local enum is implicitly static (JLS §14.3)
        @Immutable
        enum Shift { DAY }

        // IMM-INNER satisfied: a local interface is implicitly static (JLS §14.3)
        @Immutable
        interface Rule {}
    }
}

@Immutable
interface Registry {
    // IMM-INNER satisfied: a member class of an interface is implicitly static
    // (JLS §9.5)
    @Immutable
    class Entry {}
}

class Building {
    class Floor {
        // IMM-INNER satisfied: a member class declared static is not an inner class,
        // even inside an inner class
        @Immutable
        static class Room {}

        // IMM-INNER satisfied: a member record is implicitly static (JLS §8.10), even
        // inside an inner class
        @Immutable
        record Door(int number) {}
    }
}

@Immutable
class Archive {
    // IMM-INNER not applicable: Index is not marked. A static nested class of an
    // immutable class is not part of its state, so it need not be immutable.
    static class Index {
        // IMM-FINAL-CLASS not applicable: Index is not immutable
        int count;
    }
}
