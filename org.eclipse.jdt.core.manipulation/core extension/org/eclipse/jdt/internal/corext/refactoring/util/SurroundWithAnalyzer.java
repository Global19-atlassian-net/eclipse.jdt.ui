/*******************************************************************************

 * Copyright (c) 2005, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.util;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import org.eclipse.jdt.internal.core.manipulation.JavaManipulationMessages;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Selection;

public class SurroundWithAnalyzer extends CodeAnalyzer {

	private VariableDeclaration[] fLocals;
	private boolean fSurroundWithTryCatch;

	public SurroundWithAnalyzer(ICompilationUnit cunit, Selection selection, boolean surroundWithTryCatch) throws CoreException {
		super(cunit, selection, false);
		fSurroundWithTryCatch= surroundWithTryCatch;
	}

	public ASTNode[] getValidSelectedNodes() {
		if (hasSelectedNodes()) {
			return internalGetSelectedNodes().toArray(new ASTNode[internalGetSelectedNodes().size()]);
		} else {
			return new Statement[0];
		}
	}

	public VariableDeclaration[] getAffectedLocals() {
		return fLocals;
	}

	public BodyDeclaration getEnclosingBodyDeclaration() {
		ASTNode node= getFirstSelectedNode();
		if (node == null)
			return null;
		return ASTNodes.getParent(node, BodyDeclaration.class);
	}

	@Override
	protected boolean handleSelectionEndsIn(ASTNode node) {
		return true;
	}

	@Override
	public void endVisit(CompilationUnit node) {
		postProcessSelectedNodes(internalGetSelectedNodes());
		ASTNode enclosingNode= null;
		superCall: {
			if (getStatus().hasFatalError())
				break superCall;
			if (!hasSelectedNodes()) {
				ASTNode coveringNode= getLastCoveringNode();
				if (coveringNode instanceof Block) {
					Block block= (Block)coveringNode;
					Message[] messages= ASTNodes.getMessages(block, ASTNodes.NODE_ONLY);
					if (messages.length > 0) {
						invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_compile_errors,
							JavaStatusContext.create(getCompilationUnit(), block));
						break superCall;
					}
				}
				invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_doesNotCover);
				break superCall;
			}
			enclosingNode= getEnclosingNode(getFirstSelectedNode());
			boolean isValidEnclosingNode= enclosingNode instanceof MethodDeclaration || enclosingNode instanceof Initializer;
			if (fSurroundWithTryCatch) {
				isValidEnclosingNode= isValidEnclosingNode || enclosingNode instanceof MethodReference || enclosingNode.getLocationInParent() == LambdaExpression.BODY_PROPERTY;
			}
			if (!isValidEnclosingNode) {
				invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_doesNotContain);
				break superCall;
			}
			if (!validSelectedNodes()) {
				invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_onlyStatements);
			}
			fLocals= LocalDeclarationAnalyzer.perform(enclosingNode, getSelection());
		}
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		if (getSelection().getEndVisitSelectionMode(node) == Selection.SELECTED) {
			invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_cannotHandleSuper, JavaStatusContext.create(fCUnit, node));
		}
		super.endVisit(node);
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		if (getSelection().getEndVisitSelectionMode(node) == Selection.SELECTED) {
			invalidSelection(JavaManipulationMessages.SurroundWithTryCatchAnalyzer_cannotHandleThis, JavaStatusContext.create(fCUnit, node));
		}
		super.endVisit(node);
	}

	protected void postProcessSelectedNodes(List<ASTNode> selectedNodes) {
		if (selectedNodes == null || selectedNodes.isEmpty())
			return;
		if (selectedNodes.size() == 1) {
			ASTNode node= selectedNodes.get(0);
			if (node instanceof Expression && node.getParent() instanceof ExpressionStatement) {
				selectedNodes.clear();
				selectedNodes.add(node.getParent());
			}
		}
	}

	private boolean validSelectedNodes() {
		for (ASTNode node : getSelectedNodes()) {
			boolean isValidNode= node instanceof Statement;
			if (fSurroundWithTryCatch) { // allow method reference and lambda's expression body also
				isValidNode= isValidNode || node instanceof MethodReference || node.getLocationInParent() == LambdaExpression.BODY_PROPERTY;
			}
			if (!isValidNode)
				return false;
		}
		return true;
	}

	public static ASTNode getEnclosingNode(ASTNode firstSelectedNode) {
		ASTNode enclosingNode;
		if (firstSelectedNode instanceof MethodReference) {
			enclosingNode= firstSelectedNode;
		} else {
			enclosingNode= ASTResolving.findEnclosingLambdaExpression(firstSelectedNode);
			if (enclosingNode != null) {
				enclosingNode= ((LambdaExpression) enclosingNode).getBody();
			} else {
				enclosingNode= ASTNodes.getParent(firstSelectedNode, BodyDeclaration.class);
			}
		}
		return enclosingNode;
	}

}
