/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.testplugin.JavaProjectHelper;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.PreferenceConstants;


public class JavaElementLabelsTest17 extends CoreTests {

	private static final Class THIS= JavaElementLabelsTest17.class;

	private IJavaProject fJProject1;

	public JavaElementLabelsTest17(String name) {
		super(name);
	}

	public static Test suite() {
		return setUpTest(new TestSuite(THIS));
	}

	public static Test setUpTest(Test test) {
		return new Java17ProjectTestSetup(test);
	}

	protected void setUp() throws Exception {
		fJProject1= Java17ProjectTestSetup.getProject();

		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		store.setValue(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
	}

	protected void tearDown() throws Exception {
		JavaProjectHelper.clear(fJProject1, Java17ProjectTestSetup.getDefaultClasspath());
	}


	public void testMethodLabelPolymorphicSignatureDeclaration() throws Exception {
		IType methodHandle= fJProject1.findType("java.lang.invoke.MethodHandle");
		IMethod invokeExact= methodHandle.getMethod("invokeExact", new String[] {
				Signature.createArraySignature(Signature.createTypeSignature("java.lang.Object", true), 1)
		});
		
		String lab= JavaElementLabels.getTextLabel(invokeExact, JavaElementLabels.ALL_DEFAULT);
		assertEqualString(lab, "invokeExact(Object...)");
	
		lab= JavaElementLabels.getTextLabel(invokeExact, JavaElementLabels.M_PARAMETER_NAMES);
		assertEqualString(lab, "invokeExact(arg0)");
	
		lab= JavaElementLabels.getTextLabel(invokeExact, JavaElementLabels.M_PARAMETER_TYPES);
		assertEqualString(lab, "invokeExact(Object...)");
		
		lab= JavaElementLabels.getTextLabel(invokeExact, JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PARAMETER_TYPES);
		assertEqualString(lab, "invokeExact(Object... arg0)");
		
		lab= JavaElementLabels.getTextLabel(invokeExact, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeExact(Object...)");
	}

	private IJavaElement createInvokeGenericReference(String invocation) throws CoreException, JavaModelException {
		IPackageFragmentRoot sourceFolder= JavaProjectHelper.addSourceContainer(fJProject1, "src");
		
		IPackageFragment pack1= sourceFolder.createPackageFragment("org.test", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package org.test;\n");
		buf.append("import java.lang.invoke.MethodHandle;\n");
		buf.append("public class Test {\n");
		buf.append("    void foo(MethodHandle mh) throws Throwable {\n");
		buf.append("        " + invocation + ";\n");
		buf.append("    }\n");
		buf.append("}\n");
		String content= buf.toString();
		ICompilationUnit cu= pack1.createCompilationUnit("Test.java", content, false, null);
		
		IJavaElement elem= cu.codeSelect(content.indexOf("invokeGeneric"), 0)[0];
		return elem;
	}

	private static void assertInvokeGenericUnresolved(IJavaElement elem) {
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT);
		assertEqualString(lab, "invokeGeneric(Object...)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_NAMES);
		assertEqualString(lab, "invokeGeneric(arg0)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES);
		assertEqualString(lab, "invokeGeneric(Object...)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PRE_RETURNTYPE);
		assertEqualString(lab, "Object invokeGeneric(Object... arg0)");
	}

	public void testMethodLabelPolymorphicSignatureReference0() throws Exception {
		IJavaElement elem= createInvokeGenericReference("mh.invokeGeneric()");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric()");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric()");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric()");
	}

	public void testMethodLabelPolymorphicSignatureReference0Ret() throws Exception {
		IJavaElement elem= createInvokeGenericReference("String s= (String) mh.invokeGeneric()");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric()");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric()");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric()");
	}
	
	public void testMethodLabelPolymorphicSignatureReference1() throws Exception {
		IJavaElement elem= createInvokeGenericReference("mh.invokeGeneric(1)");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(int)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(int)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric(int arg00)");
	}
	
	public void testMethodLabelPolymorphicSignatureReference1Array() throws Exception {
		IJavaElement elem= createInvokeGenericReference("mh.invokeGeneric(new Object[42])");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(Object[])");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(Object[])");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric(Object[] arg00)");
	}
	
	public void testMethodLabelPolymorphicSignatureReference2() throws Exception {
		IJavaElement elem= createInvokeGenericReference("mh.invokeGeneric('a', new Integer[0][])");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(char, Integer[][])");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(char, Integer[][])");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric(char arg00, Integer[][] arg01)");
	}
	
	public void testMethodLabelPolymorphicSignatureReference3Ret() throws Exception {
		IJavaElement elem= createInvokeGenericReference("long l= (long) mh.invokeGeneric('a', new java.util.ArrayList<String>(), null)");
		
		assertInvokeGenericUnresolved(elem);
		
		String lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(char, ArrayList, Void)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "invokeGeneric(char, ArrayList, Void)");
		
		lab= JavaElementLabels.getTextLabel(elem, JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_PRE_RETURNTYPE | JavaElementLabels.USE_RESOLVED);
		assertEqualString(lab, "Object invokeGeneric(char arg00, ArrayList arg01, Void arg02)");
	}
	
}
