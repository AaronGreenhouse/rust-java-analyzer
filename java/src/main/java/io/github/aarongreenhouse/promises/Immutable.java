package io.github.aarongreenhouse.promises;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class, interface, enum, or record as immutable: the value of an instance
 * cannot change after it is created, and the type's static fields cannot change
 * after the type is initialized.
 *
 * <p>The rules are checked by the immutable analysis, described in
 * {@code analyses/Immutable.md}. In summary, for a type marked {@code @Immutable}:
 *
 * <ul>
 *   <li>Its superclass and superinterfaces must be immutable. A class may also extend
 *       {@code Object}, and an enum or record its implicit superclass.
 *   <li>All its subclasses, subinterfaces, and implementing classes must be marked
 *       {@code @Immutable}.
 *   <li>All its fields, static and instance, must be final.
 *   <li>The type of each field must be a primitive type or an immutable type. Arrays
 *       and type variables are not allowed.
 *   <li>It must not be an inner class.
 *   <li>It must not be extended or implemented by an anonymous class. An immutable
 *       interface must not be a functional interface, so that no lambda or method
 *       reference can implement it.
 * </ul>
 *
 * <p>This annotation is deliberately not {@link java.lang.annotation.Inherited
 * Inherited}: every subtype of an immutable type must be marked explicitly.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Immutable {}
