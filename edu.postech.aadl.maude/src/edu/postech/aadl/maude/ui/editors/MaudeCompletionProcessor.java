/**
 * Copyright (C) The MOMENT project, 2004-2006.
 * http://moment.dsic.upv.es
 * 
 * This file is part of the Maude Simple GUI plug-in.
 * Contributed by Abel G�mez, <agomez@dsic.upv.es>.
 * 
 * The Maude Simple GUI plug-in is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * The Maude Simple GUI plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with the Maude Development Tools plugin, see the file license.txt.
 * If not, write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
 




package edu.postech.aadl.maude.ui.editors;

import org.eclipse.jface.text.ITextViewer;
//import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * @author Abel G�mez. agomez@dsic.upv.es
 *
 */

public class MaudeCompletionProcessor implements IContentAssistProcessor {

	protected final static String[] myProposals = { "fmod", "mod", "is",
			"endfm", "endm", "pr", "protecting", "inc", "including", "ex",
			"extending", "sort", "sorts", "subsort", "subsorts", "omod",
			"endom", "fth", "endfth", "th", "endth", "oth", "endoth", "class",
			"subclass", "subclasses", "op", "ops", "var", "vars", "msg",
			"msgs", "mb", "cmb", "eq", "ceq", "rl", "crl", "if", "then",
			"else", "fi", "true", "false", "s.t.", "such", "that", "assoc",
			"associative", "commutative", "comm", "left", "right", "id:",
			"identity:", "idem", "idempotent", "iter", "iterated", "ctor",
			"constructor", "format", "ditto", "memo", "frozen", "label",
			"metadata", "nonexec", "prec", "precedence", "gather", "strat",
			"strategy", "special", "otherwise", "owise" };

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public ICompletionProposal[] computeCompletionProposals(
		ITextViewer viewer,
		int documentOffset) {
		
		/*
		ICompletionProposal[] result = new ICompletionProposal[myProposals.length];
		
		
		for (int i = 0; i < myProposals.length; i++) {
			result[i] = new CompletionProposal(myProposals[i], 
							documentOffset, 0, myProposals[i].length());
		}
		
		return result;
		*/
		
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	// For Context information 
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(
		ITextViewer viewer,
		int documentOffset) {
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return null;
	}
}
