package com.tisawesomeness.minecord;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

// Adapted from https://github.com/evacchi/class-reloader
public class DynamicLoader extends ClassLoader {

	private ClassLoader orig;
	DynamicLoader(ClassLoader orig) {
		this.orig = orig;
	}

	@Override
	public Class<?> loadClass(String s) {
		return findClass(s);
	}

	@Override
	public Class<?> findClass(String s) {
		try {
			byte[] bytes = loadClassData(s);
			return defineClass(s, bytes, 0, bytes.length);
		} catch (IOException ioe) {
			try {
				return super.loadClass(s);
			} catch (ClassNotFoundException ignore) {}
			ioe.printStackTrace(System.out);
			return null;
		}
	}

	private byte[] loadClassData(String className) throws IOException {
		try {
			Class<?> clazz = orig.loadClass(className);
			String name = clazz.getName();
			name = name.substring(name.lastIndexOf('.') + 1);
			URL url = clazz.getResource(name + ".class");
			File f = new File(url.toURI());
			int size = (int) f.length();
			byte buff[] = new byte[size];
			try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
				dis.readFully(buff);
			}
			return buff;
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

}