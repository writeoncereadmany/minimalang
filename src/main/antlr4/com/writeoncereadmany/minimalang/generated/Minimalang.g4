grammar Minimalang;

// LEXING

STRING_LITERAL : ['] ~[']* ['];

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

IDENTIFIER : LETTER (LETTER | DIGIT)*;

WHITESPACE : [ \t\r\n] -> skip;

// PARSING

program : expression EOF;

expression
  : STRING_LITERAL                                                          # string
  | IDENTIFIER 'is' expression                                              # declaration
  | IDENTIFIER                                                              # variable
  | '{' (IDENTIFIER ':' expression (',' IDENTIFIER ':' expression)*)? '}'   # object
  | expression '.' IDENTIFIER                                               # access
  | expression '[' (expression (',' expression)*)? ']'                      # call
  | expression ';' expression                                               # sequence
  ;