package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;
import java.util.List;

// Types whose declarations are outside the analyzed source are treated as not immutable,
// even library types that are immutable in practice. A list of known immutable library
// types is future work.

@Immutable
class Profile {
    // IMM-FIELD-TYPE violated: java.lang.String is outside the analyzed source
    final String name = "";

    // IMM-FIELD-TYPE violated: java.lang.Integer, a boxed primitive type, is outside
    // the analyzed source
    final Integer age = 0;

    // IMM-FIELD-TYPE violated: java.lang.Object is outside the analyzed source
    final Object tag = null;

    // IMM-FIELD-TYPE violated: java.lang.Runnable, an interface, is outside the analyzed
    // source
    final Runnable action = null;

    // IMM-FIELD-TYPE violated: java.util.List, a generic interface, is outside the
    // analyzed source
    final List<Profile> friends = null;

    // IMM-FIELD-TYPE violated: java.time.DayOfWeek, an enum named by its fully
    // qualified name, is outside the analyzed source
    final java.time.DayOfWeek birthday = null;

    // IMM-FIELD-TYPE violated: java.lang.Enum is allowed as a superclass, but not as a
    // field type, and is outside the analyzed source
    final Enum<?> anyEnum = null;

    // IMM-FIELD-TYPE violated: java.lang.Record is allowed as a superclass, but not as a
    // field type, and is outside the analyzed source
    final Record anyRecord = null;

    // IMM-FIELD-TYPE violated: java.lang.String is outside the analyzed source, in a
    // static field
    static final String PREFIX = "profile";
}
