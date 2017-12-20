grammar Minimalang;

// LEXING

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

TYPE : 'type';
STRING_LITERAL : ["] ~["]* ["];
NUMBER_LITERAL : DIGIT+ ('.' DIGIT+)?;
IDENTIFIER : LETTER (LETTER | DIGIT)*;
ANNOTATION : [@] IDENTIFIER;
WHITESPACE : [ \t\r\n] -> skip;

// PARSING

program : (expression (',' expression)*)? EOF;

expression
  : STRING_LITERAL                                                              # string
  | NUMBER_LITERAL                                                              # number
  | IDENTIFIER                                                                  # variable
  | expression '[' (expression (',' expression)*)? ']'                          # call
  | expression ':' IDENTIFIER                                                   # access
  | '[' (introduction (',' introduction)*)? ']' '=>' expression                 # function
  | '{' (introduction ':' expression (',' introduction ':' expression)*)? '}'   # object
  | introduction 'is' expression                                                # declaration
  | TYPE IDENTIFIER 'is' type                                                   # typedefinition
  | '(' expression (',' expression)* ')'                                        # sequence
  ;

introduction : (ANNOTATION)* IDENTIFIER;

type
  : IDENTIFIER                              # type_alias
  | '[' (type (',' type)*)? ']' '=>' type   # function_type
  ;
