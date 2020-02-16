// Generated from ContDynamics.g4 by ANTLR 4.4
package edu.postech.aadl.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ContDynamicsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VALID_ID_START=2, CONSTANT_PROPERTY=3, VALID_ID_CHAR=4, EQUAL=5, 
		NOTEQUAL=6, LESSTHAN=7, LESSOREQUAL=8, GREATERTHAN=9, GREATEROREQUAL=10, 
		PLUS=11, MINUS=12, MUL=13, POWER=14, DIVIDE=15, LPAREN=16, RPAREN=17, 
		DERIV=18, SEMICOLON=19, AND=20, OR=21, XOR=22, WS=23;
	public static final String[] tokenNames = {
		"<INVALID>", "'(0)'", "VALID_ID_START", "CONSTANT_PROPERTY", "VALID_ID_CHAR", 
		"'='", "'!='", "'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'**'", 
		"'/'", "'('", "')'", "'d/dt'", "';'", "'and'", "'or'", "'xor'", "WS"
	};
	public static final int
		RULE_continuousdynamics = 0, RULE_assignment = 1, RULE_target = 2, RULE_simple_expression = 3, 
		RULE_term_expression = 4, RULE_factor_expression = 5, RULE_value_expression = 6, 
		RULE_value_variable = 7, RULE_value_constant = 8, RULE_unary_operator = 9, 
		RULE_term_operator = 10, RULE_factor_operator = 11, RULE_value_operator = 12;
	public static final String[] ruleNames = {
		"continuousdynamics", "assignment", "target", "simple_expression", "term_expression", 
		"factor_expression", "value_expression", "value_variable", "value_constant", 
		"unary_operator", "term_operator", "factor_operator", "value_operator"
	};

	@Override
	public String getGrammarFileName() { return "ContDynamics.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ContDynamicsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ContinuousdynamicsContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public TerminalNode EOF() { return getToken(ContDynamicsParser.EOF, 0); }
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public ContinuousdynamicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continuousdynamics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterContinuousdynamics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitContinuousdynamics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitContinuousdynamics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinuousdynamicsContext continuousdynamics() throws RecognitionException {
		ContinuousdynamicsContext _localctx = new ContinuousdynamicsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_continuousdynamics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << VALID_ID_START) | (1L << LPAREN) | (1L << DERIV))) != 0)) {
				{
				{
				setState(26); assignment();
				}
				}
				setState(31);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(32); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public TerminalNode EQUAL() { return getToken(ContDynamicsParser.EQUAL, 0); }
		public TerminalNode SEMICOLON() { return getToken(ContDynamicsParser.SEMICOLON, 0); }
		public Simple_expressionContext simple_expression() {
			return getRuleContext(Simple_expressionContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34); target();
			setState(35); match(EQUAL);
			setState(36); simple_expression();
			setState(38);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(37); match(SEMICOLON);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TargetContext extends ParserRuleContext {
		public TargetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_target; }
	 
		public TargetContext() { }
		public void copyFrom(TargetContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ODEContext extends TargetContext {
		public TerminalNode DERIV() { return getToken(ContDynamicsParser.DERIV, 0); }
		public TerminalNode LPAREN() { return getToken(ContDynamicsParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ContDynamicsParser.RPAREN, 0); }
		public Value_variableContext value_variable() {
			return getRuleContext(Value_variableContext.class,0);
		}
		public ODEContext(TargetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterODE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitODE(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitODE(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContFuncContext extends TargetContext {
		public TerminalNode LPAREN() { return getToken(ContDynamicsParser.LPAREN, 0); }
		public Value_variableContext value_variable(int i) {
			return getRuleContext(Value_variableContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(ContDynamicsParser.RPAREN, 0); }
		public List<Value_variableContext> value_variable() {
			return getRuleContexts(Value_variableContext.class);
		}
		public ContFuncContext(TargetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterContFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitContFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitContFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TargetContext target() throws RecognitionException {
		TargetContext _localctx = new TargetContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_target);
		try {
			setState(50);
			switch (_input.LA(1)) {
			case DERIV:
				_localctx = new ODEContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(40); match(DERIV);
				setState(41); match(LPAREN);
				setState(42); value_variable();
				setState(43); match(RPAREN);
				}
				break;
			case T__0:
			case VALID_ID_START:
			case LPAREN:
				_localctx = new ContFuncContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(45); value_variable();
				setState(46); match(LPAREN);
				setState(47); value_variable();
				setState(48); match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Simple_expressionContext extends ParserRuleContext {
		public List<Term_expressionContext> term_expression() {
			return getRuleContexts(Term_expressionContext.class);
		}
		public Term_expressionContext term_expression(int i) {
			return getRuleContext(Term_expressionContext.class,i);
		}
		public Term_operatorContext term_operator(int i) {
			return getRuleContext(Term_operatorContext.class,i);
		}
		public List<Term_operatorContext> term_operator() {
			return getRuleContexts(Term_operatorContext.class);
		}
		public Simple_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterSimple_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitSimple_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitSimple_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Simple_expressionContext simple_expression() throws RecognitionException {
		Simple_expressionContext _localctx = new Simple_expressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_simple_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52); term_expression();
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(53); term_operator();
				setState(54); term_expression();
				}
				}
				setState(60);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Term_expressionContext extends ParserRuleContext {
		public List<Factor_expressionContext> factor_expression() {
			return getRuleContexts(Factor_expressionContext.class);
		}
		public Factor_expressionContext factor_expression(int i) {
			return getRuleContext(Factor_expressionContext.class,i);
		}
		public Factor_operatorContext factor_operator(int i) {
			return getRuleContext(Factor_operatorContext.class,i);
		}
		public List<Factor_operatorContext> factor_operator() {
			return getRuleContexts(Factor_operatorContext.class);
		}
		public Term_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterTerm_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitTerm_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitTerm_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Term_expressionContext term_expression() throws RecognitionException {
		Term_expressionContext _localctx = new Term_expressionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61); factor_expression();
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MUL || _la==DIVIDE) {
				{
				{
				setState(62); factor_operator();
				setState(63); factor_expression();
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Factor_expressionContext extends ParserRuleContext {
		public Value_operatorContext value_operator() {
			return getRuleContext(Value_operatorContext.class,0);
		}
		public List<Value_expressionContext> value_expression() {
			return getRuleContexts(Value_expressionContext.class);
		}
		public Value_expressionContext value_expression(int i) {
			return getRuleContext(Value_expressionContext.class,i);
		}
		public Factor_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterFactor_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitFactor_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitFactor_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Factor_expressionContext factor_expression() throws RecognitionException {
		Factor_expressionContext _localctx = new Factor_expressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_factor_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); value_expression();
			setState(74);
			_la = _input.LA(1);
			if (_la==POWER) {
				{
				setState(71); value_operator();
				setState(72); value_expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_expressionContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(ContDynamicsParser.LPAREN, 0); }
		public Value_constantContext value_constant() {
			return getRuleContext(Value_constantContext.class,0);
		}
		public Unary_operatorContext unary_operator() {
			return getRuleContext(Unary_operatorContext.class,0);
		}
		public Simple_expressionContext simple_expression() {
			return getRuleContext(Simple_expressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(ContDynamicsParser.RPAREN, 0); }
		public Value_variableContext value_variable() {
			return getRuleContext(Value_variableContext.class,0);
		}
		public Value_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterValue_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitValue_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitValue_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_expressionContext value_expression() throws RecognitionException {
		Value_expressionContext _localctx = new Value_expressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_value_expression);
		int _la;
		try {
			setState(91);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(77);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(76); unary_operator();
					}
				}

				setState(79); value_constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(81);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(80); unary_operator();
					}
					break;
				}
				setState(83); value_variable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(85);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(84); unary_operator();
					}
				}

				setState(87); match(LPAREN);
				setState(88); simple_expression();
				setState(89); match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_variableContext extends ParserRuleContext {
		public Token var;
		public Token zero;
		public List<TerminalNode> VALID_ID_START() { return getTokens(ContDynamicsParser.VALID_ID_START); }
		public TerminalNode VALID_ID_START(int i) {
			return getToken(ContDynamicsParser.VALID_ID_START, i);
		}
		public Value_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterValue_variable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitValue_variable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitValue_variable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_variableContext value_variable() throws RecognitionException {
		Value_variableContext _localctx = new Value_variableContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_value_variable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(93); ((Value_variableContext)_localctx).var = match(VALID_ID_START);
					}
					} 
				}
				setState(98);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			setState(100);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(99); ((Value_variableContext)_localctx).zero = match(T__0);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_constantContext extends ParserRuleContext {
		public TerminalNode CONSTANT_PROPERTY() { return getToken(ContDynamicsParser.CONSTANT_PROPERTY, 0); }
		public Value_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterValue_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitValue_constant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitValue_constant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_constantContext value_constant() throws RecognitionException {
		Value_constantContext _localctx = new Value_constantContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_value_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102); match(CONSTANT_PROPERTY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_operatorContext extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(ContDynamicsParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(ContDynamicsParser.MINUS, 0); }
		public Unary_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterUnary_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitUnary_operator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitUnary_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unary_operatorContext unary_operator() throws RecognitionException {
		Unary_operatorContext _localctx = new Unary_operatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_unary_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			_la = _input.LA(1);
			if ( !(_la==PLUS || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Term_operatorContext extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(ContDynamicsParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(ContDynamicsParser.MINUS, 0); }
		public Term_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterTerm_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitTerm_operator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitTerm_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Term_operatorContext term_operator() throws RecognitionException {
		Term_operatorContext _localctx = new Term_operatorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_term_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			_la = _input.LA(1);
			if ( !(_la==PLUS || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Factor_operatorContext extends ParserRuleContext {
		public TerminalNode DIVIDE() { return getToken(ContDynamicsParser.DIVIDE, 0); }
		public TerminalNode MUL() { return getToken(ContDynamicsParser.MUL, 0); }
		public Factor_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterFactor_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitFactor_operator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitFactor_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Factor_operatorContext factor_operator() throws RecognitionException {
		Factor_operatorContext _localctx = new Factor_operatorContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_factor_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			_la = _input.LA(1);
			if ( !(_la==MUL || _la==DIVIDE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_operatorContext extends ParserRuleContext {
		public TerminalNode POWER() { return getToken(ContDynamicsParser.POWER, 0); }
		public Value_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterValue_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitValue_operator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitValue_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_operatorContext value_operator() throws RecognitionException {
		Value_operatorContext _localctx = new Value_operatorContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_value_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110); match(POWER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\31s\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\2\3\2\3\3\3\3"+
		"\3\3\3\3\5\3)\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\65\n\4\3"+
		"\5\3\5\3\5\3\5\7\5;\n\5\f\5\16\5>\13\5\3\6\3\6\3\6\3\6\7\6D\n\6\f\6\16"+
		"\6G\13\6\3\7\3\7\3\7\3\7\5\7M\n\7\3\b\5\bP\n\b\3\b\3\b\5\bT\n\b\3\b\3"+
		"\b\5\bX\n\b\3\b\3\b\3\b\3\b\5\b^\n\b\3\t\7\ta\n\t\f\t\16\td\13\t\3\t\5"+
		"\tg\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\2\2\17\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\2\4\3\2\r\16\4\2\17\17\21\21r\2\37\3\2\2\2"+
		"\4$\3\2\2\2\6\64\3\2\2\2\b\66\3\2\2\2\n?\3\2\2\2\fH\3\2\2\2\16]\3\2\2"+
		"\2\20b\3\2\2\2\22h\3\2\2\2\24j\3\2\2\2\26l\3\2\2\2\30n\3\2\2\2\32p\3\2"+
		"\2\2\34\36\5\4\3\2\35\34\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2"+
		" \"\3\2\2\2!\37\3\2\2\2\"#\7\2\2\3#\3\3\2\2\2$%\5\6\4\2%&\7\7\2\2&(\5"+
		"\b\5\2\')\7\25\2\2(\'\3\2\2\2()\3\2\2\2)\5\3\2\2\2*+\7\24\2\2+,\7\22\2"+
		"\2,-\5\20\t\2-.\7\23\2\2.\65\3\2\2\2/\60\5\20\t\2\60\61\7\22\2\2\61\62"+
		"\5\20\t\2\62\63\7\23\2\2\63\65\3\2\2\2\64*\3\2\2\2\64/\3\2\2\2\65\7\3"+
		"\2\2\2\66<\5\n\6\2\678\5\26\f\289\5\n\6\29;\3\2\2\2:\67\3\2\2\2;>\3\2"+
		"\2\2<:\3\2\2\2<=\3\2\2\2=\t\3\2\2\2><\3\2\2\2?E\5\f\7\2@A\5\30\r\2AB\5"+
		"\f\7\2BD\3\2\2\2C@\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2F\13\3\2\2\2G"+
		"E\3\2\2\2HL\5\16\b\2IJ\5\32\16\2JK\5\16\b\2KM\3\2\2\2LI\3\2\2\2LM\3\2"+
		"\2\2M\r\3\2\2\2NP\5\24\13\2ON\3\2\2\2OP\3\2\2\2PQ\3\2\2\2Q^\5\22\n\2R"+
		"T\5\24\13\2SR\3\2\2\2ST\3\2\2\2TU\3\2\2\2U^\5\20\t\2VX\5\24\13\2WV\3\2"+
		"\2\2WX\3\2\2\2XY\3\2\2\2YZ\7\22\2\2Z[\5\b\5\2[\\\7\23\2\2\\^\3\2\2\2]"+
		"O\3\2\2\2]S\3\2\2\2]W\3\2\2\2^\17\3\2\2\2_a\7\4\2\2`_\3\2\2\2ad\3\2\2"+
		"\2b`\3\2\2\2bc\3\2\2\2cf\3\2\2\2db\3\2\2\2eg\7\3\2\2fe\3\2\2\2fg\3\2\2"+
		"\2g\21\3\2\2\2hi\7\5\2\2i\23\3\2\2\2jk\t\2\2\2k\25\3\2\2\2lm\t\2\2\2m"+
		"\27\3\2\2\2no\t\3\2\2o\31\3\2\2\2pq\7\20\2\2q\33\3\2\2\2\16\37(\64<EL"+
		"OSW]bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}