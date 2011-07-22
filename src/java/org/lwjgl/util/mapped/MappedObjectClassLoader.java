/*
 * Copyright (c) 2002-2011 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.util.mapped;

import org.lwjgl.LWJGLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * This classloader is responsible for applying the bytecode transformation to mapped objects.
 * The transformation can either be applied using a Java agent, or with the convenient {@link #fork} method.
 *
 * @author Riven
 */
public class MappedObjectClassLoader extends URLClassLoader {

	static final String MAPPEDOBJECT_PACKAGE_PREFIX;

	static {
		MAPPEDOBJECT_PACKAGE_PREFIX = MappedObjectClassLoader.class.getPackage().getName() + ".";
	}

	static boolean FORKED;

	/**
	 * Forks the specified class containing a main method, passing the specified arguments. See
	 * {@link org.lwjgl.test.mapped.TestMappedObject} for example usage.
	 *
	 * @param mainClass the class containing the main method
	 * @param args      the arguments to pass
	 *
	 * @return true if the fork was successful.
	 */
	public static boolean fork(Class<?> mainClass, String[] args) {
		if ( FORKED ) {
			return false;
		}

		FORKED = true;

		try {
			URLClassLoader loader = new MappedObjectClassLoader(mainClass);
			Class<?> replacedMainClass = loader.loadClass(mainClass.getName());
			Method mainMethod = replacedMainClass.getMethod("main", String[].class);
			mainMethod.invoke(null, new Object[] { args });
		} catch (InvocationTargetException exc) {
			Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), exc.getCause());
		} catch (Throwable cause) {
			throw new Error("failed to fork", cause);
		}

		return true;
	}

	private MappedObjectClassLoader(Class<?> mainClass) {
		super(((URLClassLoader)mainClass.getClassLoader()).getURLs());
	}

	private static long total_time_transforming;

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if ( name.startsWith("java.") )
			return super.loadClass(name, resolve);
		if ( name.startsWith("javax.") )
			return super.loadClass(name, resolve);

		if ( name.startsWith("sun.") )
			return super.loadClass(name, resolve);
		if ( name.startsWith("sunw.") )
			return super.loadClass(name, resolve);

		// never transform classes in this very package, sub-packages should be transformed
		if ( name.startsWith(MAPPEDOBJECT_PACKAGE_PREFIX) )
			if ( name.substring(MAPPEDOBJECT_PACKAGE_PREFIX.length()).indexOf('.') == -1 )
				return super.loadClass(name, resolve);

		String className = name.replace('.', '/');

		if ( MappedObjectTransformer.PRINT_ACTIVITY )
			LWJGLUtil.log(MappedObjectClassLoader.class.getSimpleName() + ": " + className);

		byte[] bytecode = readStream(this.getResourceAsStream(className.concat(".class")));

		long t0 = System.nanoTime();
		bytecode = MappedObjectTransformer.transformMappedAPI(className, bytecode);
		long t1 = System.nanoTime();
		total_time_transforming += (t1 - t0);

		if ( MappedObjectTransformer.PRINT_TIMING )
			LWJGLUtil.log("transforming " + className + " took " + (t1 - t0) / 1000 + " micros (total: " + (total_time_transforming / 1000 / 1000) + "ms)");

		Class<?> clazz = super.defineClass(name, bytecode, 0, bytecode.length);
		if ( resolve )
			resolveClass(clazz);
		return clazz;
	}

	private static byte[] readStream(InputStream in) {
		byte[] bytecode = new byte[256];
		int len = 0;
		try {
			while ( true ) {
				if ( bytecode.length == len )
					bytecode = Arrays.copyOf(bytecode, len * 2);
				int got = in.read(bytecode, len, bytecode.length - len);
				if ( got == -1 )
					break;
				len += got;
			}
		} catch (IOException exc) {
			// stop!
		} finally {
			try {
				in.close();
			} catch (IOException exc) {
				// ignore...
			}
		}
		return Arrays.copyOf(bytecode, len);
	}

}