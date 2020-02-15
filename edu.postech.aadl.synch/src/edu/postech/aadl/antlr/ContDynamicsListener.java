// Generated from ContDynamics.g4 by ANTLR 4.4
package edu.postech.aadl.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ContDynamicsParser}.
 */
public interface ContDynamicsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void enterUnary_operator(@NotNull ContDynamicsParser.Unary_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void exitUnary_operator(@NotNull ContDynamicsParser.Unary_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#value_expression}.
	 * @param ctx the parse tree
	 */
	void enterValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#value_expression}.
	 * @param ctx the parse tree
	 */
	void exitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#term_expression}.
	 * @param ctx the parse tree
	 */
	void enterTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#term_expression}.
	 * @param ctx the parse tree
	 */
	void exitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void enterSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#simple_expression}.
	 * @param ctx the parse tree
	 */
	void exitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#value_constant}.
	 * @param ctx the parse tree
	 */
	void enterValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#value_constant}.
	 * @param ctx the parse tree
	 */
	void exitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContFunc}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 */
	void enterContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContFunc}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 */
	void exitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ODE}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 */
	void enterODE(@NotNull ContDynamicsParser.ODEContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ODE}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 */
	void exitODE(@NotNull ContDynamicsParser.ODEContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#value_variable}.
	 * @param ctx the parse tree
	 */
	void enterValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#value_variable}.
	 * @param ctx the parse tree
	 */
	void exitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#value_operator}.
	 * @param ctx the parse tree
	 */
	void enterValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#value_operator}.
	 * @param ctx the parse tree
	 */
	void exitValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#term_operator}.
	 * @param ctx the parse tree
	 */
	void enterTerm_operator(@NotNull ContDynamicsParser.Term_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#term_operator}.
	 * @param ctx the parse tree
	 */
	void exitTerm_operator(@NotNull ContDynamicsParser.Term_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#factor_operator}.
	 * @param ctx the parse tree
	 */
	void enterFactor_operator(@NotNull ContDynamicsParser.Factor_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#factor_operator}.
	 * @param ctx the parse tree
	 */
	void exitFactor_operator(@NotNull ContDynamicsParser.Factor_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#factor_expression}.
	 * @param ctx the parse tree
	 */
	void enterFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#factor_expression}.
	 * @param ctx the parse tree
	 */
	void exitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ContDynamicsParser#continuousdynamics}.
	 * @param ctx the parse tree
	 */
	void enterContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ContDynamicsParser#continuousdynamics}.
	 * @param ctx the parse tree
	 */
	void exitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx);
}