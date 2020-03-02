// Generated from ContDynamics.g4 by ANTLR 4.4
package edu.postech.aadl.synch.maude.contspec.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ContDynamicsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ContDynamicsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#unary_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_operator(@NotNull ContDynamicsParser.Unary_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#value_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#term_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#simple_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#value_constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContFunc}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ODE}
	 * labeled alternative in {@link ContDynamicsParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitODE(@NotNull ContDynamicsParser.ODEContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#value_variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#value_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#term_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm_operator(@NotNull ContDynamicsParser.Term_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#factor_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor_operator(@NotNull ContDynamicsParser.Factor_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#factor_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ContDynamicsParser#continuousdynamics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx);
}