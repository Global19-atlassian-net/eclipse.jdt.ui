/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.base;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * An abstract default implementation for a change object - suitable for subclassing. This class manages
 * the change's active status.
 * <p>
 * <bf>NOTE:<bf> This class/interface is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being made available at 
 * this early stage to solicit feedback from pioneering adopters on the understanding that any 
 * code that uses this API will almost certainly be broken (repeatedly) as the API evolves.</p>
 */
public abstract class Change implements IChange {

	private boolean fIsActive= true;

	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public RefactoringStatus aboutToPerform(ChangeContext context, IProgressMonitor pm) {
		pm.beginTask("", 1); //$NON-NLS-1$
		// PR: 1GEWDUH: ITPJCORE:WINNT - Refactoring - Unable to undo refactor change
		RefactoringStatus result= new RefactoringStatus();
		IResource resource= getResource(getModifiedLanguageElement());
		if (resource != null) {
			pm.subTask(RefactoringCoreMessages.getFormattedString("Change.checking_for", resource.getName())); //$NON-NLS-1$
			checkIfModifiable(resource, result, context);
		}
		pm.worked(1);
		return result;
	}
	
	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public void performed() {
		// do nothing.
	} 
	
	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public void setActive(boolean active) {
		fIsActive= active;
	}
	
	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public boolean isActive() {
		return fIsActive;
	}

	/* (Non-Javadoc)
	 * Method declared in IChange.
	 */
	public boolean isUndoable() {
		return true;
	}
		
	/* (Non-Javadoc)
	 * debugging only
	 */	
	public String toString(){
		return getName();
	}
	
	/**
	 * Handles the given exception using the <code>IChangeExceptionHandler</code> provided by
	 * the given change context. If the execution of the change is to be aborted than
	 * this method throws a corresponding <code>JavaModelException</code>. The exception
	 * is either the given exception if it is an instance of <code>JavaModelException</code> or
	 * a new one created by calling <code>new JavaModelException(exception, code)</code>.
	 * 
	 * @param context the change context used to retrieve the exception handler
	 * @param exception the exception caugth during change execution
	 * @exception <code>ChangeAbortException</code> if the execution is to be aborted
	 */
	protected void handleException(ChangeContext context, Exception exception) throws ChangeAbortException {
		if (exception instanceof ChangeAbortException)
			throw (ChangeAbortException)exception;
		if (exception instanceof OperationCanceledException)
			throw (OperationCanceledException)exception;
		context.getExceptionHandler().handle(context, this, exception);
	}
	
	protected static void checkIfModifiable(Object element, RefactoringStatus status, ChangeContext context) {
		IResource resource= getResource(element);
		if (resource != null)
			checkIfModifiable(resource, status, context);
	}
	
	protected static void checkIfModifiable(IResource resource, RefactoringStatus status, ChangeContext context) {
		if (resource.isReadOnly()) {
			status.addFatalError(RefactoringCoreMessages.getFormattedString("Change.is_read_only", resource.getFullPath().toString())); //$NON-NLS-1$
		}
		if (resource instanceof IFile)
			context.checkUnsavedFile(status, (IFile)resource);
	}

	protected static void handleJavaModelException(JavaModelException e, RefactoringStatus status) {
		if (e.isDoesNotExist()) {
			// If the Java Element doesn't exist then it can't be unsaved.
			return;
		}
		status.addFatalError(RefactoringCoreMessages.getString("Change.unexpected_exception")); //$NON-NLS-1$
		JavaCore.getJavaCore().getLog().log(
			new Status(
				IStatus.ERROR, 
				JavaCore.getJavaCore().getDescriptor().getUniqueIdentifier(), 
				IStatus.ERROR, 
				RefactoringCoreMessages.getString("Change.internal_Error"), e));		 //$NON-NLS-1$
	}
	
	private static IResource getResource(Object element) {
		if (element instanceof IResource) {
			return (IResource)element;
		} 
		if (element instanceof ICompilationUnit) {
			ICompilationUnit cunit= (ICompilationUnit)element;
			if (cunit.isWorkingCopy())
				element= cunit.getOriginalElement();
		}
		if (element instanceof IAdaptable) {
			return (IResource) ((IAdaptable)element).getAdapter(IResource.class);
		}
		return null;
	}	
}
