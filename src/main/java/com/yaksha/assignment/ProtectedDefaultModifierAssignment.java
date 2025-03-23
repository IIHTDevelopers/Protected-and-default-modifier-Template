package com.yaksha.assignment;

// Animal class demonstrating protected and default modifiers
class Animal {

	// Protected method that can be accessed within the same package or by
	// subclasses
	protected void speak() {
		System.out.println("The animal makes a sound.");
	}

	// Default method (package-private) that can only be accessed within the same
	// package
	void run() {
		System.out.println("The animal runs.");
	}
}

// Dog class - Inherits from Animal and demonstrates access to protected method
class Dog extends Animal {

	// Overriding the protected speak method from Animal class
	@Override
	protected void speak() {
		System.out.println("The dog barks.");
	}

	// Accessing the default (package-private) method of Animal class
	public void dogAction() {
		run(); // Invoking the default method within the same package
	}
}

public class ProtectedDefaultModifierAssignment {
	public static void main(String[] args) {
		Dog dog = new Dog(); // Creating a Dog object
		dog.speak(); // Should print "The dog barks." (protected method overridden)
		dog.dogAction(); // Should print "The animal runs." (accessing default method from Animal)
	}
}
