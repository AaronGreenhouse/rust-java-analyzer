package io.github.aarongreenhouse.testcases.immutable.subtypes;

import io.github.aarongreenhouse.promises.Immutable;

@Immutable
class Account {}

@Immutable
abstract class Instrument {}

@Immutable
class Envelope<T> {}

class SavingsAccount
        // IMM-SUBCLASS violated: the subclass of immutable Account is not marked
        extends Account {
    int balance;
}

@Immutable
class CheckingAccount
        // IMM-SUBCLASS satisfied: the subclass of immutable Account is immutable
        extends Account {}

class BusinessChecking
        // IMM-SUBCLASS violated: the subclass of immutable CheckingAccount, and through
        // it of immutable Account, is not marked
        extends CheckingAccount {}

class HighYieldSavings
        // IMM-SUBCLASS violated: the indirect subclass of immutable Account, through
        // SavingsAccount, is not marked. SavingsAccount is reported too.
        extends SavingsAccount {}

class Bond
        // IMM-SUBCLASS violated: the subclass of immutable abstract class Instrument is
        // not marked
        extends Instrument {}

class Bank {
    static class Vault
            // IMM-SUBCLASS violated: the static nested subclass of immutable Account is
            // not marked
            extends Account {}

    class Teller
            // IMM-SUBCLASS violated: the inner subclass of immutable Account is not
            // marked
            extends Account {}

    // IMM-INNER violated: an inner class is marked immutable
    @Immutable
    class Manager
            // IMM-SUBCLASS satisfied: the inner subclass of immutable Account is marked.
            // The error for it is reported by IMM-INNER.
            extends Account {}

    void audit() {
        class Entry
                // IMM-SUBCLASS violated: the local subclass of immutable Account is not
                // marked. Marking it is not possible because of IMM-INNER.
                extends Account {}
    }
}

@Immutable
sealed class Payment permits Cash, Card {}

// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses
@Immutable
final class Cash
        // IMM-SUBCLASS satisfied: the permitted subclass of immutable Payment is
        // immutable
        extends Payment {}

non-sealed class Card
        // IMM-SUBCLASS violated: the non-sealed permitted subclass of immutable Payment
        // is not marked
        extends Payment {}

class LetterEnvelope
        // IMM-SUBCLASS violated: the subclass of the immutable generic class Envelope is
        // not marked
        extends Envelope<Integer> {}
