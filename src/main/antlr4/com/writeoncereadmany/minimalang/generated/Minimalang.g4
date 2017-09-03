grammar Minimalang;

// LEXING

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

STRING_LITERAL : ["] ~["]* ["];
NUMBER_LITERAL : DIGIT+ ('.' DIGIT+)?;
IDENTIFIER : LETTER (LETTER | DIGIT)*;
WHITESPACE : [ \t\r\n] -> skip;

// PARSING

program : expression+ EOF;

expression
  : STRING_LITERAL                                                          # string
  | NUMBER_LITERAL                                                          # number
  | IDENTIFIER                                                              # variable
  | expression '[' (expression (',' expression)*)? ']'                      # call
  | expression ':' IDENTIFIER                                               # access
  | '[' (IDENTIFIER (',' IDENTIFIER)*)? ']' '=>' expression                 # function
  | '{' (IDENTIFIER ':' expression (',' IDENTIFIER ':' expression)*)? '}'   # object
  | IDENTIFIER 'is' expression                                              # declaration
  | '(' expression (',' expression)* ')'                                    # sequence
  ;