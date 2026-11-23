package io.github.aarongreenhouse.testcases.immutable.marker.decoy;

import io.github.aarongreenhouse.promises.*;

// IMM-MARK not immutable: the simple name Immutable resolves to the decoy annotation
// declared in this package, which shadows the type-import-on-demand of
// io.github.aarongreenhouse.promises (JLS §6.4.1)
@Immutable
class ShadowedOnDemand {
    // IMM-FINAL-CLASS not applicable: ShadowedOnDemand is not immutable
    int count;
}
