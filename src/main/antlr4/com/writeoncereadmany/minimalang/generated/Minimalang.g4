grammar Minimalang;

// LEXING

STRING_LITERAL : ['] ~[']* ['];

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

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
  | '[' (IDENTIFIER (',' IDENTIFIER)*)? ']' '=>' expression                 # function
  | '{' (IDENTIFIER ':' expression (',' IDENTIFIER ':' expression)*)? '}'   # object
  | expression ':' IDENTIFIER                                               # access
  | IDENTIFIER 'is' expression                                              # declaration
  | '(' expression (',' expression)* ')'                                    # sequence
  ;