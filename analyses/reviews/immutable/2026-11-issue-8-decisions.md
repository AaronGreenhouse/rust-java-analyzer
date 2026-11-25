# Immutable analysis: working decisions

> **Based on:** the [spec review](2026-11-issue-8-spec-review.md) of [`analyses/Immutable.md`](https://github.com/AaronGreenhouse/rust-java-analyzer/blob/90f737b2f413bd9de3702ea99e2391cf6811c33f/analyses/Immutable.md) at commit [90f737b](https://github.com/AaronGreenhouse/rust-java-analyzer/tree/90f737b2f413bd9de3702ea99e2391cf6811c33f).
>
> **Issue:** #8 · **Status:** in progress. This document is updated as decisions are made. Once they are applied to the spec and the tests, it will say in which commit, and it will not be edited after that.

A record of what we decide while working through the spec review. Nothing here is in the spec or the tests yet.

Guiding principle: this is a toy project. Choose the simplest fix that keeps the analysis as sound as possible, even when that makes it stricter than it needs to be.

Sections and numbers follow the review, so decision 1.2 answers review item 1.2. Review items with no decision yet are listed under [Open](#open).

**Progress:** review items 1.1 to 1.9 are processed. Next is review item 1.10, interface constants. Items 1.10 onward are not processed yet, even where the Open list already has a proposal for them.

Review bullets that need no decision are not recorded below. In 1.3 and 1.4, those bullets only confirm that the spec covers something. One exception: the fifth bullet of 1.4, which suggests calling an enum with constant bodies "implicitly sealed". We left the spec's wording as it is, because 1.5a gives sealed classes no positive result.

## 1. Missing corner cases

### 1.1 Lambdas and method references with intersection target types

**Rule:** IMM-LAMBDA. At most one abstract method is an error.

**Problem.** A cast can give a lambda or method reference an intersection target type, such as `(Runnable & Tag) () -> n[0]++`. The object is then a `Tag`. If `Tag` is an immutable interface with no abstract methods of its own, the current check at the interface's declaration passes, so nothing is reported. That makes the spec's claim that the declaration check is "enough, and still sound" false.

**Decision.** Make the declaration check stricter rather than check casts:

> Rule IMM-LAMBDA: It is an error for an immutable interface that is not `sealed` to have at most one abstract method. Abstract methods are counted as for a functional interface (JLS §9.8): they include methods inherited from superinterfaces, and they don't include default methods, static methods, or abstract methods that match a public method of `java.lang.Object`.

**Why it is sound.** In an intersection target, the abstract methods of all the components together must reduce to exactly one. So an immutable component of the intersection has at most one abstract method of its own, and the new rule reports it. An interface with exactly one abstract method is a functional interface, so the direct case is still caught too.

**Why it is simple.** It is still checked only at declarations, with the same method counting as before. No casts, target typing, or type inference are needed.

**Cost.** Immutable marker interfaces (no abstract methods) are no longer allowed. We accept this because they aren't useful for an immutable type anyway.

**Sealed interfaces are exempt.** The "not `sealed`" part of the rule allows an immutable sealed interface with at most one abstract method, such as `Expr` with only `eval()`. This is sound because a lambda or method reference can never be an instance of a sealed interface: its class can't be named in the `permits` clause. Checked with javac 26: `(Runnable & Tag) () -> {}` compiles when `Tag` is an ordinary interface, but is rejected when `Tag` is sealed, whether `Tag` has zero or one abstract methods and in either order in the cast.

**Effect on the tests.**

- `anonymous/LambdaSatisfied.java`: `Marker` (no abstract methods), `Defaults` (only default and static methods), and `Identity` (only `Object` methods) become IMM-LAMBDA violations. Move them to `LambdaViolated.java`, and add a case for the intersection cast.
- Many test files use empty `@Immutable` interfaces as fixtures. Every error is marked, so each one would need an IMM-LAMBDA error comment. It is simpler to give each fixture two abstract methods. They include:
  - `Badge` in `anonymous/AnonymousViolated.java`.
  - `ImmutableParent`, `OtherImmutableParent`, `ImmutableGenericParent`, `ImmutableNestedParent`, and `NoSuperinterfaces` in `supertypes/SuperinterfaceSatisfied.java`.
  - `ImmutableSibling` in `supertypes/SuperinterfaceViolated.java`.
  - `Shape` and `Container` in `subtypes/SubinterfaceSatisfied.java`.
  - `Animal` and `Bag` in `subtypes/SubinterfaceViolated.java`.
  - `MarkedInterface` in `marker/MarkedTypes.java`.
  - `Lendable` and `Rule` in `nested/InnerSatisfied.java`.
  - `Color` in `fieldtypes/FieldTypeImmutableTypes.java`.
  - `Figure` in `crosspackage/lib/`.

  Any other interface with one abstract method would need the same treatment. Annotation interfaces are not affected, because only IMM-ANNOTATION applies to them.

### 1.2 `java.lang.reflect.Proxy`

**Rule:** Limitations. Classes created at run time are not checked.

**Problem.** `Proxy.newProxyInstance(loader, new Class<?>[] {Badge.class}, handler)` creates an instance of an immutable interface without any declaration in the source. Its behavior, and in effect its state, lives in an `InvocationHandler`, which may be mutable. Limitations mentions reflection only as a way to change final fields, not as a way to implement an immutable type.

**Decision.** Don't check it. Add a bullet to Limitations in `analyses/Immutable.md`, after "Changing final fields by other means":

> - **Classes created at run time.** `java.lang.reflect.Proxy`, and other classes generated at run time, such as by bytecode libraries, can implement an immutable interface or extend an immutable class without a declaration in the analyzed source. Such a class is not checked, and may be mutable. For example, a proxy's state lives in its `InvocationHandler`.

**Why.** Finding `Proxy` calls would need expression analysis, and it still couldn't catch generated bytecode. This is the same kind of hole as the reflection and native-code bullet that is already there, so listing it keeps the guarantee honest at no cost.

**Effect on the tests.** None. Limitations are not tested.

### 1.3 Records

The review raised two points about records. Both are decided.

#### 1.3a Where a record component's result goes

**Rule:** IMM-FINAL-CLASS and IMM-FIELD-TYPE. A record component's result goes on the component.

**Problem.** A record's component fields have no field declaration in the source. The spec doesn't say where their IMM-FINAL-CLASS and IMM-FIELD-TYPE results go. Only the test conventions in `package-info.java` mention record components as a place for a result.

**Decision.** Report the result on the record component in the record header. The component stands in for the field declaration. Add this paragraph to `analyses/Immutable.md`, after the paragraph on fields in enum constant bodies:

> A field that comes from a record component has no field declaration in the source. Its results, for IMM-FINAL-CLASS and IMM-FIELD-TYPE, are reported on the record component in the record header, which stands in for the field declaration.

**Why.** The component is the only place in the source that declares the field, and it has the field's name and type.

**Effect on the tests.** None. The tests already put these results on the components, for example `Reading` in `finalfields/FinalClassSatisfied.java`.

#### 1.3b Annotations within a type

**Rule:** IMM-FIELD-TYPE. Annotations within a type are ignored. The review raised this under records, but it applies to the type of every field and record component.

**Problem.** Since Java 8, type annotations (`@Target(ElementType.TYPE_USE)`) can appear inside a type, not only before a declaration (JLS §9.7.4). The type that IMM-FIELD-TYPE resolves can then contain annotations:

```java
record Payment(@NonNull Money amount) {}    // before a record component's type
final java.lang.@NonNull String name;       // inside a qualified name
final Map.@NonNull Entry<K, V> entry;       // inside a nested type name
final List<@NonNull Money> items;           // inside a type argument
final int @NonNull [] counts;               // on an array dimension
```

`java.lang.@NonNull String` is not a plain dotted name, so a resolver that expects `a.b.C` won't match it. The spec doesn't say what to do.

**Decision.** Add this sentence to the IMM-FIELD-TYPE section of `analyses/Immutable.md`, after the "In particular" list:

> Annotations within a type, such as `java.lang.@NonNull String` or `List<@NonNull Foo>`, are ignored when the type is resolved. They do not affect whether the type is immutable.

**Why.** It is a resolution detail, not a change to any rule. `@Immutable` can only be placed on a type declaration, so writing it inside a type doesn't compile, and it can't make a type immutable that way. A resolver that mishandled these annotations would most likely fail to resolve the type and report "not immutable", which is safe. But that would be a false error, so the spec should rule it out.

**Effect on the tests (mandatory).** Add `fieldtypes/FieldTypeTypeAnnotations.java`. It declares its own unmarked `@Target(ElementType.TYPE_USE)` annotation, `@Tagged`, plus immutable and mutable fixture types. Every result is marked, so that the analyzer is shown to give the same answer as for the bare type. It covers:

- **Satisfied, immutable class:**
  - `@Tagged` in modifier position: `@Tagged final Money a;`.
  - `@Tagged` before the type: `final @Tagged Money b;`.
  - Inside a fully qualified name: `final io.github.aarongreenhouse.testcases.immutable.fieldtypes.@Tagged Money c;`.
  - Inside a nested type name: `final Holder.@Tagged Inner d;`.
- **Satisfied, other cases:**
  - Inside a type argument of an immutable generic type: `final Box<@Tagged Wallet> e;`. The type argument is mutable, but that doesn't matter.
  - On a primitive: `final @Tagged int f;`.
  - On a record component: `record Payment(@Tagged Money amount)`.
- **Violated, the annotation does not hide the type:**
  - On a mutable source type: `final @Tagged Wallet g;`.
  - On a library type: `final java.lang.@Tagged String h;`.
  - On an array dimension: `final int @Tagged [] i;`, and on an array's element type: `final @Tagged Money[] j;`.

Also add a sentence to `fieldtypes/package-info.java`: "Annotations within a type are ignored."

### 1.4 Enums

The review raised two gaps about enums, 1.4a and 1.4b, and proposed one test, 1.4c. All three are decided.

#### 1.4a The fields of `java.lang.Enum`, and trusted library types

**Rule:** IMM-FIELD-TYPE and Scope. A short list of library types is trusted to be immutable.

**Problem.** IMM-SUPERCLASS allows `java.lang.Enum` as a superclass, but `Enum` declares `private final String name`. Since `String` is outside the analyzed source, the field-type rule calls it not immutable. So trusting `Enum` already assumes that `String` is immutable, while a `String` field in an immutable class is an error. That is inconsistent, and it makes almost no realistic immutable class pass. The spec lists trusted library types as future work.

**Decision.** Trust a hard-coded list of library types as immutable. A field of one of these types satisfies IMM-FIELD-TYPE:

- `java.lang.String`
- the boxed primitive types: `java.lang.Boolean`, `Byte`, `Character`, `Short`, `Integer`, `Long`, `Float`, and `Double`

A type is on the list only if it is `final` and immutable as observed through its API. It must be `final` because the analysis can't see library subclasses, so a non-final type could be replaced by a mutable subclass. "As observed through its API" covers `String`, which caches its hash code in a field it sets later, a change that no caller can see.

These are not on the list:

- **`BigInteger` and `BigDecimal`:** they are not `final`.
- **`Optional`, `List.of(...)` and other containers:** they are only as immutable as their contents.
- **`java.time` types:** they qualify, but are left out to keep the list short. They can be added later.

Changes to `analyses/Immutable.md`:

- **Scope:** replace the last sentence of the bullet on types outside the analyzed source ("Library types that are known to be immutable, such as `String`, are a planned addition") with: "The exceptions are also the trusted library types listed under [Field types must be immutable](#field-types-must-be-immutable). `Object`, `Enum`, and `Record` are trusted, too: as superclasses, their own fields are not checked."
- **IMM-FIELD-TYPE:** add a case to the list, "a trusted library type: `java.lang.String` or a boxed primitive type", and add a paragraph giving the list and the two conditions above.
- **Future work:** reduce the "Known immutable library types" item to extending the list, for example with `java.time` types.

**Why.** It is consistent with trusting `Enum`, and it makes the analysis usable on ordinary classes. It is simple: after name resolution, the check compares a fully qualified name against a fixed list. The resolver already tells `java.lang.String` apart from a source class named `String`. Requiring `final` keeps it sound.

**Effect on the tests.**

- Add `fieldtypes/FieldTypeTrustedTypes.java`. It marks a field of each trusted type as satisfied, including a static `String` field and a fully qualified `java.lang.String`. It also includes near-misses marked as violated: `BigInteger`, `BigDecimal`, and `Optional<String>`. And it covers a source class named `String` that is not immutable: a field of that type is violated, because it does not resolve to `java.lang.String`.
- `fieldtypes/FieldTypeOutsideSource.java`: move the `String` and `Integer` fields (`name`, `age`, and `PREFIX`) to the new file. Change the comment at the top of the file to say that types outside the analyzed source are not immutable unless they are trusted.
- No other tests change. No other test file declares a field of a trusted type. `String` and `Integer` appear elsewhere only as method types and type arguments.

#### 1.4b Fields generated by the compiler

**Rule:** IMM-FINAL-CLASS and IMM-FIELD-TYPE, and Limitations. Only fields in the source are checked.

**Problem.** javac adds fields that are not in the source. Every enum gets `private static final E[] $VALUES`, which backs `values()`. Inner, local, and anonymous classes get fields such as `this$0`, for the enclosing instance, and `val$x`, for each captured local variable. `$VALUES` is a mutable array in the enum's static state, which seems to contradict the Summary's claim that an immutable type's static fields cannot change. The spec doesn't say whether these fields count.

**Decision.** Only fields that appear in the source are checked: declared fields, the fields of record components, and enum constants. Compiler-generated fields are ignored. Two changes to `analyses/Immutable.md`:

- **Fields must be final:** after "These rules apply to all declared fields, both `static` and instance fields", add: "Only fields that appear in the source are checked: declared fields, the fields that come from record components, and enum constants. Fields that the compiler adds, such as an enum's `$VALUES` array or an inner class's `this$0`, are not checked."
- **Limitations:** add a bullet:

  > - **Fields generated by the compiler.** The compiler adds fields that are not in the source, such as the `$VALUES` array behind an enum's `values()` method. They are not checked. Source code cannot refer to them, and `values()` returns a copy of the array, so they can be changed only by the means listed under "Changing final fields by other means".

**Why it is sound.** No code in the source can reach these fields:

- **`$VALUES`:** source code can't refer to it. Checked with javac 26: `$VALUES[0] = B;` inside an enum is rejected with "cannot find symbol". `values()` returns a copy, so the array itself never escapes. Only reflection or native code can change it, which Limitations already covers.
- **`this$0` and `val$x`:** they exist only in inner, local, and anonymous classes. None of these can be immutable: IMM-INNER rejects a marked inner or local class, and IMM-ANONYMOUS rejects an anonymous class of an immutable type. So their fields never need checking.

**Why it is simple.** The analyzer works on source only, so these fields are already invisible to it. The decision only writes down what it already does.

**Effect on the tests.** None. There is nothing in the source to mark.

#### 1.4c Static fields in enum constant bodies

**Rule:** IMM-FINAL-CLASS and IMM-FIELD-TYPE. Tests only.

**Problem.** Since Java 16, an enum constant's class body can declare `static` fields (JEP 395). Checked with javac: `RED { static int hits; }` compiles at release 26, the project's release, and is rejected at release 15. The spec already covers these fields: the field rules apply to "all declared fields, both `static` and instance", and fields in a constant body are "checked as if they were declared in the enum itself". But the tests have only instance fields in constant bodies. An analyzer that skipped static fields in an anonymous class body would pass every test.

**Decision.** No spec change. Add a static field that passes and one that fails to each constant-body test, and one static field marked not applicable to each non-immutable enum:

- **`finalfields/FinalEnumConstantBody.java`:**
  - In `RED`: `static int hits;` is IMM-FINAL-CLASS violated, and `static final int MAX = 5;` is IMM-FINAL-CLASS satisfied (declared final).
  - In `ON` of `Lamp`: `static int count;` is IMM-FINAL-CLASS not applicable.
- **`fieldtypes/FieldTypeEnumConstantBody.java`:**
  - In `HIGH`: `static final int[] LEVELS = {1, 2};` is IMM-FIELD-TYPE violated (array type), and `static final int STEP = 1;` is IMM-FIELD-TYPE satisfied (primitive type).
  - In `LOUD` of `Volume`: `static final int[] RANGE = {1, 2};` is IMM-FIELD-TYPE not applicable.

**Why.** It is cheap, and it covers a case that is easy to get wrong. A constant body is an anonymous class, and some parsers and analyzers handle the static members of an anonymous class differently from those of a named class.

### 1.5 Sealed hierarchies

The review raised two gaps about sealed hierarchies. Both are decided. Its other bullets confirm that the spec and the tests already cover IMM-LAMBDA for sealed interfaces, and implicit `permits` (`Token`).

#### 1.5a No closed-world result for sealed classes

**Rule:** IMM-SUBCLASS. A sealed class gets no "trivially satisfied" result.

**Problem.** Suppose every permitted subclass of a sealed class is in the source and is `final`, sealed, or a record. Then the hierarchy is closed, like a `final` class. The review suggests giving such a class the "trivially satisfied" IMM-SUBCLASS positive that `final` classes get. The spec currently says nothing about sealed classes here.

**Decision.** Don't treat sealed hierarchies as closed. Add this paragraph to `analyses/Immutable.md`, after the list of classes that trivially satisfy IMM-SUBCLASS:

> A `sealed` class is not in this list. It receives no IMM-SUBCLASS result of its own, and each of its permitted subclasses is checked by IMM-SUBCLASS like any other subclass.

**Why.**

- **Soundness doesn't change.** Each permitted subclass is already checked by IMM-SUBCLASS, so every error is caught either way. The positive would only make the report nicer.
- **It is simpler.** The analyzer doesn't have to walk the permitted subclasses, follow nested `sealed` levels, or stop at a `non-sealed` class.
- **It avoids a misleading result.** Without a module, a permitted subclass must be in the same package, but it can still come from a class file compiled separately. A "closed" positive would claim something the analysis didn't check.

**Effect on the tests.** None. `Payment` in `subtypes/SubclassViolated.java` already gets no IMM-SUBCLASS result of its own.

#### 1.5b The `permits` clause gets no result

**Rule:** IMM-SUBCLASS and IMM-SUBINTERFACE. A result for a permitted subtype goes on the subtype's own clause.

**Problem.** When a `permits` clause names a subtype that is not marked, the spec doesn't say whether the error goes on the `permits` clause, on the subtype, or on both. The tests put it only on the subtype's `extends` or `implements` clause. That already follows the spec's general rule for where subtype results go, but the spec doesn't say so for `permits`.

**Decision.** Add this sentence to the paragraph in `analyses/Immutable.md` on where subtype results are reported, the one that begins "Each result is reported on the subtype's `extends` or `implements` clause":

> The `permits` clause of a sealed type receives no result. An unmarked permitted subtype is reported on its own `extends` or `implements` clause, like any other subtype.

**Why.** It reuses the existing placement rule, so each error is reported once, in one place.

**Effect on the tests.** None. `Card` in `subtypes/SubclassViolated.java` and `Feathers` in `subtypes/SubinterfaceViolated.java` already follow it.

### 1.6 Generics

The review raised two points about generics. 1.6b is decided, and 1.6a is deferred to 2.4. Its other bullets confirm that the spec and the tests already cover type variables, and parameterized, wildcard, raw, and intersection types.

#### 1.6a The guarantee holds only when there are no errors (deferred to 2.4)

**Rule:** the meaning of the overall result.

**Problem.** The IMM-FIELD-TYPE section says a parameterized type such as `Box<Loose>` satisfies the rule whatever its type arguments, because `Box` cannot have a field of type `T`. That is true only if `Box` itself has no errors. A marked type counts as immutable even when it has errors, as `Flawed` in `fieldtypes/FieldTypeImmutableTypes.java` relies on. So the real guarantee is conditional: if the analysis reports no errors, every marked type is immutable. The spec never says so.

**Decision.** Deferred. Decide it once, under review item 2.4, which is about what a positive result and the overall guarantee mean. Review item 4.8 raises the same point about the same sentence, so 2.4 should cover 1.6a and 4.8 together.

#### 1.6b A type parameter shadows a type of the same name

**Rule:** IMM-FIELD-TYPE. Field types are resolved with Java's scoping rules.

**Problem.** A type parameter can have the same name as a class:

```java
@Immutable class Label {}

@Immutable
class Shadow<Label> {
    final Label value = null;   // Label is the type variable, not the class
}
```

Inside `Shadow`, `Label` means the type parameter, so the field is an IMM-FIELD-TYPE error. A resolver that ignores type-parameter scopes would find the immutable class `Label` and report the field as satisfied. That is a missed error, so it is a soundness bug.

**Decision.** Add this sentence to the IMM-FIELD-TYPE section of `analyses/Immutable.md`, next to the type-variable bullet:

> A field's type is resolved using Java's scoping rules (JLS §6.3, §6.4.1). In particular, a type parameter shadows a type of the same name, so in `class Shadow<Label>` the name `Label` means the type variable.

**Why.** It is one sentence, and it closes a soundness hole. The other ways a type variable could shadow a name in a field's type are already ruled out:

- **Methods' type parameters:** fields can't use them.
- **Static nested classes:** a static nested class can't see the enclosing class's type parameters.
- **Inner classes:** an inner class can't be immutable (IMM-INNER).

**Effect on the tests.** In `fieldtypes/FieldTypeTypeVariables.java`, which already declares the immutable class `Label`, add:

```java
@Immutable
class Shadow<Label> {
    // IMM-FIELD-TYPE violated: the type variable Label shadows the immutable class
    // Label, and is treated as a mutable type
    final Label value = null;
}
```

### 1.7 Arrays

Covered. No change. `fieldtypes/FieldTypeArrays.java` has every case the review lists: C-style brackets, multiple declarators, mixed brackets, varargs record components, and array fields that are static, interface constants, record components, or in an enum. Arrays of type variables are in `FieldTypeTypeVariables.java`, and arrays with type annotations are part of 1.3b.

### 1.8 Primitives, boxed types, String, JDK types

Superseded by 1.4a. The review says these types are all treated as not immutable. Decision 1.4a makes `String` and the boxed primitive types trusted. Other JDK types are still not immutable.

### 1.9 Static vs instance fields

**Rule:** Limitations. Static fields read during class initialization are not checked.

**Problem.** The field rules already cover static fields, and the tests have them throughout. But while a type is being initialized, code can read one of its `static final` fields before the field is assigned. That code sees the default value, `null` or `0`, and later sees the real value. This is the static counterpart of `this` escaping a constructor. Checked with javac 26 and java:

```java
final class Late {
    static final Late FIRST = Peek.peek();     // runs while Late is being initialized
    static final Late SECOND = new Late();
}
class Peek {
    static Late snapshot;
    static Late peek() { snapshot = Late.SECOND; return null; }
}
```

`Peek` saw `SECOND` as `null`, and `SECOND` was then non-null. The same happens across two classes whose static initializers refer to each other (JLS §12.4.2). Strictly, the Summary's wording ("cannot change after the type is initialized") still holds, because the change happens before `Late` has finished initializing. But a reader can easily miss that qualifier.

**Decision.** Don't check it. Add a bullet to Limitations in `analyses/Immutable.md`, after "`this` escaping during construction":

> - **Reading static fields during class initialization.** Code that runs while an immutable type is being initialized, including code in another class reached through a cycle of static initializers, can read a static final field before it is assigned and see its default value (JLS §12.4.2). The guarantee for static fields begins once the type is initialized.

**Why.** Detecting it would mean analyzing initializer expressions and the order in which classes are initialized, which is well beyond a declaration-only analysis. The `this` escape bullet already handles instance fields the same way.

**Effect on the tests.** None. Limitations are not tested.

## Open

Not decided yet. These are numbered by review item, with the proposals from the discussion so far.

- **1.22 Types outside the analyzed source:** don't trust library classes marked `@Immutable`. A type outside the source is not immutable, which is the current behavior, so the spec only needs to say so.
- **2.6 Ambiguous result placement:** report the results for the implicit `Object` superclass, for no superinterfaces, for IMM-LAMBDA, and for constants on the type's declaration. Record components are decided in 1.3a.
- **2.4 The meaning of a positive result and of the overall guarantee:** also decide 1.6a and 4.8 here. They all ask the spec to say that the guarantee holds only when the analysis reports no errors.
- All other review items in sections 1, 2, and 4 have no proposal yet.
