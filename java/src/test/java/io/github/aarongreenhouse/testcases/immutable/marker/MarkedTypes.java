package io.github.aarongreenhouse.testcases.immutable.marker;

import io.github.aarongreenhouse.promises.Immutable;

// IMM-MARK immutable: class annotated through a single-type import
@Immutable
class MarkedClass {}

// IMM-MARK immutable: abstract class annotated through a single-type import
@Immutable
abstract class MarkedAbstractClass {}

// IMM-MARK immutable: interface annotated through a single-type import
@Immutable
interface MarkedInterface {}

// IMM-MARK immutable: enum annotated through a single-type import
@Immutable
enum MarkedEnum { FIRST, SECOND }

// IMM-MARK immutable: record annotated through a single-type import
@Immutable
record MarkedRecord(int value) {}

// IMM-MARK immutable: annotated using the fully qualified name of the annotation
@io.github.aarongreenhouse.promises.Immutable
class MarkedByQualifiedName {}

// IMM-MARK immutable: the annotation comes after another modifier
final @Immutable class MarkedAfterModifier {}

class MarkedContainer {
    // IMM-MARK immutable: static nested class annotated through a single-type import
    @Immutable
    static class MarkedNested {}
}
