grammar Minstrel;

// LEXING

STRING_LITERAL : '"' ~["]* '"';

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

IDENTIFIER : LETTER (LETTER | DIGIT)*;

WHITESPACE : [ \t\r\n] -> skip;

// PARSING

program : expression EOF;

expression
  : STRING_LITERAL  # string
  | IDENTIFIER      # variable
  | expression args # call
  ;

args : '[' (expression (',' expression)*)? ']';