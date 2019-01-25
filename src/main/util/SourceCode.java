package main.util;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * Holds a class name and the associated source code
 *
 * Created by trung on 5/3/15.
 */
public class SourceCode extends SimpleJavaFileObject {
	private String contents = null;
	private String className;

	/**
	 * Constructs a SourceCode object holding the class name and class source code
	 * @param className
	 *   Name of the class represented
	 * @param contents
	 *   Source code for the class
	 */
	public SourceCode(String className, String contents) {
		super(URI.create("string:///" + className.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.contents = contents;
		this.className = className;
	}

	/**
	 * Gets name of class in source code
	 * @return
	 * 	 Class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the source code
	 * @param ignoreEncodingErrors
	 * @return
	 *   Source code
	 */
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return contents;
	}
}
