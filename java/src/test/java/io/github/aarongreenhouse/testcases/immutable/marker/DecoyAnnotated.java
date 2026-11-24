package io.github.aarongreenhouse.testcases.immutable.marker;

import io.github.aarongreenhouse.testcases.immutable.marker.decoy.Immutable;

// In this file, the simple name Immutable refers to the decoy annotation
// io.github.aarongreenhouse.testcases.immutable.marker.decoy.Immutable.

// IMM-MARK not immutable: annotated with the decoy annotation, imported through a
// single-type import
@Immutable
class DecoyBySingleTypeImport {
    // IMM-FINAL-CLASS not applicable: DecoyBySingleTypeImport is not immutable
    int count;
}

// IMM-MARK not immutable: annotated with the decoy annotation, using its fully
// qualified name
@io.github.aarongreenhouse.testcases.immutable.marker.decoy.Immutable
class DecoyByQualifiedName {
    // IMM-FINAL-CLASS not applicable: DecoyByQualifiedName is not immutable
    int count;
}

// IMM-MARK immutable: annotated with the real annotation using its fully qualified name,
// even though the simple name Immutable refers to the decoy in this file
@io.github.aarongreenhouse.promises.Immutable
class RealByQualifiedName {}

// IMM-MARK not immutable: annotated with the decoy annotation
@Immutable
class DecoySubclass
        // IMM-SUBCLASS violated: the subclass of immutable RealByQualifiedName is marked
        // only with the decoy annotation
        extends RealByQualifiedName {}
