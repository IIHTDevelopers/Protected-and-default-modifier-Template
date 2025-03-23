package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code demonstrates proper usage of protected and default modifiers
	public boolean testProtectedDefaultModifiers(String filePath) throws IOException {
		System.out.println("Starting testProtectedDefaultModifiers with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean animalExtendsCorrectly = new AtomicBoolean(false); // To check if Dog extends Animal
		AtomicBoolean protectedSpeakMethodFound = new AtomicBoolean(false);
		AtomicBoolean defaultRunMethodFound = new AtomicBoolean(false);
		AtomicBoolean dogMethodFound = new AtomicBoolean(false);
		AtomicBoolean methodsExecutedInMain = new AtomicBoolean(false);

		// Check for class implementation, inheritance, and method accessibility
		System.out.println("------ Class and Method Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
					// Check for protected method 'speak' and default method 'run'
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("speak") && method.isProtected()) {
							protectedSpeakMethodFound.set(true);
							System.out.println("Protected method 'speak' found.");
						}
						if (method.getNameAsString().equals("run") && !method.isPublic() && !method.isProtected()
								&& !method.isPrivate()) {
							defaultRunMethodFound.set(true);
							System.out.println("Default method 'run' found.");
						}
					});
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);
					// Check if Dog extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						animalExtendsCorrectly.set(true);
						System.out.println("Class 'Dog' extends 'Animal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Animal'.");
					}
					// Check for dog-specific methods
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("dogAction") && method.isPublic()) {
							dogMethodFound.set(true);
							System.out.println("Public method 'dogAction' found.");
						}
					});
				}
			}
		}

		// Ensure all classes and methods exist and that the inheritance structure is
		// correct
		if (!animalClassFound.get() || !dogClassFound.get()) {
			System.out.println("Error: Class 'Animal' or 'Dog' not found.");
			return false;
		}

		if (!animalExtendsCorrectly.get()) {
			System.out.println("Error: 'Dog' class must extend 'Animal'.");
			return false;
		}

		if (!protectedSpeakMethodFound.get()) {
			System.out.println("Error: 'speak' method is not protected in 'Animal' class.");
			return false;
		}

		if (!defaultRunMethodFound.get()) {
			System.out.println("Error: 'run' method is not default (package-private) in 'Animal' class.");
			return false;
		}

		if (!dogMethodFound.get()) {
			System.out.println("Error: 'dogAction' method is not public in 'Dog' class.");
			return false;
		}

		// Check if methods are executed in the main method
		System.out.println("------ Method Execution Check in Main ------");

		AtomicBoolean speakExecuted = new AtomicBoolean(false);
		AtomicBoolean dogActionExecuted = new AtomicBoolean(false);

		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak")) {
							speakExecuted.set(true);
							System.out.println("Method 'speak' is executed in the main method.");
						}
						if (callExpr.getNameAsString().equals("dogAction")) {
							dogActionExecuted.set(true);
							System.out.println("Method 'dogAction' is executed in the main method.");
						}
					});
				}
			}
		}

		// Check for missing method executions
		if (!speakExecuted.get()) {
			System.out.println("Error: 'speak' method not executed in the main method.");
		}

		if (!dogActionExecuted.get()) {
			System.out.println("Error: 'dogAction' method not executed in the main method.");
		}

		// Fail the test if either method wasn't executed
		if (!speakExecuted.get() || !dogActionExecuted.get()) {
			return false;
		}

		// If all checks pass
		System.out.println("Test passed: Protected and default modifiers are correctly implemented.");
		return true;
	}
}
