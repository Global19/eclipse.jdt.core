/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.regression;
import java.io.File;
import java.io.IOException;

import junit.framework.Test;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.core.util.ClassFileBytesDisassembler;

public class ArrayTest extends AbstractRegressionTest {

	public ArrayTest(String name) {
		super(name);
	}

	public static Test suite() {
		return setupSuite(testClass());
	}
	
	public static Class testClass() {
		return ArrayTest.class;
	}

public void test001() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  int[] x= new int[] {,};\n" + 
		"}\n",
	});
}

/**
 * http://dev.eclipse.org/bugs/show_bug.cgi?id=28615
 */
public void test002() {
	this.runConformTest(
		new String[] {
			"A.java",
			"public class A {\n" +
			"    public static void main(String[] args) {\n" +
			"        float[] tab = new float[] {-0.0f};\n" +
			"        System.out.print(tab[0]);\n" +
			"    }\n" +
			"}",
		},
		"-0.0");
}
/**
 * http://dev.eclipse.org/bugs/show_bug.cgi?id=28615
 */
public void test003() {
	this.runConformTest(
		new String[] {
			"A.java",
			"public class A {\n" +
			"    public static void main(String[] args) {\n" +
			"        float[] tab = new float[] {0.0f};\n" +
			"        System.out.print(tab[0]);\n" +
			"    }\n" +
			"}",
		},
		"0.0");
}
/**
 * http://dev.eclipse.org/bugs/show_bug.cgi?id=28615
 */
public void test004() {
	this.runConformTest(
		new String[] {
			"A.java",
			"public class A {\n" +
			"    public static void main(String[] args) {\n" +
			"        int[] tab = new int[] {-0};\n" +
			"        System.out.print(tab[0]);\n" +
			"    }\n" +
			"}",
		},
		"0");
}
/**
 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=37387
 */
public void test005() {
	this.runConformTest(
		new String[] {
			"X.java",
			"public class X {\n" +
			"	 private static final Object X[] = new Object[]{null,null};\n" +
			"    public static void main(String[] args) {\n" +
			"		System.out.println(\"SUCCESS\");\n" +
			"    }\n" +
			"}\n",
		},
		"SUCCESS");
		
	ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
	String actualOutput = null;
	try {
		byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(new File(OUTPUT_DIR + File.separator  +"X.class"));
		actualOutput =
			disassembler.disassemble(
				classFileBytes,
				"\n",
				ClassFileBytesDisassembler.DETAILED); 
	} catch (org.eclipse.jdt.core.util.ClassFormatException e) {
		assertTrue("ClassFormatException", false);
	} catch (IOException e) {
		assertTrue("IOException", false);
	}
	
	String expectedOutput = 
		"  // Method descriptor  #7 ()V\n" + 
		"  // Stack: 1, Locals: 0\n" + 
		"  static {};\n" + 
		"    0  iconst_2\n" + 
		"    1  anewarray #4 java.lang.Object\n" + 
		"    4  putstatic #10 <Field X#X java.lang.Object[]>\n" + 
		"    7  return\n" + 
		"      Line numbers:\n" + 
		"        [pc: 0, line: 2]\n" + 
		"        [pc: 7, line: 1]\n";
		
	if (actualOutput.indexOf(expectedOutput) == -1) {
		System.out.println(Util.displayString(actualOutput, 2));
	}
	assertTrue("unexpected bytecode sequence", actualOutput.indexOf(expectedOutput) != -1);
}

}
