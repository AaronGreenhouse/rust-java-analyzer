package io.github.aarongreenhouse.testcases.immutable.crosspackage.lib;

class LooseChange
        // IMM-SUBCLASS violated: the subclass of immutable Amount, declared in another
        // file of the same package, is not marked
        extends Amount {}
