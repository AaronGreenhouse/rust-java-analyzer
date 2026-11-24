package io.github.aarongreenhouse.testcases.immutable.crosspackage.app;

import io.github.aarongreenhouse.promises.Immutable;
import io.github.aarongreenhouse.testcases.immutable.crosspackage.lib.Amount;
import io.github.aarongreenhouse.testcases.immutable.crosspackage.lib.Figure;
import io.github.aarongreenhouse.testcases.immutable.crosspackage.lib.Ledger;
import io.github.aarongreenhouse.testcases.immutable.crosspackage.lib.Units.Meter;

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class Price
        // IMM-SUPERCLASS satisfied: the superclass Amount, imported from another
        // package, is immutable
        // IMM-SUBCLASS satisfied: the subclass of immutable Amount, imported from another
        // package, is immutable
        extends Amount {
    // IMM-FIELD-TYPE satisfied: Amount, imported from another package, is immutable
    final Amount base = null;

    // IMM-FIELD-TYPE satisfied: Figure, an interface imported from another package, is
    // immutable
    final Figure figure = null;

    // IMM-FIELD-TYPE satisfied: Meter, a nested class imported from another package,
    // is immutable
    final Meter length = null;

    // IMM-FIELD-TYPE violated: Ledger, imported from another package, is not immutable
    final Ledger ledger = null;
}

class Refund
        // IMM-SUBCLASS violated: the subclass of immutable Amount, imported from another
        // package, is not marked
        extends Amount {}

@Immutable
class Audit
        // IMM-SUPERCLASS violated: the superclass Ledger, imported from another package,
        // is not immutable
        extends Ledger {}

@Immutable
interface Outline
        // IMM-SUPERINTERFACE satisfied: Figure, imported from another package, is
        // immutable
        // IMM-SUBINTERFACE satisfied: the subinterface of immutable Figure, imported from
        // another package, is immutable
        extends Figure {}

class Sketch
        // IMM-SUBINTERFACE violated: the implementing class of immutable Figure, imported
        // from another package, is not marked
        implements Figure {}
