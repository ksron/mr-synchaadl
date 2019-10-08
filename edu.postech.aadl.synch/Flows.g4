grammar Flows;

// x(t) = (-dotx * t) + x(0);

continuousdynamics 
	: (assignment)* EOF
	;
	
assignment
	: target EQUAL value_expression (SEMICOLON)?
	;

target
	: DERIV LPAREN value_variable RPAREN 
	| value_variable LPAREN value_variable RPAREN
	;

value_expression
	: relation (logical_operator relation)*
	;

relation
	: simple_expression (relational_operator simple_expression)?
	;
	
simple_expression
	: (unary_adding_operator)? term (binary_adding_operator term)*
	;

term
	: factor (multiplying_operator factor)*
	;
	
factor
	: value (binary_numeric_operator value)?
	;
	
value
	: value_constant
	| value_variable
	| LPAREN value_expression RPAREN
	;


value_variable
   : VALID_ID_START*
   | VALID_ID_START* '(0)'
   ;

value_constant
    : CONSTANT_PROPERTY
    ;


VALID_ID_START
   : ('a' .. 'z') | ('A' .. 'Z') | '_'
   ;


CONSTANT_PROPERTY
   : VALID_ID_CHAR* '::' VALID_ID_CHAR*
   | ('0' .. '9')+
   | ('0' .. '9')+ ('.' ('0' .. '9')+)?
   ;
   
VALID_ID_CHAR
   : VALID_ID_START | ('0' .. '9') 
   ;
   
logical_operator
	: AND | OR | XOR
	;
   
relational_operator
	: EQUAL | NOTEQUAL | LESSTHAN | LESSOREQUAL | GREATERTHAN | GREATEROREQUAL
	;
	
unary_adding_operator
	: PLUS | MINUS
	;
	
binary_adding_operator
	: PLUS | MINUS
	;
	
multiplying_operator
	: STAR | DIVIDE | MOD | REM
	;
	
binary_numeric_operator
	: STARSTAR
	;
   
   
// Keywords
EQUAL          	: '=';
NOTEQUAL       	: '!=';
LESSTHAN       	: '<';
LESSOREQUAL    	: '<=';
GREATERTHAN    	: '>';
GREATEROREQUAL 	: '>=';
PLUS 			: '+';
MINUS			: '-';
STAR			: '*';
STARSTAR		: '**';
DIVIDE 			: '/';
MOD				: 'mod';
REM				: 'rem';
LPAREN			: '(';
RPAREN 			: ')';
DERIV			: 'd/dt';	
SEMICOLON		: ';';
AND				: 'and';
OR				: 'or';
XOR				: 'xor';


WS
   : [ \r\n\t] + -> skip
   ;