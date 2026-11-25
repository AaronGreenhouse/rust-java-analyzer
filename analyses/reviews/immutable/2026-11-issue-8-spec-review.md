# Review of `analyses/Immutable.md`

> **Reviewed:** [`analyses/Immutable.md`](https://github.com/AaronGreenhouse/rust-java-analyzer/blob/90f737b2f413bd9de3702ea99e2391cf6811c33f/analyses/Immutable.md) at commit [90f737b](https://github.com/AaronGreenhouse/rust-java-analyzer/tree/90f737b2f413bd9de3702ea99e2391cf6811c33f), and the tests at commit [41bd13a](https://github.com/AaronGreenhouse/rust-java-analyzer/tree/41bd13a2fa918918920b39545641876c8cfd1f56). Line numbers refer to those commits.
>
> **Issue:** #8 · **Status:** a snapshot, not edited after review. The decisions taken on it are in [2026-11-issue-8-decisions.md](2026-11-issue-8-decisions.md).
>
> **Corrections:** two findings are wrong, as explained in a [comment on issue #8](https://github.com/AaronGreenhouse/rust-java-analyzer/issues/8#issuecomment-7142469233). The `SportsCar` count (summary item 3 and section 4.4) doesn't contradict the spec, because the third result is the separate positive for `final`. And `HIGH` and `RED` (summary item 5 and section 4.9) don't lack positive markers, because IMM-ANONYMOUS has no positive results.

This review was written by a Claude Code subagent on 2026-11-24.

Reviewed: `analyses/Immutable.md` (line numbers below refer to it), `promises/Immutable.java`, and all test cases under `java/src/test/java/io/github/aarongreenhouse/testcases/immutable/`.

## Summary

The spec is careful and mostly complete for a declaration-only analysis. The test suite follows it closely. The most important findings:

1. **Soundness hole in IMM-LAMBDA (high).** A lambda or method reference can target an *intersection type* in a cast, such as `(Runnable & Tag) () -> n[0]++`. The resulting object is an instance of `Tag`. If `Tag` is an immutable interface with no abstract methods, it is not functional, so the declaration-level check passes and nothing is reported. Many test interfaces (`Badge`, `Shape`, `Animal`, `Marker`, `Identity`) are open to this.
2. **`java.lang.reflect.Proxy` is a second way to implement an immutable interface without a declaration (medium).** It is not listed under Limitations, which mention reflection only as a way to change final fields.
3. **Spec text contradicts the tests and the implementation note in a few places.** The lambda example at line 172 puts the error on the lambda, but line 192 and the tests put it on the interface. Line 83 says `SportsCar` gets "two" IMM-SUBCLASS results, but the tests expect three (the third is the `final` result). IMM-MARK's "if and only if" (line 41) is contradicted by IMM-ANNOTATION (line 208) and by the Scope rule for annotated library types (line 26).
4. **Result placement is underspecified (medium).** The spec does not say where several results go: the IMM-SUPERCLASS result for an implicit superclass, the IMM-SUPERINTERFACE result when there are no superinterfaces, the IMM-LAMBDA result, results for record components and enum constants, and per-declarator results. Line 85 is also ambiguous when an immutable type is reached both directly and indirectly.
5. **Two test files are missing positive markers for the rule they test.** In `FieldTypeEnumConstantBody.java`, constant `HIGH` has no IMM-FIELD-TYPE marker. In `FinalEnumConstantBody.java`, constant `RED` has no IMM-FINAL-CLASS marker.
6. **Open questions the spec should settle:** library class files that carry `@Immutable` (it has CLASS retention), sealed hierarchies as a closed world, the target Java version, the fields of `java.lang.Enum`, synthetic fields such as `$VALUES`, and preview value classes.

**Verdict on question 3 (checkability).** Every rule except IMM-LAMBDA can be checked with a syntax tree, a name resolver, and an index of all type declarations in the source. The resolver needs type-parameter scopes and the inherited-member-type lookup of JLS §6.4.1 / §8.5. No type checker, type inference, overload resolution, or expression typing is needed. The one exception is a qualified `o.new Inner() {}`, which only matters for a marked inner class, and that is already an error. Name resolution needs library class files in one case: a member type inherited from a library supertype can shadow a same-package or imported source type (for example `Entry` inside `class M extends AbstractMap<K,V>`). Without them the analysis can give an unsound "immutable" result. IMM-LAMBDA (the functional-interface test) needs a small slice of the type system beyond name resolution: generic type-argument substitution into inherited method signatures, erasure, subsignature and override-equivalence (JLS §8.4.2), and knowledge of `Object`'s public methods. It needs to read library class files only when IMM-SUPERINTERFACE is already violated, because otherwise every superinterface is in the source. So the minimum beyond "AST + name resolver" is: (a) a class-file reader for supertypes, member types and method signatures, (b) method-signature substitution and override-equivalence, (c) a whole-source type-declaration and hierarchy index, and (d) a few hard-coded JDK facts. Fixing the intersection-cast hole syntactically needs nothing more.

---

## 1. Missing corner cases

Status: **Covered** = the spec addresses it adequately. **Partial** = addressed but ambiguous or incomplete. **Gap** = not addressed.

### 1.1 Lambdas and method references with intersection target types (Gap, soundness)

Lines 190-200 argue that checking whether each immutable interface is functional is "enough, and still sound". That is not true. JLS §15.27.3 and §9.9 allow the target to be an intersection type that *induces* a functional interface. A marker interface with no abstract methods can be one component:

```java
@Immutable interface Tag {}                     // IMM-LAMBDA satisfied: no abstract methods
...
int[] n = {0};
Tag t = (Tag) (Runnable & Tag) () -> n[0]++;    // a mutable Tag, nothing reported
Tag u = (Tag) (Runnable & Tag) this::bump;      // same for a method reference
```

The same holds for an immutable interface whose only abstract methods match public methods of `Object` (`Identity` in `LambdaSatisfied.java`). An immutable interface with exactly one abstract method is already reported, so the hole is for non-sealed immutable interfaces with no abstract methods of their own. A sealed component should be rejected by javac, which does not allow a lambda to implement a sealed interface, but check this against the JLS for the target version.

Fix options:
- (a) A syntactic rule: report any cast expression whose type is an intersection (`(A & B ...)`) with an immutable component, when its operand, after removing parentheses, is a lambda or method reference. This needs only the AST and the name resolver. It should be checked whether an intersection target can also come from inference, such as `<T extends Runnable & Tag> void m(T t)` called as `m(() -> {})`. If it can, option (a) is incomplete.
- (b) A strictly declaration-level rule: a non-sealed immutable interface must have at least two abstract methods. This is sound but would ban marker interfaces, which the tests use heavily.

### 1.2 `java.lang.reflect.Proxy` (Gap, Limitations)

`Proxy.newProxyInstance(loader, new Class<?>[]{Badge.class}, handler)` creates an instance of an immutable interface whose behavior, and in effect its state, is in a mutable `InvocationHandler`. It is out of scope in the same way as reflection, but Limitations (line 227) lists reflection only for changing final fields. It should be listed as another way to implement an immutable interface without a declaration.

### 1.3 Records (mostly Covered)

- Implicit component fields: covered (lines 106, 114).
- Varargs components: covered by the tests (`Polyline`).
- Static fields: covered.
- Records cannot declare instance fields or instance initializers, so nothing else adds state.
- Compact and canonical constructors and accessors are behavior. The spec could say once that they are irrelevant.
- **Partial:** the spec never says where the result for a component field goes. The tests put it on the record component, and only `package-info.java` says so.
- **Gap (minor):** type-use annotations on component or field types, such as `final @NonNull Money m;` and `final java.lang.@NonNull String s;`, must be skipped when the type name is resolved.

### 1.4 Enums (mostly Covered; two gaps)

- Constant bodies: covered (lines 87, 93, 101, 181).
- Implicit finality: covered.
- **Gap: the fields of `java.lang.Enum`.** IMM-SUPERCLASS allows `java.lang.Enum` (line 48), but `Enum` declares `private final String name` and `private final int ordinal`. By the field-type rule `String` is "not immutable". The exception is fine, but the spec should say that `Object`, `Enum`, and `Record` are *trusted*, not just "allowed".
- **Gap: synthetic fields.** javac generates `private static final E[] $VALUES` for every enum, and `this$0` / `val$x` for inner and local classes. These fields are not in the source, so a source analysis never sees them. But `$VALUES` is a mutable array in the enum's static state, which contradicts the summary's claim that the enum's static fields cannot change (line 5). Only reflection could reach it. The spec should say that only fields declared in source, record component fields, and enum constants are considered.
- Since Java 17 an enum with constant bodies is implicitly *sealed* (JLS §8.9), and its permitted subclasses are the bodies. Line 93 says "not final", which is correct, but calling it sealed would explain why IMM-SUBCLASS is closed for it.
- Static fields in constant bodies are allowed since Java 16. Line 101 covers them implicitly ("Fields declared in the class body"). A test such as `RED { static int hits; }` would be worth adding.

### 1.5 Sealed hierarchies and `permits` (Partial)

- IMM-LAMBDA and sealed interfaces: covered.
- Implicit `permits`: covered by the tests (`Token`).
- **Gap:** A sealed immutable class whose permitted subclasses are all `final`, sealed, or records and are all in the analyzed source is a *closed world*, just like a `final` class. The spec gives a "trivially satisfied" positive result for `final` (lines 89-95) but nothing for sealed. Line 95 suggests sealed classes get no result, which wastes the one feature that avoids the open-world problem. Example:
  ```java
  @Immutable sealed abstract class Shape permits Circle, Square {}   // could be "closed"
  ```
  Note that with no module (the unnamed module), permitted subclasses must be in the same package, but they could still be separately compiled class files. That is rare but possible.
- **Gap (minor):** What is reported when a `permits` clause names an unmarked type? Currently the error goes only on the subclass's `extends` clause (`Card`, `Feathers` in the tests). That works, but should be stated.

### 1.6 Generics (mostly Covered)

- Type variables as field types: covered.
- Parameterized, wildcard, and raw types: covered (line 135 and the tests).
- Intersection types cannot be field types, only type-variable bounds, so they are handled by the type-variable rule.
- **Partial:** Line 135 says type arguments cannot introduce mutable state because `Box` cannot have a field of type `T`. That is true only if `Box` itself has no errors. Since `Flawed` "counts as immutable even though it has errors" (test `FieldTypeImmutableTypes.java`), the overall guarantee is conditional: no errors anywhere implies immutability. The spec never states this global "if and only if no errors" reading, and it should (see 2.4).
- **Gap (resolver):** A type variable can shadow a class of the same name: `@Immutable class Holder<Money> { final Money m; }` is a type variable, not the class `Money`. The resolver must handle type-parameter scopes (JLS §6.3). Not tested.

### 1.7 Arrays: Covered

Including C-style brackets, multiple declarators, and varargs.

### 1.8 Primitives, boxed types, String, JDK types: Covered

All are treated as not immutable. Known-immutable library types are future work (line 232).

### 1.9 Static vs instance fields: Covered

Line 99. Note, consistent with the summary's wording "after the type is initialized": a class-initialization cycle can expose default values of `static final` fields during initialization. This is the static counterpart of `this` escape. Worth one line in Limitations.

### 1.10 Interface constants: Covered

Line 109. Inherited constants are a known limitation (line 228). See 4.6 for an inconsistency in why they are handled differently for interfaces and classes.

### 1.11 Inherited fields: Covered

Through IMM-SUPERCLASS, except for `Enum` (see 1.4).

### 1.12 Nested, inner, local, and anonymous classes (mostly Covered)

- **Gap (resolver):** A member type named `Immutable` shadows an import:
  ```java
  import io.github.aarongreenhouse.promises.Immutable;
  class Outer {
      @interface Immutable {}
      @Immutable static class X {}   // Outer.Immutable, not the real one (JLS §6.4.1)
  }
  ```
  The same happens with a member type *inherited* from a superclass or superinterface, possibly a library one. Neither case is tested.
- **Gap (minor):** `o.new Kiosk() {}`, a qualified class instance creation with an anonymous body. To resolve `Kiosk` you must know the type of the expression `o`. It only matters when `Kiosk` is a marked inner class, which is already an IMM-INNER error, so the spec can say these are ignored.
- Member classes of annotation interfaces are implicitly static (JLS §9.6 refers to §9.5). Line 156 mentions only interfaces. Minor.

### 1.13 Lambdas and method references: Partial

See 1.1, and 4.1 for the line 172 example.

### 1.14 `this` escape and reflection/serialization: Covered as out of scope

Lines 226-227.

### 1.15 Fields assigned in initializers vs constructors: Covered by construction

Only finality is checked, and blank finals are fine.

### 1.16 `transient`/`volatile`: Covered by the tests

`volatile final` is a compile error.

### 1.17 Annotation placement: Covered or N/A

`@Target(TYPE)` makes packages, modules, type parameters, type uses, enum constants, and record components compile errors. `@Immutable` is not `@Repeatable`, so a repeat is a compile error. **Partial:** *meta-annotation*. A custom `@ValueType` annotated `@Immutable` is an IMM-ANNOTATION error, and types annotated `@ValueType` are not immutable. This follows from the spec but is worth stating, because users may expect a meta-annotation to propagate.

### 1.18 `@Inherited`: Covered (line 35)

### 1.19 How the annotation is named (mostly Covered)

- Covered by the tests: fully qualified name, single-type import, on-demand import, a decoy annotation, and a same-package decoy shadowing an on-demand import.
- Java has no import aliasing, so "imported under a different name" does not arise.
- Not covered: member-type shadowing (1.12).
- **Gap:** The spec should say the analyzer must know that the package `io.github.aarongreenhouse.promises` contains `Immutable` *even when that package is a library jar and not analyzed source*. That is the normal case once test cases or users are analyzed without `java/src/main`.

### 1.20 Cross-package and cross-compilation-unit: Covered (`crosspackage` tests)

### 1.21 Cyclic type references: Covered implicitly, should be explicit

Immutability is by marking (IMM-MARK), not by successful verification, so `@Immutable class Node { final Node next; }` and mutually referring types need no fixpoint. The tests show this (`Wallet.next`, `Flawed`). The spec should state this modularity explicitly. It is a key design property.

### 1.22 Types outside the analyzed source: Partial

Covered as "not immutable" (line 26). **Gap:** `@Immutable` has `RetentionPolicy.CLASS` (line 34), so it is *visible in library class files*. The spec does not say whether a library class annotated `@Immutable`, perhaps verified when that library was analyzed, should be trusted. Currently it is not, which makes separate compilation and multi-module projects impractical. And if it *were* trusted, the open-world subclass problem would become sharper. A decision is needed either way, along with the reason for CLASS rather than SOURCE retention.

### 1.23 Multiple top-level types per file: Covered implicitly

The tests use them. Nothing special.

### 1.24 `module-info.java`: N/A for the rules

The annotation cannot apply to modules. In a named module, the name resolver must respect `requires`/`exports` readability. It is irrelevant if the code compiles.

### 1.25 `var`: N/A

Fields cannot use `var`, and `var x = () -> 1` is illegal. It only matters for precise IMM-LAMBDA (future work).

### 1.26 Java version features (Gap)

- The spec does not state a target Java version. The tests need at least Java 17 (sealed classes, local interfaces and enums).
- Pattern matching is irrelevant.
- **Value classes (JEP 401, preview):** `value class` and `value record` fields are implicitly final. If supported, line 103's list of implicitly final fields would need another entry. Otherwise the spec should say preview features are out of scope.
- Implicitly declared classes (compact source files, JEP 512): the implicit class cannot be annotated, but member types inside it can. This should just work.

---

## 2. Hard problems

### 2.1 The open world for subclasses and implementations (acknowledged, fundamental)

A non-final immutable class or non-sealed immutable interface can be extended outside the analyzed source (lines 25, 95, 225). Every IMM-FIELD-TYPE "satisfied" result for an interface or non-final class type therefore holds only under a closed-world assumption. Ways to soften it:
- give sealed and all-`final` hierarchies a positive "closed" result (1.5)
- treat an immutable class whose constructors are all `private` as closed except for its nested classes
- optionally make "immutable class must be final or sealed or abstract-with-private-constructors" a strict-mode rule

### 2.2 The subtype rules are actually *local*, so say so

IMM-SUBCLASS and IMM-SUBINTERFACE read as whole-program rules ("all subclasses"), but all their results are placed on the *subtype's* clauses (line 85). So they can be checked by walking *up* from each type declaration in the source, with no search for subclasses. The walk can stop at the first type outside the source, because a library type cannot have a source type as a supertype unless the library was compiled from an earlier version of the analyzed source, a case the spec should exclude. The only global facts needed are the declarations of supertypes, which a resolver gives. Saying this in the spec removes the impression that a whole-program subclass enumeration is required. It also makes incremental (per-file) analysis feasible: a file's results depend only on the files that declare its supertypes.

### 2.3 Counting abstract methods for IMM-LAMBDA is genuinely tricky

JLS §9.8 requires:
- the *member* methods after overriding, including a default method that overrides an abstract one (`LowOnly`) and an abstract method that redeclares a default one (`FormalGreeter`)
- merging inherited methods with override-equivalent signatures *after substituting type arguments*:
  ```java
  interface A<T> { void m(T t); }
  interface B    { void m(String s); }
  @Immutable interface C extends A<String>, B {}  // one abstract method after substitution: functional
  ```
  The `Channel` test covers only identical signatures.
- generic methods (the `Transformer` test) and erasure-based subsignatures:
  ```java
  interface X { void m(java.util.List<String> l); }
  interface Y { void m(java.util.List l); }       // raw: subsignature; javac may treat X & Y as functional
  ```
- matching *public* `Object` methods by signature, where the parameter type must resolve to `java.lang.Object`. A source class named `Object` in the same package changes the answer for `boolean equals(Object o)`.

None of this needs expression typing, but it is more than name resolution. The spec does not list these cases. The tests cover the main ones, but not substitution or raw/erasure.

### 2.4 The meaning of a positive result and of the overall guarantee

Because immutability is by marking, an IMM-FIELD-TYPE "satisfied" for `Flawed` is reported even though `Flawed` has errors. The spec never states the overall theorem: *if the analysis reports no errors in the analyzed source (and the limitations don't apply), every marked type is immutable*. It should, because without it a reader may take individual positive results as guarantees.

### 2.5 Results that may look contradictory

These are allowed by the spec but likely to confuse users:
- `EnumWithBody` gets "IMM-SUBCLASS satisfied: no class declaration can extend an enum" while its constant bodies get IMM-ANONYMOUS errors for being subclasses.
- A marked inner class gets "IMM-SUBCLASS satisfied" and "IMM-INNER violated" (line 162).
- A class that is not final and has no subclasses gets no IMM-SUBCLASS result (line 95), while a `final` one gets a positive result. That is fine, but a user sees a positive result disappear when `final` is removed from an otherwise identical class.

None of these is a logical contradiction. The spec could say explicitly that each rule is judged independently.

### 2.6 Ambiguous result placement

- **Line 85:** "the clause that names ... the immutable type itself, for a direct subtype ... If reached through more than one clause, the first such clause." For `class X extends Circle implements Shape` (where `Circle implements Shape`), `X` is a *direct* implementer of `Shape` and also reaches it through `Circle`. The first rule says `implements Shape`. The second says `extends Circle`, which comes first. Pick one: "prefer the clause naming the immutable type itself; otherwise the first clause".
- **Line 65:** "Supertypes that satisfy these rules should be positively marked" names the wrong thing. It is the subtype, or its clause, that gets the result. The spec also does not say where the positive result goes when the superclass is implicit (enum, record, no `extends`) or when there are no superinterfaces. The tests put it on the type declaration. Nor does it say that IMM-SUPERINTERFACE gives one result per listed superinterface, which the tests assume (`ExtendsTwoImmutable`).
- **IMM-LAMBDA:** placed on the interface declaration (line 192), but on which token? The tests imply the declaration.
- **Fields:** which declarator in `int a, b;`, the record component for component fields, the enum constant for constants. The convention is in `package-info.java` but not in the spec.
- **IMM-ANONYMOUS:** on the `new` expression or on the enum constant. Same remark.

Line 219 should be extended with an "anchor" for each rule.

### 2.7 What IMM-LAMBDA's positive cases are

Line 217 says positive results are reported only where a rule defines a case. IMM-LAMBDA's cases are implied by the implementation note: no abstract methods, more than one, only `Object` methods, sealed. The tests use five different case descriptions (`LambdaSatisfied.java`). List the cases explicitly, as IMM-FIELD-TYPE does.

---

## 3. Checkability with only a syntax tree and a name resolver

Definitions:
- **AST** = a parse tree of the analyzed sources.
- **NR-src** = a name resolver over source declarations only. It handles imports, packages, nesting, type parameters, and member types inherited from *source* supertypes, and it treats any unresolved name as "outside source".
- **NR-lib** = NR-src plus reading library class files for package contents, member types, and supertypes.
- **Index** = a table of all type declarations in the analyzed source, with their resolved direct supertypes.

| Rule | AST + NR-src | Needs more? | Notes |
|---|---|---|---|
| IMM-MARK | Almost | NR-lib for one corner | Resolve the annotation name. Must hard-code that `io.github.aarongreenhouse.promises.Immutable` exists even when it is in a jar. Unsound only if a member type named `Immutable` is *inherited from a library supertype* and shadows the import, which is very unlikely. |
| IMM-SUPERCLASS | Yes | — | Implicit superclass is syntactic (no `extends`, or enum/record kind). Explicit `extends Object` needs `Object` to resolve to `java.lang.Object` and not a shadowing source or inherited type. NR-src with the implicit `java.lang.*` rule handles this. |
| IMM-SUPERINTERFACE | Yes | — | Same as above. |
| IMM-SUBCLASS | Yes, with Index | — | Upward walk (2.2). Finality is syntactic: `final`, record, enum without bodies. `sealed`/`non-sealed` are syntactic. Implicit `permits` is "same compilation unit", which is syntactic. |
| IMM-SUBINTERFACE | Yes, with Index | — | Upward walk through superclasses and superinterfaces (JLS §8.1.5). Stop at the first non-source type. |
| IMM-FINAL-INTERFACE | Yes | — | Purely syntactic. |
| IMM-FINAL-CLASS | Yes | — | Syntactic. Implicit finality comes from the declaration kind, record components, and enum constants. Fields in constant bodies are syntactic. |
| IMM-FIELD-TYPE | Almost | NR-lib for one corner | Primitive and array types (including C-style and varargs) are syntactic. Type variables need type-parameter scopes. Named types resolve through NR. The corner case: an inherited library member type can shadow a source type with the same simple name, for example `Entry` inside `class M extends AbstractMap<K,V>` when the package also has an `@Immutable class Entry`. NR-src would wrongly pick the immutable source `Entry`, which is unsound. |
| IMM-INNER | Yes | — | Syntactic: `static` modifier, declaration kind, member of an interface or annotation interface, local vs member. |
| IMM-ANONYMOUS | Yes | (Expression typing for `o.new Inner(){}` only) | Find `new T(...) {...}` and enum constants with bodies, then resolve `T`. Diamonds and type arguments are irrelevant. A qualified creation names an inner class, which is never legitimately immutable, so it can be ignored or approximated. |
| IMM-LAMBDA (declaration form) | No | Signature substitution and override-equivalence (JLS §8.4.2, §9.4.1, §9.8). NR-lib only when IMM-SUPERINTERFACE is already violated. | Collect member abstract methods over the superinterface graph. Apply defaults and redeclarations. Substitute type arguments. Merge override-equivalent signatures, using erasure. Drop public `Object` methods (hard-code `equals(Object)`, `hashCode()`, `toString()`; the other public `Object` methods are final and cannot be redeclared). `sealed` is syntactic. If any superinterface is a library type (`Runnable`, `Comparator`), the method count needs class files. That interface is already an IMM-SUPERINTERFACE error, so an approximation such as "unknown, report it" would be acceptable if the spec allows it. |
| IMM-LAMBDA (proposed intersection-cast fix, 1.1a) | Yes | — | Syntactic cast-with-`&` whose operand is a lambda or method reference, plus resolving the components. |
| IMM-LAMBDA (precise, future work) | No | Full attribution: overload resolution, inference, target typing | As line 200 says. |
| IMM-ANNOTATION | Yes | — | Syntactic `@interface` plus IMM-MARK. |

**Overall verdict:** With the AST, an Index, and NR-src, every rule except IMM-LAMBDA is decidable, with two unlikely corner cases where shadowing by inherited library member types needs NR-lib. No type checker, inference, overload resolution, or attribution is needed anywhere in the spec as written. That holds because every name that is checked appears in a *type context* (annotation, `extends`/`implements`, field type, class instance creation type, cast type), where JLS §6.5 classifies names syntactically. The one expression-level exception is `o.new T(){}`, which can be ignored.

**Minimal extra capabilities needed:**
1. A whole-source type index with resolved direct supertypes. This is not a whole-program analysis: an upward walk is enough.
2. Type-parameter scopes and inherited-member-type lookup in the resolver (JLS §6.4.1, §8.5, §9.5).
3. A class-file reader for library supertypes, member types, and method signatures and modifiers. It is strictly needed only for (a) the shadowing corners and (b) IMM-LAMBDA on interfaces that already violate IMM-SUPERINTERFACE. The spec's own example at line 23 (`Runnable`) is exactly that error case.
4. Method-signature substitution, erasure, and override-equivalence for IMM-LAMBDA.
5. Hard-coded JDK facts: `java.lang.Object`, `Enum`, `Record`, `Object`'s public non-final methods, the implicit `java.lang.*` import, and the location of `io.github.aarongreenhouse.promises.Immutable`.

---

## 4. Other issues

### 4.1 The line 172 example contradicts the implementation note and the tests

```java
Source s = () -> state[0]++;   // error: lambda of immutable type Source
```
Line 192 and `LambdaUsage.java` say the lambda itself gets *no* result, and the error is on `interface Source`. Reword the comment, for example "this is why Source must not be functional", or move the error marker.

### 4.2 Line 13 understates what is examined in method bodies

It says method bodies are examined only "to find lambdas, method references, and anonymous classes". But:
- the analysis must also find *local classes, records, enums, and interfaces* in bodies and initializers, for IMM-MARK, IMM-INNER, the subtype rules, and the field rules
- under the implementation note, lambdas and method references are *not* examined at all, unless the intersection-cast fix is adopted

### 4.3 IMM-MARK "if and only if" (line 41) conflicts with other rules

An annotated annotation interface is "never considered immutable" (line 208), and an annotated library type is "treated as not immutable" (line 26). Reword: "A type declared in the analyzed source, other than an annotation interface, is immutable iff ..."

### 4.4 Line 83 count

"`SportsCar` receives two IMM-SUBCLASS results". The tests (`SubclassSatisfied.java`) expect three, because it is `final` and also gets the trivially-satisfied result from line 89. Say "two results for its supertypes, plus ...", or drop `final` from the example.

### 4.5 The `Object`/`Enum`/`Record` exceptions (line 26) are described as "allowed", not "trusted"

The superclass rule's justification (line 52: "would inherit the mutable fields") silently depends on these classes having no mutable state. For `Enum` (a `String name` field) that relies on trusting `String`, which IMM-FIELD-TYPE does not trust. State the trust explicitly.

### 4.6 Superinterface asymmetry

IMM-SUPERINTERFACE forbids `@Immutable interface Shape extends Comparable<Shape>` or `extends Serializable`, and the tests treat `extends Cloneable` as an error. But `@Immutable final class Circle implements Comparable<Circle>, Serializable` is fine (lines 61-63). The stated reason (inherited constants) applies equally to both, and the class case is just listed as a limitation. Either accept the limitation for interfaces too, or keep the rule and give a reason it is worth the practical cost. The cost is large: an immutable interface cannot extend any JDK interface until a known-immutable or known-constant-free list exists. Note that inherited interface constants are *static fields of the interface*, not of the class, so by the summary's own definition (line 5: "its static fields") they are arguably not the class's state at all.

### 4.7 Line 179's argument is right but needs a premise

"If that type is a subtype of an immutable type, then it must itself be immutable" holds only for named types in the source. A library type cannot be a subtype of a source immutable type (see 2.2), so this is fine, but say so.

### 4.8 Line 135 rationale depends on `Box` having no errors

See 2.4.

### 4.9 The tests are missing two required positive markers

`package-info.java` says positive results for the rule a file is testing must be marked.
- `fieldtypes/FieldTypeEnumConstantBody.java`: constant `HIGH { ... }` has only the IMM-ANONYMOUS marker. It should also have `// IMM-FIELD-TYPE satisfied: an enum constant has the type of its own enum` (`LOW` has it).
- `finalfields/FinalEnumConstantBody.java`: constant `RED { ... }` has only the IMM-ANONYMOUS marker. It should also have `// IMM-FINAL-CLASS satisfied: an enum constant is implicitly final` (`GREEN` has it).

### 4.10 Tests worth adding for the gaps above

- An intersection-cast lambda: `(Runnable & Badge) () -> {}`.
- A member type named `Immutable` shadowing the import.
- A type variable shadowing a class name: `class H<Money> { final Money m; }`.
- Static fields in enum constant bodies.
- A generic-substitution functional interface (the `A<String>`/`B` example in 2.3).
- `extends Circle implements Shape` placement (2.6).
- `boolean equals(Object o)` where `Object` is a same-package source class.
- An immutable interface extending a library functional interface: `@Immutable interface Job extends Runnable {}`, where both IMM-SUPERINTERFACE and IMM-LAMBDA should fire.

### 4.11 Minor wording

- Line 15 says "symbol table on top of the parser's AST", while line 23 says library class files may be read. Put the two together, and state that the symbol table covers library types as well.
- Line 109: "it is impossible for this to not be true" could be shortened to "this cannot be violated".
- Line 160: "stating which case applies" should list the cases: declared `static`, member enum/record/interface, member of an interface, local enum/record/interface. The tests use these, but the spec does not enumerate them.
- Line 177's example of code that "receives no IMM-ANONYMOUS result" could also mention anonymous classes of non-immutable types, as in `AnonymousSatisfied.java`.
