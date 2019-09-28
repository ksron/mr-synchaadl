/**
 * Define a grammar called Hello
 */
grammar Flows;
cd : equation* EOF;

equation
   : expression '=' expression ';'
   ;

expression
   :  expression  '^' expression
   |  expression  ('*' | '/')  expression
   |  expression  ('+' | '-') expression
   |  '(' expression ')'
   |  ('-')* atom
   ;

atom
   : constant
   | token
   | variable
   ;
   
token
	: TOKEN
	;

constant
   : CONSTANT_PROPERTY
   ;

variable
   : VARIABLE
   ;


VARIABLE
   : VALID_ID_START VALID_ID_CHAR*
   ;


fragment VALID_ID_START
   : ('a' .. 'z') | ('A' .. 'Z') | '_'
   ;


fragment VALID_ID_CHAR
   : VALID_ID_START | ('0' .. '9')
   ;

CONSTANT_PROPERTY
   : VALID_ID_CHAR* '::' VALID_ID_CHAR*
   ;

TOKEN
	: VALID_ID_CHAR* '(' VARIABLE ')'
	;


WS
   : [ \r\n\t] + -> skip
   ;