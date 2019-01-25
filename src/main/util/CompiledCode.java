package main.util;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Holds a class name and an associated byte array stream of compiled code
 *
 * Created by trung on 5/3/15.
 */
public class CompiledCode extends SimpleJavaFileObject {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private String className;

    public CompiledCode(String className) throws Exception {
        super(new URI(className), Kind.CLASS);
        this.className = className;
    }

    /**
     * Returns class name
     * @return
     *   Class name
     */
    public String getClassName() {
		return className;
	}

    /**
     * Returns the ByteArrayOutputStream that was opened when the object was instantiated
     * @return
     *   The ByteArrayOutputStream
     */
    @Override
    public OutputStream openOutputStream() {
        return baos;
    }

    /**
     * Returns compiled code as a byte array
     * @return
     *   Compiled code
     */
    public byte[] getByteCode() {
        return baos.toByteArray();
    }
}
