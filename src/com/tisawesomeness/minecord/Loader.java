package com.tisawesomeness.minecord;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Loader implements Runnable {

	private final static boolean propagate = false;
	private final static String botClass = "com.tisawesomeness.minecord.Bot";
	private String[] args;
	
	public Loader(String[] args) {
		this.args = args;
	}
	
	public void run() {
		
		//Clear references
		DynamicLoader dl;
		
		//Dynamically start a new bot
		dl = new DynamicLoader(Main.cl);
		if (propagate) Thread.currentThread().setContextClassLoader(dl);
		Class<?> clazz = dl.loadClass(botClass);
		try {
			clazz.getMethods()[1].invoke(null, args, true);
		} catch (ClassCastException ex) {
			//Do nothing
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	// Adapted from https://github.com/evacchi/class-reloader
 	static class DynamicLoader extends ClassLoader {

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
 				} catch (ClassNotFoundException ex) {
 					ex.printStackTrace(System.out);
 				}
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
				 byte[] buff = new byte[size];
				 try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
					 dis.readFully(buff);
				 }
				 return buff;
			 } catch (Exception ex) {
				 throw new IOException(ex);
			 }
		 }
 		
 	}
	
}