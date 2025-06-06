// SimpleLang.g4

grammar SimpleLang;

// --- Parser Rules ---
program: statement* EOF;

statement
    : assignment SEMI           # AssignmentStmt
    | ifStatement               # IfStmt
    | whileStatement            # WhileStmt
    | printStatement SEMI       # PrintStmt
    | LBRACE statement* RBRACE  # BlockStmt 
    ;

assignment: ID EQ expression;

ifStatement:
    IF LPAREN expression RPAREN statement (ELSE statement)?;

whileStatement:
    WHILE LPAREN expression RPAREN statement;

printStatement:
    PRINT expression;

expression
    : expression (GT | LT | GTE | LTE | EQEQ | NEQ) expression  # CompareExpr
    | expression (PLUS | MINUS) expression                      # AddSubExpr
    | expression (MUL | DIV) expression                         # MulDivExpr
    | MINUS expression                                          # UnaryMinusExpr
    | atom                                                      # AtomExpr
    ;

atom
    : LPAREN expression RPAREN  # ParenExpr
    | NUMBER                    # NumberAtom
    | ID                        # IdAtom
    | STRING                    # StringAtom
    | FLOAT                     # FloatAtom
    ;

// --- Lexer Rules ---
IF: 'if';
ELSE: 'else';
WHILE: 'while';
PRINT: 'print';
ID: [a-zA-Z_][a-zA-Z_0-9]*;
NUMBER: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]* | '.' [0-9]+;
STRING: '"' (~["\\] | '\\' .)*? '"' | '\'' (~['\\] | '\\' .)*? '\'';
EQ: '=';
EQEQ: '==';
NEQ: '!=';
GT: '>';
LT: '<';
GTE: '>=';
LTE: '<=';
PLUS: '+';
MINUS: '-';
MUL: '*';
DIV: '/';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
SEMI: ';';
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;