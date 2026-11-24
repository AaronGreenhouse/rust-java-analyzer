package io.github.aarongreenhouse.testcases.immutable.anonymous;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
interface Badge {}

@Immutable
interface VipBadge
        extends Badge {}

@Immutable
abstract class Ticket {
    abstract int price();
}

@Immutable
class Stamp {}

@Immutable
class Seat {
    final int row;

    Seat(int row) {
        this.row = row;
    }
}

@Immutable
class Crate<T> {}

// IMM-SUBCLASS violated: the subclass of immutable Stamp is not marked
class PostageStamp extends Stamp {}

class Venue {
    // IMM-ANONYMOUS violated: the anonymous class implements the immutable interface
    // Badge
    final Badge badge = new Badge() {};

    // IMM-ANONYMOUS violated: the anonymous class implements the immutable interface
    // VipBadge
    final VipBadge vipBadge = new VipBadge() {};

    // IMM-ANONYMOUS violated: the anonymous class extends the immutable abstract class
    // Ticket
    final Ticket ticket = new Ticket() {
        @Override
        int price() {
            return 1;
        }
    };

    // IMM-ANONYMOUS violated: the anonymous class extends the immutable class Stamp
    final Stamp stamp = new Stamp() {};

    // IMM-ANONYMOUS violated: the anonymous class, created with constructor arguments,
    // extends the immutable class Seat
    final Seat seat = new Seat(3) {};

    // IMM-ANONYMOUS violated: the anonymous class extends the immutable generic class
    // Crate, with an explicit type argument
    final Crate<Venue> crate = new Crate<Venue>() {};

    // IMM-ANONYMOUS violated: the anonymous class extends the immutable generic class
    // Crate, with an inferred type argument
    final Crate<Venue> inferredCrate = new Crate<>() {};

    // IMM-ANONYMOUS violated: the anonymous class, in a static field initializer,
    // implements the immutable interface Badge
    static final Badge STATIC_BADGE = new Badge() {};

    // IMM-ANONYMOUS not applicable: the class named here, PostageStamp, is not
    // immutable. The anonymous class is still a subclass of immutable Stamp, but the
    // error is reported at PostageStamp by IMM-SUBCLASS.
    final PostageStamp postage = new PostageStamp() {};

    static {
        // IMM-ANONYMOUS violated: the anonymous class, in a static initializer,
        // implements the immutable interface Badge
        Badge badge = new Badge() {};
        badge.hashCode();
    }

    void open() {
        // IMM-ANONYMOUS violated: the anonymous class, in a method body, implements the
        // immutable interface Badge
        Badge local = new Badge() {};
        local.hashCode();

        Runnable action = () -> {
            // IMM-ANONYMOUS violated: the anonymous class, in a lambda body, implements
            // the immutable interface Badge
            Badge inLambda = new Badge() {};
            inLambda.hashCode();
        };
        action.run();

        Object outer = new Object() {
            // IMM-ANONYMOUS violated: the anonymous class, in the body of another
            // anonymous class, implements the immutable interface Badge
            final Badge inner = new Badge() {};
        };
        outer.hashCode();
    }
}
