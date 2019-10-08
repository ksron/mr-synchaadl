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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * @author Abel G�mez. agomez@dsic.upv.es
 *
 */


public class MaudeRuleScanner extends RuleBasedScanner {

	public static String[] keyMod = { "fmod", "mod", "is", "endfm",
			"endm", "pr", "protecting", "inc", "including", "ex", "extending",
			"sort", "sorts", "subsort", "subsorts", "omod", "endom", "fth",
			"endfth", "th", "endth", "oth", "endoth", "class", "subclass",
			"subclasses" };

	public static String[] keyModElt = { "op", "ops", "var", "vars", "msg",
			"msgs" };

	public static String[] keyStat = { "mb", "cmb", "eq", "ceq", "rl",
			"crl", "if", "then", "else", "fi", "true", "false", "s.t.", "such",
			"that" };

	public static String[] keyAttr = { "assoc", "associative",
			"commutative", "comm", "left", "right", "id:", "identity:", "idem",
			"idempotent", "iter", "iterated", "ctor", "constructor", "format",
			"ditto", "memo", "frozen", "label", "metadata", "nonexec", "prec",
			"precedence", "gather", "strat", "strategy", "special",
			"otherwise", "owise" };


	private static Color KEY_MOD_COLOR = new Color(Display.getCurrent(), new RGB(128, 0, 0));
	private static Color KEY_MODELT_COLOR = new Color(Display.getCurrent(), new RGB(128, 0, 128));
	private static Color KEY_STAT_COLOR = new Color(Display.getCurrent(), new RGB(128, 128, 0));
	private static Color KEY_ATTR_COLOR = new Color(Display.getCurrent(), new RGB(0, 128, 128));
	private static Color COMMENT_COLOR = new Color(Display.getCurrent(), new RGB(0, 128, 0));
	private static Color STRING_COLOR = new Color(Display.getCurrent(), new RGB(0, 0, 128));

	public MaudeRuleScanner() {
		IToken keyModToken = new Token(new TextAttribute(KEY_MOD_COLOR,null,SWT.BOLD));
		IToken keyModEltToken = new Token(new TextAttribute(KEY_MODELT_COLOR,null,SWT.BOLD));
		IToken keyStatToken = new Token(new TextAttribute(KEY_STAT_COLOR,null,SWT.BOLD));
		IToken keyAttrToken = new Token(new TextAttribute(KEY_ATTR_COLOR,null,SWT.BOLD));
		IToken commentToken = new Token(new TextAttribute(COMMENT_COLOR));
		IToken stringToken = new Token(new TextAttribute(STRING_COLOR));


		IWordDetector wordDet = new IWordDetector() {
			public boolean isWordStart(char c) {
				return (Character.isJavaIdentifierStart(c));
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		};

		WordRule keyModRule = new WordRule(wordDet);
		WordRule keyModEltRule = new WordRule(wordDet);
		WordRule keyStatRule = new WordRule(wordDet);
		WordRule keyAttrRule = new WordRule(wordDet);

		// add tokens for each module reserved word
		for (int i = 0; i < keyMod.length; i++) {
			keyModRule.addWord(keyMod[i], keyModToken);
		}
				
		// add tokens for each module element reserved word
		for (int i = 0; i < keyModElt.length; i++) {
			keyModRule.addWord(keyModElt[i], keyModEltToken);
		}

		// add tokens for each statement reserved word
		for (int i = 0; i < keyStat.length; i++) {
			keyModRule.addWord(keyStat[i], keyStatToken);
		}

		// add tokens for each attribute reserved word
		for (int i = 0; i < keyAttr.length; i++) {
			keyModRule.addWord(keyAttr[i], keyAttrToken);
		}

		
		setRules(new IRule[] {
		// Add rule for processing instructions
				keyModRule,
				keyModEltRule,
				keyStatRule,
				keyAttrRule,
				new SingleLineRule("\"", "\"", stringToken),
				new SingleLineRule("'", " ", stringToken),
				new MultiLineRule("***(", ") ", commentToken),
				new EndOfLineRule("***", commentToken),
				new EndOfLineRule("---", commentToken),			
				new WhitespaceRule(new WhitespaceDetector())

		});
	}
}
