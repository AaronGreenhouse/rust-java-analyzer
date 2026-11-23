package io.github.aarongreenhouse.testcases.immutable.fieldtypes;

import io.github.aarongreenhouse.promises.Immutable;

class Notebook {
    int pages;
}

interface Writer {}

enum Ink { BLUE }

record Note(int id) {}

class Pouch<T> {}

class Drawers {
    static class Drawer {}
}

@Immutable
class Pen {}

// IMM-SUBCLASS violated: the subclass of immutable Pen is not marked
class FancyPen extends Pen {}

@Immutable
class Desk {
    // IMM-FIELD-TYPE violated: the class Notebook is not immutable
    final Notebook notebook = null;

    // IMM-FIELD-TYPE violated: the interface Writer is not immutable
    final Writer writer = null;

    // IMM-FIELD-TYPE violated: the enum Ink is not immutable, even though it declares no
    // fields other than its constants
    final Ink ink = Ink.BLUE;

    // IMM-FIELD-TYPE violated: the record Note is not immutable, even though its only
    // field is final and of a primitive type
    final Note note = null;

    // IMM-FIELD-TYPE violated: the generic class Pouch is not immutable, even though its
    // type argument is
    final Pouch<Pen> pouch = null;

    // IMM-FIELD-TYPE violated: the static nested class Drawers.Drawer is not immutable
    final Drawers.Drawer drawer = null;

    // IMM-FIELD-TYPE violated: the class FancyPen is not immutable, even though its
    // superclass Pen is
    final FancyPen fancyPen = null;

    // IMM-FIELD-TYPE violated: the class Notebook is not immutable, in a static field
    static final Notebook SHARED = null;
}

@Immutable
interface Office {
    // IMM-FIELD-TYPE violated: the class Notebook is not immutable, in an interface
    // constant
    Notebook LOG = null;
}

@Immutable
record Bookshelf(
        // IMM-FIELD-TYPE violated: the class Notebook is not immutable, in a record
        // component
        Notebook book) {}

@Immutable
enum Stationery {
    // IMM-FIELD-TYPE satisfied: an enum constant has the type of its enum, Stationery,
    // which is immutable
    PAPER;

    // IMM-FIELD-TYPE violated: the class Notebook is not immutable, in an enum
    final Notebook notebook = null;
}
