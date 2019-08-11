/*
 * generated by Xtext 2.17.0
 */
package edu.postech.xtext.maude.parser.antlr;

import com.google.inject.Inject;
import edu.postech.xtext.maude.parser.antlr.internal.InternalMaudeParser;
import edu.postech.xtext.maude.services.MaudeGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

public class MaudeParser extends AbstractAntlrParser {

	@Inject
	private MaudeGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalMaudeParser createParser(XtextTokenStream stream) {
		return new InternalMaudeParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "Model";
	}

	public MaudeGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(MaudeGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
