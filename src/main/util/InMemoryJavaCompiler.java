package main.util;

import java.util.*;

import javax.tools.*;

/**
 * Compile Java sources in-memory
 */
public class InMemoryJavaCompiler {
	private JavaCompiler javac;
	private DynamicClassLoader classLoader;
	private Iterable<String> options;
	boolean ignoreWarnings = false;

	private Map<String, SourceCode> sourceCodes = new HashMap<String, SourceCode>();

	public static InMemoryJavaCompiler newInstance() {
		return new InMemoryJavaCompiler();
	}

	private InMemoryJavaCompiler() {
		this.javac = ToolProvider.getSystemJavaCompiler();
		this.classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
//		this.classLoader = new DynamicClassLoader("target/classes");
	}

	/**
	 * Changes the classloader used internally
	 * @param parent
	 * 	 The parent classloader
	 * @return
	 *   The InMemoryJavaCompiler that uses the new classloader
	 */
	public InMemoryJavaCompiler useParentClassLoader(ClassLoader parent) {
		this.classLoader = new DynamicClassLoader(parent);
		return this;
	}

	/**
	 * @return the class loader used internally by the compiler
	 */
	public ClassLoader getClassloader() {
		return classLoader;
	}

	/**
	 * Options used by the compiler, e.g. '-Xlint:unchecked'.
	 *
	 * @param options
	 *   The new options to use
	 * @return
	 *   The InMemoryJavaCompiler that uses the new classloader
	 */
	public InMemoryJavaCompiler useOptions(String... options) {
		this.options = Arrays.asList(options);
		return this;
	}

	/**
	 * Ignore non-critical compiler output, like unchecked/unsafe operation
	 * warnings.
	 *
	 * @return
	 *    The InMemoryJavaCompiler that ignores warnings
	 */
	public InMemoryJavaCompiler ignoreWarnings() {
		ignoreWarnings = true;
		return this;
	}

	/**
	 * Compile all sources
	 *
	 * @return Map containing instances of all compiled classes
	 * @throws Exception
	 *    Throws exception if code is unable to be compiled
	 */
	public Map<String, Class<?>> compileAll() throws Exception {
		if (sourceCodes.size() == 0) {
			throw new CompilationException("No source code to compile");
		}

		// Get all of the source codes
		Collection<SourceCode> compilationUnits = sourceCodes.values();

		// Track the compiled codes
		CompiledCode[] code = new CompiledCode[compilationUnits.size()];

		// Create an iterator for the source codes
		Iterator<SourceCode> iter = compilationUnits.iterator();

		// Loop through all of the source codes
		for (int i = 0; i < code.length; i++) {
			// Create a new compiled code for each class
			code[i] = new CompiledCode(iter.next().getClassName());
		}

		// Create a diagnostics collector
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

		// Create an extended file manager
		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), classLoader);

		// Create a task to compile all of the source code
		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, collector, options, null, compilationUnits);

		// Start the task
		boolean result = task.call();

		// If the result was false, or we have some diagnostics information
		if (!result || collector.getDiagnostics().size() > 0) {
			// Then we have a problem, and we need to get a solid report for the user
			StringBuffer exceptionMsg = new StringBuffer();

			// Unable to compile source
			exceptionMsg.append("Unable to compile the source");

			// Track whether or not we have warnings or errors
			boolean hasWarnings = false;
			boolean hasErrors = false;

			// Loop through the diagnostics problems, and track which one we got
			for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
				switch (d.getKind()) {
				case NOTE:
				case MANDATORY_WARNING:
				case WARNING:
					hasWarnings = true;
					break;
				case OTHER:
				case ERROR:
				default:
					hasErrors = true;
					break;
				}

				// We also append the kind and warning message we recieve from the diagnostics
				exceptionMsg.append("\n").append("[kind=").append(d.getKind());
				exceptionMsg.append(", ").append("line=").append(d.getLineNumber());
				exceptionMsg.append(", ").append("message=").append(d.getMessage(Locale.US)).append("]");
			}

			// If we had a warning and are not ignoring them, or if we had errors
			if (hasWarnings && !ignoreWarnings || hasErrors) {
				// Throw a new compilation exception with the message we just compiled above
				throw new CompilationException(exceptionMsg.toString());
			}
		}

		// Create a new map of class names to source codes
		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		for (String className : sourceCodes.keySet()) {
			classes.put(className, classLoader.loadClass(className));
		}

		// Return the classes
		return classes;
	}

	/**
	 * Compile single source
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 */
	public Class<?> compile(String className, String sourceCode) throws Exception {
		return addSource(className, sourceCode).compileAll().get(className);
	}

	/**
	 * Add source code to the compiler
	 *
	 * @param className
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 * @see {@link #compileAll()}
	 */
	public InMemoryJavaCompiler addSource(String className, String sourceCode) throws Exception {
		sourceCodes.put(className, new SourceCode(className, sourceCode));
		return this;
	}
}
