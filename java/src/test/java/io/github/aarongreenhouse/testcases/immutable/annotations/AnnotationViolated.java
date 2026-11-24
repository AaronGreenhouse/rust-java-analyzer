package io.github.aarongreenhouse.testcases.immutable.annotations;

import io.github.aarongreenhouse.promises.Immutable;
import java.lang.annotation.Annotation;

// IMM-ANNOTATION violated: an annotation interface cannot be immutable
@Immutable
@interface Marked {}

// IMM-ANNOTATION violated: an annotation interface with elements cannot be immutable
@Immutable
@interface Labeled {
    String value();

    int[] codes() default {};

    // IMM-FIELD-TYPE not applicable: no other rules are checked for an annotation
    // interface
    int[] TABLE = {1, 2};
}

class Holder {
    // IMM-ANNOTATION violated: a nested annotation interface cannot be immutable
    @Immutable
    @interface Nested {}
}

// IMM-ANNOTATION not applicable: the annotation interface is not marked
@interface Unmarked {
    int[] TABLE = {1, 2};
}

class MarkedImpl
        // IMM-SUBINTERFACE not applicable: Marked is an annotation interface, which is
        // never immutable
        implements Marked {
    @Override
    public Class<? extends Annotation> annotationType() {
        return Marked.class;
    }
}

@Immutable
final class UsesMarked {
    // IMM-FIELD-TYPE violated: the annotation interface Marked is never immutable, even
    // though it is marked
    final Marked marked = null;
}
