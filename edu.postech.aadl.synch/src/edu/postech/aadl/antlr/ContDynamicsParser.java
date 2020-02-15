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
		RULE_term = 4, RULE_factor = 5, RULE_value = 6, RULE_value_variable = 7, 
		RULE_value_constant = 8, RULE_unary_operator = 9, RULE_term_operator = 10, 
		RULE_factor_operator = 11, RULE_value_operator = 12;
	public static final String[] ruleNames = {
		"continuousdynamics", "assignment", "target", "simple_expression", "term", 
		"factor", "value", "value_variable", "value_constant", "unary_operator", 
		"term_operator", "factor_operator", "value_operator"
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
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public Unary_operatorContext unary_operator() {
			return getRuleContext(Unary_operatorContext.class,0);
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
			setState(53);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(52); unary_operator();
				}
				break;
			}
			setState(55); term();
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(56); term_operator();
				setState(57); term();
				}
				}
				setState(63);
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

	public static class TermContext extends ParserRuleContext {
		public List<FactorContext> factor() {
			return getRuleContexts(FactorContext.class);
		}
		public FactorContext factor(int i) {
			return getRuleContext(FactorContext.class,i);
		}
		public Factor_operatorContext factor_operator(int i) {
			return getRuleContext(Factor_operatorContext.class,i);
		}
		public List<Factor_operatorContext> factor_operator() {
			return getRuleContexts(Factor_operatorContext.class);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64); factor();
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MUL || _la==DIVIDE) {
				{
				{
				setState(65); factor_operator();
				setState(66); factor();
				}
				}
				setState(72);
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

	public static class FactorContext extends ParserRuleContext {
		public Value_operatorContext value_operator() {
			return getRuleContext(Value_operatorContext.class,0);
		}
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitFactor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitFactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_factor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73); value();
			setState(77);
			_la = _input.LA(1);
			if (_la==POWER) {
				{
				setState(74); value_operator();
				setState(75); value();
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

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(ContDynamicsParser.LPAREN, 0); }
		public Value_constantContext value_constant() {
			return getRuleContext(Value_constantContext.class,0);
		}
		public Simple_expressionContext simple_expression() {
			return getRuleContext(Simple_expressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(ContDynamicsParser.RPAREN, 0); }
		public Value_variableContext value_variable() {
			return getRuleContext(Value_variableContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ContDynamicsListener ) ((ContDynamicsListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ContDynamicsVisitor ) return ((ContDynamicsVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_value);
		try {
			setState(85);
			switch (_input.LA(1)) {
			case CONSTANT_PROPERTY:
				enterOuterAlt(_localctx, 1);
				{
				setState(79); value_constant();
				}
				break;
			case EOF:
			case T__0:
			case VALID_ID_START:
			case PLUS:
			case MINUS:
			case MUL:
			case POWER:
			case DIVIDE:
			case RPAREN:
			case DERIV:
			case SEMICOLON:
				enterOuterAlt(_localctx, 2);
				{
				setState(80); value_variable();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(81); match(LPAREN);
				setState(82); simple_expression();
				setState(83); match(RPAREN);
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

	public static class Value_variableContext extends ParserRuleContext {
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
			setState(90);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(87); match(VALID_ID_START);
					}
					} 
				}
				setState(92);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			setState(94);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(93); ((Value_variableContext)_localctx).zero = match(T__0);
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
			setState(96); match(CONSTANT_PROPERTY);
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
			setState(98);
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
			setState(100);
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
			setState(102);
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
			setState(104); match(POWER);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\31m\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\2\3\2\3\3\3\3"+
		"\3\3\3\3\5\3)\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\65\n\4\3"+
		"\5\5\58\n\5\3\5\3\5\3\5\3\5\7\5>\n\5\f\5\16\5A\13\5\3\6\3\6\3\6\3\6\7"+
		"\6G\n\6\f\6\16\6J\13\6\3\7\3\7\3\7\3\7\5\7P\n\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\5\bX\n\b\3\t\7\t[\n\t\f\t\16\t^\13\t\3\t\5\ta\n\t\3\n\3\n\3\13\3\13"+
		"\3\f\3\f\3\r\3\r\3\16\3\16\3\16\2\2\17\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\2\4\3\2\r\16\4\2\17\17\21\21j\2\37\3\2\2\2\4$\3\2\2\2\6\64\3\2\2\2\b"+
		"\67\3\2\2\2\nB\3\2\2\2\fK\3\2\2\2\16W\3\2\2\2\20\\\3\2\2\2\22b\3\2\2\2"+
		"\24d\3\2\2\2\26f\3\2\2\2\30h\3\2\2\2\32j\3\2\2\2\34\36\5\4\3\2\35\34\3"+
		"\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \"\3\2\2\2!\37\3\2\2\2\""+
		"#\7\2\2\3#\3\3\2\2\2$%\5\6\4\2%&\7\7\2\2&(\5\b\5\2\')\7\25\2\2(\'\3\2"+
		"\2\2()\3\2\2\2)\5\3\2\2\2*+\7\24\2\2+,\7\22\2\2,-\5\20\t\2-.\7\23\2\2"+
		".\65\3\2\2\2/\60\5\20\t\2\60\61\7\22\2\2\61\62\5\20\t\2\62\63\7\23\2\2"+
		"\63\65\3\2\2\2\64*\3\2\2\2\64/\3\2\2\2\65\7\3\2\2\2\668\5\24\13\2\67\66"+
		"\3\2\2\2\678\3\2\2\289\3\2\2\29?\5\n\6\2:;\5\26\f\2;<\5\n\6\2<>\3\2\2"+
		"\2=:\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@\t\3\2\2\2A?\3\2\2\2BH\5\f"+
		"\7\2CD\5\30\r\2DE\5\f\7\2EG\3\2\2\2FC\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3"+
		"\2\2\2I\13\3\2\2\2JH\3\2\2\2KO\5\16\b\2LM\5\32\16\2MN\5\16\b\2NP\3\2\2"+
		"\2OL\3\2\2\2OP\3\2\2\2P\r\3\2\2\2QX\5\22\n\2RX\5\20\t\2ST\7\22\2\2TU\5"+
		"\b\5\2UV\7\23\2\2VX\3\2\2\2WQ\3\2\2\2WR\3\2\2\2WS\3\2\2\2X\17\3\2\2\2"+
		"Y[\7\4\2\2ZY\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]`\3\2\2\2^\\\3\2"+
		"\2\2_a\7\3\2\2`_\3\2\2\2`a\3\2\2\2a\21\3\2\2\2bc\7\5\2\2c\23\3\2\2\2d"+
		"e\t\2\2\2e\25\3\2\2\2fg\t\2\2\2g\27\3\2\2\2hi\t\3\2\2i\31\3\2\2\2jk\7"+
		"\20\2\2k\33\3\2\2\2\f\37(\64\67?HOW\\`";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}