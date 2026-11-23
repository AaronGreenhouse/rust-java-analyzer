package io.github.aarongreenhouse.testcases.immutable.marker.decoy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation with the same simple name as
 * {@link io.github.aarongreenhouse.promises.Immutable}, but a different fully qualified
 * name. The immutable analysis must not mistake it for the real annotation.
 */
@Target(ElementType.TYPE)
public @interface Immutable {}
