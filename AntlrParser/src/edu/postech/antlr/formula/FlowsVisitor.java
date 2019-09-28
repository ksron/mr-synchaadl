package edu.postech.antlr.formula;

import edu.postech.antlr.parser.*;
import java.util.List;

public class FlowsVisitor extends FlowsBaseVisitor<String> {

    private FlowsData flowsData;

    public FlowsVisitor setFlowsData(FlowsData data){
        flowsData = data;
        return this;
    }

    @Override
    public String visitFormula(FlowsParser.FormulaContext ctx) {
        String ret = "";
        for(FlowsParser.EquationContext equation : ctx.equation()){
            ret += visitEquation(equation);
        }
        return ret;
     }

     @Override
     public String visitEquation(FlowsParser.EquationContext ctx) {
         List<FlowsParser.ExpressionContext> expressions = ctx.expression();
         return visitAtom(expressions.get(0).atom()) + "=" + visitExpression(expressions.get(1))+ ";";
     }

     @Override
     public String visitExpression(FlowsParser.ExpressionContext ctx) {
         //System.out.println("visitExpression : " + ctx.getText());

         if(ctx.left != null && ctx.right != null){
             switch(ctx.op.getText().charAt(0)){
                 case '+':  return "(" + visitExpression(ctx.left) + "+" + visitExpression(ctx.right)+")";
                 case '-':  return "(" + visitExpression(ctx.left) + "-" + visitExpression(ctx.right)+")";
                 case '*':  return "(" + visitExpression(ctx.left) + "*" + visitExpression(ctx.right)+")";
                 case '/':  return "(" + visitExpression(ctx.left) + "/" + visitExpression(ctx.right)+")";
                 default: return "(" + visitExpression(ctx.left) + "+" + visitExpression(ctx.right)+")";
             }
         } else if (ctx.param != null){
            return "(" + visitExpression(ctx.param) + ")";
         } else {
             if(ctx.getText().charAt(0)=='-'){
                 return "minus("+visitAtom(ctx.atom()) + ")";
             }
             return visitAtom(ctx.atom());
         }
     }

     @Override
     public String visitAtom(FlowsParser.AtomContext ctx) {
         System.out.println("visitAtom : "+ctx.getText());
         if(ctx.getText().charAt(0)=='-'){
             return "minus("+visitChildren(ctx)+")";
         }
         return visitChildren(ctx);
     }

     @Override
     public String visitToken(FlowsParser.TokenContext ctx) {
          return assignTokenPrefix(ctx.getText());
      }

     @Override
     public String visitConstant(FlowsParser.ConstantContext ctx) {
         return "[["+assignConstant(ctx.getText())+"]]";
      }

     @Override
     public String visitVariable(FlowsParser.VariableContext ctx) {
          return "v["+ctx.getText()+"]";
      }

      private String assignTokenPrefix(String token){
          String ret = token;
          if(token.contains("(0)")){
              ret = "c["+token.substring(0, token.indexOf('('))+"]";
          }
          return ret;
      }

      private String assignConstant(String constant){
          String ret = null;
          if(constant.contains("::")){
              ret = flowsData.getConstant(constant);
          } else {
              ret = constant;
          }
          return ret != null ? ret : "null";
      }

}
