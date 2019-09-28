grammar Flows;
formula : equation* EOF;

equation
   : expression '=' expression ';'
   ;

expression
   :  left=expression  op='^' right=expression
   |  left=expression  op=('*' | '/') right=expression
   |  left=expression  op=('+' | '-') right=expression
   |  '(' param=expression ')'
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

variable
   : VARIABLE
   ;

constant
    : CONSTANT_PROPERTY
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
   | VALID_ID_CHAR+
   ;

TOKEN
	: VALID_ID_CHAR* '(' VALID_ID_CHAR ')'
	;


WS
   : [ \r\n\t] + -> skip
   ;
   
