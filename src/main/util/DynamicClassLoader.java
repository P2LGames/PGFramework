package main.util;

import java.util.HashMap;
import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

	private ClassLoader parent = DynamicClassLoader.class.getClassLoader();

	private Map<String, CompiledCode> customCompiledCode = new HashMap<>();

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	public void addCode(CompiledCode cc) {
		customCompiledCode.put(cc.getName(), cc);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		CompiledCode cc = customCompiledCode.get(name);
		if (cc == null) {
			return super.findClass(name);
		}
		byte[] byteCode = cc.getByteCode();
		return defineClass(name, byteCode, 0, byteCode.length);
	}

//	@Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException {
//		if (loadedClasses.contains(name) || unavaiClasses.contains(name)) {
//			return super.loadClass(name); // Use default CL cache
//		}
//
//		byte[] newClassData = loadNewClass(name);
//		if (newClassData != null) {
//			loadedClasses.add(name);
//			return loadClass(newClassData, name);
//		} else {
//			unavaiClasses.add(name);
//			return parent.loadClass(name);
//		}
//	}
//
//	protected abstract byte[] loadNewClass(String name);
//
//	@Override
//	protected Class<?> findClass(String name) throws ClassNotFoundException {
//		CompiledCode cc = customCompiledCode.get(name);
//		if (cc == null) {
//			return super.findClass(name);
//		}
//		byte[] byteCode = cc.getByteCode();
//		return defineClass(name, byteCode, 0, byteCode.length);
//	}
//
//	public Class<?> loadClass(byte[] classData, String name) {
//		Class<?> clazz = defineClass(name, classData, 0, classData.length);
//		if (clazz != null) {
//			if (clazz.getPackage() == null) {
//				definePackage(name.replaceAll("\\.\\w+$", ""), null, null, null, null, null, null, null);
//			}
//			resolveClass(clazz);
//		}
//		return clazz;
//	}
//
//	public static String toFilePath(String name) {
//		return name.replaceAll("\\.", "/") + ".class";
//	}
}
