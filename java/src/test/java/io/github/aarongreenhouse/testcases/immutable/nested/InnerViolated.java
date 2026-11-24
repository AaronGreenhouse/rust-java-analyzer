package io.github.aarongreenhouse.testcases.immutable.nested;

import io.github.aarongreenhouse.promises.Immutable;

class Store {
    // IMM-INNER violated: a member class that is not static is an inner class
    @Immutable
    class Clerk {}

    final Runnable task = () -> {
        // IMM-INNER violated: a local class in a field initializer's lambda body is an
        // inner class
        @Immutable
        class Step {}
    };

    static {
        // IMM-INNER violated: a local class in a static initializer is an inner class
        // (JLS §8.1.3)
        @Immutable
        class Inventory {}
    }

    {
        // IMM-INNER violated: a local class in an instance initializer is an inner class
        @Immutable
        class Greeting {}
    }

    Store() {
        // IMM-INNER violated: a local class in a constructor is an inner class
        @Immutable
        class Setup {}
    }

    void open() {
        // IMM-INNER violated: a local class in an instance method is an inner class
        @Immutable
        class Visitor {}
    }

    static void restock() {
        // IMM-INNER violated: a local class in a static method is still an inner class
        // (JLS §8.1.3)
        @Immutable
        class Shipment {}
    }
}

@Immutable
class Mall {
    // IMM-INNER violated: a member class that is not static is an inner class, even in
    // an immutable class
    @Immutable
    class Kiosk {}
}

enum Department {
    TOYS;

    // IMM-INNER violated: a member class of an enum that is not static is an inner class
    @Immutable
    class Aisle {}
}

record Receipt(int total) {
    // IMM-INNER violated: a member class of a record that is not static is an inner
    // class
    @Immutable
    class Line {}
}
