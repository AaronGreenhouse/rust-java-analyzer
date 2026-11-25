# Review of the `@Immutable` test cases

> **Reviewed:** the test cases and [`analyses/Immutable.md`](https://github.com/AaronGreenhouse/rust-java-analyzer/blob/20ee2328555685e9499b6328943b0ebcab5acdae/analyses/Immutable.md) at commit [20ee232](https://github.com/AaronGreenhouse/rust-java-analyzer/tree/20ee2328555685e9499b6328943b0ebcab5acdae). Line numbers refer to that commit.
>
> **Issue:** #7 · **Status:** resolved by PR #9. See the **Outcome** note at the end of each section.

A Claude Code subagent reviewed the test cases independently against the spec on 2026-11-23. It did not change any files.

Correctness items A1, A2 and A3 were checked against the source before any fixes were made, and they are real. The other findings were not verified line by line before they were acted on.

All paths below are relative to `java/src/test/java/io/github/aarongreenhouse/testcases/immutable/`.

**Overall:** The test cases are in good shape. They compile cleanly and the `violated:` comments look correct. The real problems are:

- about 13 missing positive-result comments;
- one misplaced comment;
- several places where the spec does not say how many results to expect or where they go.

**Compile check:** `mvn -f java/pom.xml test-compile` reports BUILD SUCCESS. Its only warning is that the `maven-compiler-plugin` version is not set. Maven considered the build up to date, so the subagent also compiled every source from scratch with `javac --release 26 -Xlint:all -Xdoclint:all,-missing`. That finished with no errors or warnings.

**CLAUDE.md:** All files follow it. Every test file is in `java/src/test/java`, under `io.github.aarongreenhouse.testcases.immutable`. The decoy annotation `marker/decoy/Immutable.java` is not in the `promises` package. That is correct, because it is test input, not one of the analyses' annotations.

## A. Correctness

The subagent re-checked every `violated:` comment against the spec and found none that is wrong. The problems are missing positive results and where comments are placed.

1. **Final classes are missing IMM-SUBCLASS positives (high confidence; verified).** The spec says (line 83): "A class that can have no subclasses trivially satisfies IMM-SUBCLASS and is positively marked as such." The IMM-SUBCLASS files mark this for `FinalLeaf`, `FinalRecord` and `FinalEnum`, but not for these other final classes:
   - `subtypes/SubclassSatisfied.java:28` (`SportsCar`)
   - `subtypes/SubclassSatisfied.java:37` (`Diesel`)
   - `subtypes/SubclassSatisfied.java:44` (`Garage.Truck`)
   - `subtypes/SubclassSatisfied.java:54` (`TreeLeaf`)
   - `subtypes/SubclassSatisfied.java:72` (`AppleCrate`)
   - `subtypes/SubclassViolated.java:70` (`Cash`)

   **Fix:** Add `// IMM-SUBCLASS satisfied: the class is declared final, so it has no subclasses` to each type declaration. Each of these classes then has two IMM-SUBCLASS results: this one on the declaration, and the existing one on the `extends` clause.

2. **The helper base classes are missing IMM-SUPERCLASS positives (high confidence; verified).** The spec says (line 63): "Supertypes that satisfy these rules should be positively marked." These marked classes implicitly extend `Object`, and their files test IMM-SUPERCLASS:
   - `supertypes/SuperclassSatisfied.java:6` (`ImmutableBase`)
   - `supertypes/SuperclassSatisfied.java:9` (`ImmutableAbstractBase`)
   - `supertypes/SuperclassSatisfied.java:12` (`ImmutableGenericBase`)
   - `supertypes/SuperclassSatisfied.java:20` (`ImmutableNestedBase`)
   - `supertypes/SuperclassViolated.java:20` (`ImmutableRoot`)

   The same case is marked at `SuperclassSatisfied.java:23`, so these files are incomplete as written. **Fix:** Add the "implicitly `java.lang.Object`" comment to each, or move the helpers into a file that tests no rule (see C2).

3. **One comment is in the wrong place (high confidence; verified).** In `nested/InnerViolated.java:10-11`, the IMM-INNER comment sits before the field `task` (line 12). The class it is about, `Step`, is declared at lines 13–14, inside the lambda. `package-info.java` says a comment goes "directly before the code that the result is about: a type declaration". **Fix:** Move the comment into the lambda, directly above `@Immutable` at line 13.

4. **Some positives are marked for rules the file does not test (medium confidence).** `package-info.java` says: "Positive results are marked only for the rules a file is testing." These comments break that rule:
   - IMM-SUBINTERFACE positives at:
     - `anonymous/AnonymousViolated.java:10`
     - `anonymous/LambdaSatisfied.java:24` and `:63`
     - `anonymous/LambdaViolated.java:47`, `:84`, `:108` and `:110`
   - An IMM-SUBCLASS positive at `subtypes/SubinterfaceSatisfied.java:48`.

   The IMM-SUBCLASS one matters more. If it means that file now tests IMM-SUBCLASS, the file is also missing the "trivially final" positives for `Arrow` (line 27), `Dot` (33), `Square` (39), `Ring` (45) and `Point` (60).

   **Fix:** Either delete these comments, or change the convention so that positives for other rules *may* be marked.

5. **Some notes look like result comments (medium confidence).** These explanatory notes begin with `// IMM-RULE`, but they are not results and are not attached to any code:
   - `finalfields/FinalInterfaceSatisfied.java:5`
   - `supertypes/SuperinterfaceOfClass.java:9`
   - `anonymous/LambdaSatisfied.java:5`
   - `anonymous/LambdaViolated.java:5`
   - `anonymous/LambdaUsage.java:5`

   A harness that matches `// IMM-` could misread them. **Fix:** Start them with something like `// Note:`.

6. **IMM-ANONYMOUS comments come before a declaration rather than an expression (low confidence; a judgment call).** This affects `anonymous/AnonymousViolated.java:37-102`, and the "not applicable" comments in `AnonymousSatisfied.java`.
   - Each comment comes before the field or local variable whose initializer contains `new X() {}`.
   - `package-info.java` lists "class instance creation expression" as a place for comments. The expression is on the next line, but the next thing in the syntax tree is the declaration, which is also a place for comments.

   **Fix:** Either have `package-info.java` say that a comment before a declaration also covers a `new` expression in its initializer, or put these comments inline.

7. **One comment does not name its case exactly (low confidence).** `finalfields/FinalInterfaceSatisfied.java:16` says "declared public, static, and final", but the spec's case is "declared final" (spec line 109). See also spec gap 7.

**Outcome (A):** all fixed in commit a6e1936.

- **A1 and A2:** the missing positives were added.
- **A3:** the IMM-INNER comment was moved onto `Step`.
- **A4:** the extra positives were removed. The convention stayed "positives are marked only for the rules a file is testing".
- **A5:** the notes now start with `// NOTE:`.
- **A6:** `package-info.java` now says that a comment before a declaration also covers a `new` expression in its initializer.
- **A7:** settled by spec gap 7. A field that is both declared and implicitly final is reported as declared final.

## B. Missing cases

In each of these cases, the spec says what the result should be, but no test covers it. They are listed most important first.

1. **Types from other files and packages.** No supertype, subtype or field type comes from another package through an import. No subtype rule is tested across files. The spec requires resolving names through "imports, packages, and nested types" (line 15), and the subtype rules need all of the analyzed source. **Add:**
   - an immutable class in package A that classes in package B extend, implement and use as a field type, with both marked and unmarked types in B;
   - an unmarked subclass in a different file from its immutable superclass.
2. **A type that extends or implements an unmarked subinterface of an immutable interface.** There is no test of `class X implements Pet {}` or `interface Y extends Pet {}`, where `Pet` is an unmarked subinterface of `Animal`. Spec line 73 says these are reported "even when a type between it and the immutable type is also reported". This is the interface version of `HighYieldSavings`. **Add** it to `subtypes/SubinterfaceViolated.java`.
3. **Marked types that have errors of their own.**
   - In `@Immutable class A extends Mutable {}` and `@Immutable class B extends A {}`, B satisfies IMM-SUPERCLASS ("a class that is declared immutable").
   - A field whose type is a marked type with errors satisfies IMM-FIELD-TYPE.

   These check that a type is judged only by whether the other type is marked, not by whether that type passes its own checks.
4. **IMM-LAMBDA with `non-sealed` and with sealed interfaces that have no `permits` clause.** Consider `@Immutable non-sealed interface Sub extends Expr {}`. It inherits one abstract method and is not sealed, so it is a functional interface and violates the rule (spec line 178). There is also no test of a sealed interface with no `permits` clause.
5. **Marked inner and local subtypes.** Spec line 150 covers both classes and interfaces, but only `Manager` (an inner class that extends an immutable class) is tested. **Add:**
   - a marked inner class that implements an immutable interface: IMM-SUBINTERFACE satisfied, IMM-INNER violated;
   - a marked local class that extends an immutable class.
6. **`java.lang.Enum` or `java.lang.Record` as a field type.** This should be a violation, because they are exempt only "as superclasses" (spec line 24). `Object` is tested, but these two are not, and an implementation could wrongly allow all three as field types.
7. **A sealed immutable interface whose permitted subtype is unmarked.** This should violate IMM-SUBINTERFACE. Only the class version (`Card`) is tested.
8. **An anonymous class of an unmarked subinterface of an immutable interface.** The anonymous class is "not applicable", and the error is reported on the subinterface (spec line 165). Only the class version (`PostageStamp`) is tested.
9. **A raw immutable generic type, or an array type argument, as a field type.** Both `final Box raw;` and `final Box<int[]> b;` satisfy IMM-FIELD-TYPE ("whatever its type arguments are").
10. **An unmarked local record or enum that implements an immutable interface.** Only the marked local record is tested.
11. **Interfaces with private methods or re-declared abstract methods.**
    - An interface with one abstract method and a private method is still a functional interface.
    - So is `interface B extends A { void m(); }`, where `A` has a default `m`.

**Already covered well:**

- records, and enums with and without constant bodies
- sealed types
- generics and type variables
- arrays, including C-style arrays and varargs
- `String` and boxed types
- static fields
- nested, inner and local classes
- default and static interface methods
- imported, fully qualified and on-demand annotation names
- several levels of inheritance

`var` does not apply, because it cannot be used on fields.

**Outcome (B):** all 11 cases were added in commit f4b5177. Case 1 added the new `crosspackage` test package. Spec gap 6 later added the `annotations` test package, in commit 90f737b.

## Spec gaps

In each of these cases, the spec is silent or ambiguous. The tests already assume an answer for several of them. They are listed most important first.

1. **How many results an indirect subtype gets.** The spec doesn't say whether there is one result per subtype or one per immutable ancestor.
   - For indirect subtypes, the tests use one combined comment: `SubclassSatisfied:29`, `SubclassViolated:26`, `SubinterfaceViolated:36` and `:41`, and `SubinterfaceSatisfied:40`.
   - When a type has several direct supertypes, they use one comment per clause (`LambdaViolated:108-111`).
   - An analyzer that applies each rule once per immutable type would naturally report two results for `SportsCar`.
2. **Where subtype results go.** The spec does not say. The tests put them on the subtype's `extends` or `implements` clause, including the clause that makes the subtype indirect, as for `Puppy` and `Ring`.
3. **Positive results when there is nothing to check.** The tests decide these, but the spec does not:
   - IMM-SUPERINTERFACE for a type with no superinterfaces: the tests use "not applicable" (`SuperinterfaceSatisfied:23`).
   - IMM-SUBCLASS for a non-final class with no subclasses: no result.
   - IMM-INNER for top-level types: no result. `InnerSatisfied` leaves `Registry` and `Archive` unmarked.
   - IMM-ANONYMOUS for each enum constant without a body: a positive result, although the spec never defines positive results for this rule.
4. **IMM-SUBCLASS for an immutable enum whose constants have bodies** (`EnumConstantBodies:9` and `:31`). The enum is not final. Its only subclasses are anonymous, and these rules do not report anonymous subclasses. The spec does not say what result the enum gets.
5. **IMM-LAMBDA when abstract methods come from outside the analyzed source.** An example is `@Immutable interface X extends Runnable {}`. Counting its abstract methods requires information about the library.
6. **`@Immutable` on an annotation interface (`@interface`).** `@Target(TYPE)` allows the annotation there. The spec's definition of "type" (line 17) leaves annotation interfaces out, and the spec does not say whether IMM-LAMBDA applies to annotation elements.
7. **Interface fields that are both declared final and implicitly final.** For an interface field written with `final`, the spec does not say whether to report "declared final" or "implicitly final" (`FinalInterfaceSatisfied:14`).
8. **Fields declared in an enum constant's class body.** The spec does not say whether they are checked as fields of the enum.

**Outcome (spec gaps):** all resolved in `analyses/Immutable.md`, in commit 90f737b. The tests were updated to match.

1. A subtype gets one result for each immutable type it is a subtype of. Types that are not immutable don't count.
2. Each result goes on the `extends` or `implements` clause through which the immutable type is reached. If more than one clause reaches it, the result goes on the first.
3. A positive result is reported only where a rule defines a case for it:
   - an immutable interface with no superinterfaces satisfies IMM-SUPERINTERFACE;
   - a non-final class with no subclasses gets no IMM-SUBCLASS result;
   - IMM-INNER gives a positive result for a static nested type, and no result for a top-level type;
   - IMM-ANONYMOUS reports only errors.
4. Every enum trivially satisfies IMM-SUBCLASS.
5. The analyzed source is assumed to compile, so library class files may be read.
6. A new rule, IMM-ANNOTATION: an annotation interface marked `@Immutable` is an error, and is never immutable.
7. A field that is both declared and implicitly final is reported as declared final.
8. Fields in an enum constant's class body are checked as fields of the enum.

## C. Simplification

1. **Make the comment convention consistent.** Drop the extra positive comments listed in A4, and reword the notes listed in A5.
2. **Move helper types into their own files.** Move shared base types, such as `ImmutableBase`, `ImmutableRoot`, `Account` and `Shape`, into a file in each package that tests no rule, for example `supertypes/Bases.java`. Then the A2 comments are not needed, and you can see that each test file's expected results are complete just by reading it.
3. **Remove duplicate cases.**
   - `LambdaViolated.java:71-77` (`Pair`, a satisfied case in the violated file) repeats `Range` in `LambdaSatisfied`. Make `FirstOnly` extend `Range`, and delete `Pair`.
   - `AnonymousSatisfied.java:40-56` (`Coin`) repeats `Priority.LOW` in `EnumConstantBodies.java`. Move `PENNY` into `EnumConstantBodies`, and delete `Coin` and its two constructors.
   - `MarkedTypes.java:25-27` (the real annotation, fully qualified) is a weaker version of `DecoyAnnotated.java:24-27`. Keep only one.
   - `Generator` in `LambdaUsage` is the same as `Source` in `LambdaViolated`. It could reuse `Source`, but it is also reasonable to keep the file self-contained.
4. **Remove code that isn't needed.** None of this is needed for the code to compile, and javac does not warn about unused local variables or private fields:
   - `FinalClassViolated.java:33-35`, the `hidden()` method;
   - the `.hashCode()` and `.run()` calls in `AnonymousViolated.java` (lines 82, 89, 95, 97 and 104) and in `AnonymousSatisfied.java:36`;
   - the `counter.next()` call in `LambdaUsage.java:37`.
5. **Cut the repeated variants in `fieldtypes`.** All five files repeat the same field variants: a static field, an interface constant, a record component and an enum field. Put those variants once in a single file, for example with primitives for the satisfied cases and `Notebook` for the violated ones. Keep the other files to one class each.
6. **Use one file-naming scheme.** Most files are named `*Satisfied` or `*Violated`. The exceptions are the `fieldtypes` files (named by the kind of type), `SuperinterfaceOfClass`, `EnumConstantBodies`, `LambdaUsage` and the `marker` files.
7. **Use consistent comment wording.**
   - "Marking it is not possible because of IMM-INNER" appears for `Entry`, `Cage` and `Stray`, but not for `Teller` (`SubclassViolated.java:47`).
   - Most comments say "is immutable", but `SubclassViolated.java:54` says "is marked".
   - IMM-SUPERINTERFACE comments say "X is immutable", while IMM-SUPERCLASS comments say "the superclass X is immutable".
   - Some comments cite JLS section numbers and others do not.
   - `MarkedContainer` (`MarkedTypes.java:32`) is the only type in the `marker` files without an IMM-MARK comment.

**Outcome (C):** partly applied, in commit 41bd13a.

- **Applied:**
  - **C1:** done as part of A4 and A5.
  - **C3:** `Pair` and `Coin` were removed. `LowOnly` now extends `Range`.
  - **C4:** the unneeded code was removed.
  - **C7:** the `Teller` note, "the superinterface X", and the IMM-MARK comment on `MarkedContainer`.
- **Skipped:**
  - **C2:** a helper file that tests no rule would not fit the convention of marking the positives for the rules a file tests. And the A2 comments were already added.
  - **C3, the `MarkedTypes` and `DecoyAnnotated` cases:** they test different things, so both stay.
  - **C3, `Generator`:** it stays, so that `LambdaUsage.java` is self-contained.
  - **C5:** testing each field variant with each kind of type gives useful coverage of the combinations.
  - **C6:** descriptive file names are clearer than a strict `*Satisfied`/`*Violated` scheme.
  - **C7, citing the JLS consistently:** not done.
