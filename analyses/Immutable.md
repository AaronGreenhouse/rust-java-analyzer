# Immutable analysis

## Summary

Assures that a type declaration is such that all instances of the type are immutable objects. That is, the value of an object of the type cannot change after it is created. Here the value of an object is the values of all its fields, including inherited ones, and, transitively, the values of the objects those fields refer to. We also consider the property to apply to the `static` portion of the type: its static fields cannot change after the type is initialized.

The property is about state, not behavior. A method of an immutable class may still read mutable state held elsewhere, such as a static field of another class, and so return different results over time. That does not make the object itself mutable.

This project is a demonstration. The rules aim to be as sound as possible while staying simple. Hard cases that would be research problems are listed under [Limitations](#limitations) and [Future work](#future-work) rather than handled.

## Description

This analysis is verified solely by looking at how types and fields are declared. No method bodies, constructors, or control flow are examined, except to find lambdas, method references, and anonymous classes (see [Anonymous classes, lambdas, and method references](#anonymous-classes-lambdas-and-method-references)).

The analysis is not purely syntactic in the strict sense. Checking a field's type or a class's supertypes requires resolving names (through imports, packages, and nested types) to their declarations, and knowing the type hierarchy. The implementation therefore needs a symbol table on top of the parser's AST.

In the rules, "type" means a class, interface, enum, or record declaration. Enums and records are classes (JLS §8.9, §8.10), so every rule for classes applies to them too.

### Scope

The analysis is limited to the analyzed source: the set of Java source files given to the analyzer.

- Rules that say "all subclasses" or "all implementing classes" mean those declared in the analyzed source. A subclass compiled separately, for example in another library, is not seen.
- A type whose declaration is not in the analyzed source is treated as not immutable. The exceptions are `java.lang.Object`, `java.lang.Enum`, and `java.lang.Record`, which are allowed as superclasses (see [Supertypes](#supertypes)). Library types that are known to be immutable, such as `String`, are a planned addition (see [Future work](#future-work)).

### The `@Immutable` annotation

The annotation is declared in the project's `java/` source tree:

- Its fully qualified name is `io.github.aarongreenhouse.promises.Immutable`.
- It is a declaration annotation with `@Target(ElementType.TYPE)`, so it can be placed on class, interface, enum, and record declarations.
- It has `@Retention(RetentionPolicy.CLASS)` and is `@Documented`, so it appears in generated Javadoc.
- It is deliberately not `@Inherited`. That meta-annotation only works through superclasses and has no effect through interfaces ([`java.lang.annotation.Inherited`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Inherited.html)). It would also hide the explicit marking that the [subtype rules](#subtypes) require.

The analyzer recognizes the annotation by its fully qualified name, `io.github.aarongreenhouse.promises.Immutable`, not just its simple name, so an unrelated annotation also called `Immutable` is not mistaken for it.

### Marker

Rule IMM-MARK: A type is considered immutable if and only if its declaration is annotated with `@Immutable`.

### Supertypes

Rule IMM-SUPERCLASS: If a class is immutable, its direct superclass must be one of:

- `java.lang.Object`
- `java.lang.Enum`, for an enum
- `java.lang.Record`, for a record
- a class that is declared immutable

It is an error if the direct superclass is anything else. Without this rule, an immutable class would inherit the mutable fields of its superclass:

```java
class Point { int x; }                        // not immutable
@Immutable class NamedPoint extends Point {   // error: superclass Point is not immutable
    final int id;
}
```

Rule IMM-SUPERINTERFACE: If an interface is immutable, each of its direct superinterfaces must be immutable. It is an error if one is not. Without this rule, a constant inherited from an unmarked superinterface, such as `int[] TABLE = {1, 2};`, would never have its type checked.

A class that is immutable does not need its superinterfaces to be immutable. Interfaces have no instance state, and the class's own fields are checked by the field rules below. Interface constants inherited by the class are a [limitation](#limitations).

Supertypes that satisfy these rules should be positively marked, stating which case they satisfy.

### Subtypes

Rule IMM-SUBINTERFACE: If an interface is immutable then all its subinterfaces and implementing classes must be immutable. It is an error if there exists a subinterface or implementing class that is not marked as immutable. Subinterfaces and implementing classes that are marked as immutable should receive a positive result that they satisfy the requirement.

Rule IMM-SUBCLASS: If a class is immutable then all its subclasses must be immutable. It is an error if there exists a subclass that is not marked as immutable. Subclasses that are marked as immutable should receive a positive result that they satisfy the requirement.

Both rules apply to the analyzed source only (see [Scope](#scope)).

Both rules cover indirect subtypes as well as direct ones, as the JLS does when it uses "subclass" and "subinterface" (JLS §8.1.4, §9.1.3). A class implements an interface if it, or any of its superclasses, has the interface as a direct or indirect superinterface (JLS §8.1.5). An unmarked indirect subtype is reported even when a type between it and the immutable type is also reported:

```java
@Immutable class Account {}
class Savings extends Account {}        // error: subclass of Account is not marked
class HighYield extends Savings {}      // error: indirect subclass of Account is not marked
```

Anonymous classes, including enum constants with a class body, are not reported under these rules. They are covered by IMM-ANONYMOUS instead (see [Anonymous classes, lambdas, and method references](#anonymous-classes-lambdas-and-method-references)).

A class that can have no subclasses trivially satisfies IMM-SUBCLASS and is positively marked as such. These are:

- a class declared `final`
- a record, which is implicitly final (JLS §8.10)
- an enum none of whose constants has a class body, which is implicitly final (JLS §8.9)

### Fields must be final

These rules apply to all declared fields, both `static` and instance fields.

A field may be final because it is declared with the `final` modifier, or because the Java Language Specification makes it implicitly final. The analysis must treat implicitly final fields as final, rather than looking only for the `final` keyword. The implicitly final fields are:

- fields declared in an interface, which are implicitly `public`, `static`, and `final` (JLS §9.3)
- the fields of a record that come from its record components, which are implicitly `private` and `final` (JLS §8.10.3)
- enum constants, which are implicitly `public`, `static`, and `final` (JLS §8.9.3)

Rule IMM-FINAL-INTERFACE: If an interface is immutable, then all its declared fields must be final. Note: this is trivially true by the requirements of the Java Language Specification. That is, it is impossible for this to not be true, but it is worth checking to be explicit.

Rule IMM-FINAL-CLASS: If a class is immutable, then all its declared fields must be final. It is an error for a field not to be final. For example, a record may declare a static field, and that field must still be declared `final`:

```java
@Immutable record Pair(int a, int b) {
    static int count;          // error: static field is not final
}
```

Fields that are final should be positively marked as satisfying the requirement, stating whether they are declared or implicitly final.

### Field types must be immutable

Rule IMM-FIELD-TYPE: If a type is immutable, the type of each declared field, static or instance, must be one of:

- a Java primitive type
- an interface that is declared immutable
- a class that is declared immutable, including an enum or record

It is an error if a field does not satisfy one of these cases. In particular:

- **Arrays are always an error.** Array elements can always be assigned (JLS §10), so an array type is never immutable, even when the field holding it is final.
- **Type variables are always an error.** A field whose type is a type variable, such as `final T value;`, is treated as having a mutable type, because the type argument might be mutable. A better approach is listed under [Future work](#future-work).
- **Types outside the analyzed source are an error**, including library types such as `String` (see [Scope](#scope)).

A parameterized type whose generic type is declared immutable, such as `Box<Foo>` for an immutable `Box<T>`, satisfies the rule whatever its type arguments are. Because of the type variable rule, `Box` cannot have a field of type `T`, so its type arguments cannot introduce mutable state.

An enum constant is a field whose type is its own enum, so the constants of an immutable enum satisfy this rule.

A field that does satisfy one of these cases should be positively marked as to which case it satisfies.

### Nested classes

Rule IMM-INNER: An inner class must not be immutable. It is an error for an inner class to be annotated `@Immutable`.

An inner class is a nested class that is not explicitly or implicitly `static` (JLS §8.1.3). This includes non-`static` member classes and local classes. An inner class has hidden state that does not appear among its declared fields: a reference to its enclosing instance, and, for a local class, the captured local variables. That hidden state can be mutable:

```java
class Counter {
    int count;
    @Immutable class View {              // error: an inner class cannot be immutable
        int get() { return count; }
    }
}
```

Nested enums, records, and interfaces are implicitly `static` (JLS §8.9, §8.10, §9.1.1.3), as are member classes of interfaces (JLS §9.5), so they may be immutable. Local enums, records, and interfaces are also implicitly `static` (JLS §14.3).

A `static` nested class declared inside an immutable type is not part of that type's state, so it does not need to be immutable itself.

Since an inner class cannot be immutable, an inner class that extends an immutable class or implements an immutable interface is always an error. If it is not marked, the error is under IMM-SUBCLASS or IMM-SUBINTERFACE. If it is marked, it satisfies those rules, and the error is under IMM-INNER.

### Anonymous classes, lambdas, and method references

Anonymous classes, lambdas, and method references create objects whose classes have no declaration that can be annotated (JLS §15.9.5, §15.27, §15.13). They can also capture mutable state:

```java
@Immutable interface Source { int next(); }

int[] state = {0};
Source s = () -> state[0]++;   // error: lambda of immutable type Source
```

Rule IMM-ANONYMOUS: It is an error for an anonymous class to extend an immutable class or implement an immutable interface.

Checking only the class or interface named in the class instance creation expression is enough. If that type is a subtype of an immutable type, then it must itself be immutable by IMM-SUBCLASS or IMM-SUBINTERFACE, so the anonymous class is still caught.

An enum constant with a class body is an anonymous class whose superclass is the enum (JLS §8.9.1). So in an immutable enum, a constant with a body is an error under this rule:

```java
@Immutable enum Op {
    PLUS { int apply(int a, int b) { return a + b; } };   // error: constant with a body
    abstract int apply(int a, int b);
}
```

Rule IMM-LAMBDA: It is an error for a lambda expression or method reference to have an immutable interface as its target type.

Implementation note: IMM-LAMBDA should be implemented by checking declarations instead of lambdas and method references. A lambda or method reference can only have a functional interface as its target type (JLS §15.27.3, §15.13.2). So it is enough, and still sound, to report an error for any immutable interface that is a functional interface, at the interface's declaration. A functional interface is one that is not declared `sealed` and has exactly one abstract method, counting abstract methods inherited from superinterfaces (JLS §9.8). Default and static methods do not count, nor do abstract methods that match a public method of `java.lang.Object`, such as `equals`. A sealed immutable interface therefore satisfies IMM-LAMBDA even if it has exactly one abstract method:

```java
@Immutable sealed interface Expr permits Literal {   // OK: a sealed interface is not functional
    int eval();
}
```

This is simpler because the target type of a particular lambda or method reference comes from its context, and finding it in general requires overload resolution and type inference. The cost is that it is stricter than needed: an immutable functional interface is reported even if no lambda or method reference ever targets it. Checking each lambda and method reference precisely is listed under [Future work](#future-work).

### Results

The analysis reports two kinds of result:

- an **error**, when a rule is violated
- a **positive result**, when a rule is satisfied, stating which case applies where a rule has more than one

Each result gives the source location (file, line, and column), the rule identifier (such as `IMM-FIELD-TYPE`), and a message.

## Limitations

The analysis does not handle these cases. They are noted here so that the guarantee is clear.

- **Code outside the analyzed source.** A subclass or implementing class declared outside the analyzed source is not checked, and may be mutable.
- **`this` escaping during construction.** If a constructor leaks `this` before its final fields are assigned, other code can see the fields change from their default values (JLS §17.5).
- **Changing final fields by other means.** Reflection (`Field.setAccessible`), `sun.misc.Unsafe`, deserialization, and native code can all change final fields (JLS §17.5.3).
- **Interface constants inherited by an immutable class.** An immutable class may implement an interface that is not immutable, and that interface's constants are not type-checked.

## Future work

- **Known immutable library types.** Add a list of library types that are trusted to be immutable, such as `String` and the boxed primitive types (`Integer`, `Long`, and so on). Error Prone's checker does this ([errorprone.info/bugpattern/Immutable](https://errorprone.info/bugpattern/Immutable)). Some types need care: `BigInteger` and `BigDecimal` are not `final`, so a mutable subclass could be passed in their place (*Effective Java*, 3rd ed., Item 17).
- **Type variables.** Allow fields whose type is a type variable, with the immutability of the type argument checked where the generic type is used. Error Prone handles this with `@Immutable(containerOf = "T")`.
- **Precise checking of lambdas and method references.** Find the target type of each lambda and method reference, and report only those whose target type is immutable. This would allow immutable functional interfaces that are never implemented by a lambda or method reference.

## Related work

- Brian Goetz et al., *Java Concurrency in Practice* (2006), defines an `@Immutable` annotation that is documentation only and is not checked.
- Joshua Bloch, *Effective Java*, 3rd ed. (2018), Item 17, "Minimize mutability", allows an immutable class to have private mutable fields, protected by defensive copies. A declaration-only analysis cannot check that, so the rules here are stricter: every field must be final and of an immutable type.
- Error Prone's `@Immutable` checker ([errorprone.info/bugpattern/Immutable](https://errorprone.info/bugpattern/Immutable)) is close to this design, and also handles the cases listed under [Future work](#future-work).
