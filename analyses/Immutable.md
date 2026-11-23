# Immutable analysis

## Summary

Assures that a class definition is such that all instances of the class are immutable objects.  That is the value of an object of the class cannot change after it is created.  Here we also consider the property to apply to the `static` portion of the class.

## Description

This is a purely syntactic analysis.  That is, it is verified solely by looking at how classes and fields are declared.

### Marker

Rule: A class or interface is considered immutable if and only if its declaration contains the @Immutable annotation type.

### Inheritance

Rule: If an interface is immutable then all its subinterfaces and implementing classes must be immutable.  It is an error if there exists a subinterface or implementing class that is not marked as immutable.  Subinterfaces and implementing classes that are marked as immutable should receive a positive message or marking that they satisfy the requirement.

Rule: If a class is immutable then all its subclasses must be immutable.  It is an error if there exists a subclass that is not marked as immutable.  Subclasses that are marked as immutable should receive a positive message or marking that they satisfy the requirement.

### Fields must be final

Rule: If an interface is immutable, then all its declared fields must be final.  Note: this is trivially true by the requirements of the Java Language Standard.  That is, it is impossible for this to not be true, but it is worth checking to be explicit.  

Rule: If a class is immutable, then all its declared fields must be final.  It is an error for a field not be declared final.

Fields that are declared final are positively marked as satisfying the requirement.

### Referenced types must final

Rule: If an interface or class is immutable, the type of each declared field must be either
- a Java primitive type
- an interface that is declared immutable
- a class that is declared immutable

It is an error if a field does not satisfy one of these cases.

A field that does satisfy one of these cases should be positively marked as to which case it satisfies.
