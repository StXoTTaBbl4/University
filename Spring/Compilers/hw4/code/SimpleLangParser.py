# Generated from SimpleLang.g4 by ANTLR 4.13.2
# encoding: utf-8
from antlr4 import *
from io import StringIO
import sys
if sys.version_info[1] > 5:
	from typing import TextIO
else:
	from typing.io import TextIO

def serializedATN():
    return [
        4,1,26,95,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,
        6,2,7,7,7,1,0,5,0,18,8,0,10,0,12,0,21,9,0,1,0,1,0,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,1,1,1,1,1,5,1,35,8,1,10,1,12,1,38,9,1,1,1,3,1,41,
        8,1,1,2,1,2,1,2,1,2,1,3,1,3,1,3,1,3,1,3,1,3,1,3,3,3,54,8,3,1,4,1,
        4,1,4,1,4,1,4,1,4,1,5,1,5,1,5,1,6,1,6,1,6,1,6,3,6,69,8,6,1,6,1,6,
        1,6,1,6,1,6,1,6,1,6,1,6,1,6,5,6,80,8,6,10,6,12,6,83,9,6,1,7,1,7,
        1,7,1,7,1,7,1,7,1,7,1,7,3,7,93,8,7,1,7,0,1,12,8,0,2,4,6,8,10,12,
        14,0,3,1,0,10,15,1,0,16,17,1,0,18,19,101,0,19,1,0,0,0,2,40,1,0,0,
        0,4,42,1,0,0,0,6,46,1,0,0,0,8,55,1,0,0,0,10,61,1,0,0,0,12,68,1,0,
        0,0,14,92,1,0,0,0,16,18,3,2,1,0,17,16,1,0,0,0,18,21,1,0,0,0,19,17,
        1,0,0,0,19,20,1,0,0,0,20,22,1,0,0,0,21,19,1,0,0,0,22,23,5,0,0,1,
        23,1,1,0,0,0,24,25,3,4,2,0,25,26,5,24,0,0,26,41,1,0,0,0,27,41,3,
        6,3,0,28,41,3,8,4,0,29,30,3,10,5,0,30,31,5,24,0,0,31,41,1,0,0,0,
        32,36,5,22,0,0,33,35,3,2,1,0,34,33,1,0,0,0,35,38,1,0,0,0,36,34,1,
        0,0,0,36,37,1,0,0,0,37,39,1,0,0,0,38,36,1,0,0,0,39,41,5,23,0,0,40,
        24,1,0,0,0,40,27,1,0,0,0,40,28,1,0,0,0,40,29,1,0,0,0,40,32,1,0,0,
        0,41,3,1,0,0,0,42,43,5,5,0,0,43,44,5,9,0,0,44,45,3,12,6,0,45,5,1,
        0,0,0,46,47,5,1,0,0,47,48,5,20,0,0,48,49,3,12,6,0,49,50,5,21,0,0,
        50,53,3,2,1,0,51,52,5,2,0,0,52,54,3,2,1,0,53,51,1,0,0,0,53,54,1,
        0,0,0,54,7,1,0,0,0,55,56,5,3,0,0,56,57,5,20,0,0,57,58,3,12,6,0,58,
        59,5,21,0,0,59,60,3,2,1,0,60,9,1,0,0,0,61,62,5,4,0,0,62,63,3,12,
        6,0,63,11,1,0,0,0,64,65,6,6,-1,0,65,66,5,17,0,0,66,69,3,12,6,2,67,
        69,3,14,7,0,68,64,1,0,0,0,68,67,1,0,0,0,69,81,1,0,0,0,70,71,10,5,
        0,0,71,72,7,0,0,0,72,80,3,12,6,6,73,74,10,4,0,0,74,75,7,1,0,0,75,
        80,3,12,6,5,76,77,10,3,0,0,77,78,7,2,0,0,78,80,3,12,6,4,79,70,1,
        0,0,0,79,73,1,0,0,0,79,76,1,0,0,0,80,83,1,0,0,0,81,79,1,0,0,0,81,
        82,1,0,0,0,82,13,1,0,0,0,83,81,1,0,0,0,84,85,5,20,0,0,85,86,3,12,
        6,0,86,87,5,21,0,0,87,93,1,0,0,0,88,93,5,6,0,0,89,93,5,5,0,0,90,
        93,5,8,0,0,91,93,5,7,0,0,92,84,1,0,0,0,92,88,1,0,0,0,92,89,1,0,0,
        0,92,90,1,0,0,0,92,91,1,0,0,0,93,15,1,0,0,0,8,19,36,40,53,68,79,
        81,92
    ]

class SimpleLangParser ( Parser ):

    grammarFileName = "SimpleLang.g4"

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    sharedContextCache = PredictionContextCache()

    literalNames = [ "<INVALID>", "'if'", "'else'", "'while'", "'print'", 
                     "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                     "'='", "'=='", "'!='", "'>'", "'<'", "'>='", "'<='", 
                     "'+'", "'-'", "'*'", "'/'", "'('", "')'", "'{'", "'}'", 
                     "';'" ]

    symbolicNames = [ "<INVALID>", "IF", "ELSE", "WHILE", "PRINT", "ID", 
                      "NUMBER", "FLOAT", "STRING", "EQ", "EQEQ", "NEQ", 
                      "GT", "LT", "GTE", "LTE", "PLUS", "MINUS", "MUL", 
                      "DIV", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "SEMI", 
                      "WS", "COMMENT" ]

    RULE_program = 0
    RULE_statement = 1
    RULE_assignment = 2
    RULE_ifStatement = 3
    RULE_whileStatement = 4
    RULE_printStatement = 5
    RULE_expression = 6
    RULE_atom = 7

    ruleNames =  [ "program", "statement", "assignment", "ifStatement", 
                   "whileStatement", "printStatement", "expression", "atom" ]

    EOF = Token.EOF
    IF=1
    ELSE=2
    WHILE=3
    PRINT=4
    ID=5
    NUMBER=6
    FLOAT=7
    STRING=8
    EQ=9
    EQEQ=10
    NEQ=11
    GT=12
    LT=13
    GTE=14
    LTE=15
    PLUS=16
    MINUS=17
    MUL=18
    DIV=19
    LPAREN=20
    RPAREN=21
    LBRACE=22
    RBRACE=23
    SEMI=24
    WS=25
    COMMENT=26

    def __init__(self, input:TokenStream, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.13.2")
        self._interp = ParserATNSimulator(self, self.atn, self.decisionsToDFA, self.sharedContextCache)
        self._predicates = None




    class ProgramContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def EOF(self):
            return self.getToken(SimpleLangParser.EOF, 0)

        def statement(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.StatementContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.StatementContext,i)


        def getRuleIndex(self):
            return SimpleLangParser.RULE_program

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitProgram" ):
                return visitor.visitProgram(self)
            else:
                return visitor.visitChildren(self)




    def program(self):

        localctx = SimpleLangParser.ProgramContext(self, self._ctx, self.state)
        self.enterRule(localctx, 0, self.RULE_program)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 19
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while (((_la) & ~0x3f) == 0 and ((1 << _la) & 4194362) != 0):
                self.state = 16
                self.statement()
                self.state = 21
                self._errHandler.sync(self)
                _la = self._input.LA(1)

            self.state = 22
            self.match(SimpleLangParser.EOF)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class StatementContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return SimpleLangParser.RULE_statement

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)



    class IfStmtContext(StatementContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.StatementContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def ifStatement(self):
            return self.getTypedRuleContext(SimpleLangParser.IfStatementContext,0)


        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitIfStmt" ):
                return visitor.visitIfStmt(self)
            else:
                return visitor.visitChildren(self)


    class PrintStmtContext(StatementContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.StatementContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def printStatement(self):
            return self.getTypedRuleContext(SimpleLangParser.PrintStatementContext,0)

        def SEMI(self):
            return self.getToken(SimpleLangParser.SEMI, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitPrintStmt" ):
                return visitor.visitPrintStmt(self)
            else:
                return visitor.visitChildren(self)


    class WhileStmtContext(StatementContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.StatementContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def whileStatement(self):
            return self.getTypedRuleContext(SimpleLangParser.WhileStatementContext,0)


        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitWhileStmt" ):
                return visitor.visitWhileStmt(self)
            else:
                return visitor.visitChildren(self)


    class BlockStmtContext(StatementContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.StatementContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def LBRACE(self):
            return self.getToken(SimpleLangParser.LBRACE, 0)
        def RBRACE(self):
            return self.getToken(SimpleLangParser.RBRACE, 0)
        def statement(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.StatementContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.StatementContext,i)


        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitBlockStmt" ):
                return visitor.visitBlockStmt(self)
            else:
                return visitor.visitChildren(self)


    class AssignmentStmtContext(StatementContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.StatementContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def assignment(self):
            return self.getTypedRuleContext(SimpleLangParser.AssignmentContext,0)

        def SEMI(self):
            return self.getToken(SimpleLangParser.SEMI, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitAssignmentStmt" ):
                return visitor.visitAssignmentStmt(self)
            else:
                return visitor.visitChildren(self)



    def statement(self):

        localctx = SimpleLangParser.StatementContext(self, self._ctx, self.state)
        self.enterRule(localctx, 2, self.RULE_statement)
        self._la = 0 # Token type
        try:
            self.state = 40
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [5]:
                localctx = SimpleLangParser.AssignmentStmtContext(self, localctx)
                self.enterOuterAlt(localctx, 1)
                self.state = 24
                self.assignment()
                self.state = 25
                self.match(SimpleLangParser.SEMI)
                pass
            elif token in [1]:
                localctx = SimpleLangParser.IfStmtContext(self, localctx)
                self.enterOuterAlt(localctx, 2)
                self.state = 27
                self.ifStatement()
                pass
            elif token in [3]:
                localctx = SimpleLangParser.WhileStmtContext(self, localctx)
                self.enterOuterAlt(localctx, 3)
                self.state = 28
                self.whileStatement()
                pass
            elif token in [4]:
                localctx = SimpleLangParser.PrintStmtContext(self, localctx)
                self.enterOuterAlt(localctx, 4)
                self.state = 29
                self.printStatement()
                self.state = 30
                self.match(SimpleLangParser.SEMI)
                pass
            elif token in [22]:
                localctx = SimpleLangParser.BlockStmtContext(self, localctx)
                self.enterOuterAlt(localctx, 5)
                self.state = 32
                self.match(SimpleLangParser.LBRACE)
                self.state = 36
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                while (((_la) & ~0x3f) == 0 and ((1 << _la) & 4194362) != 0):
                    self.state = 33
                    self.statement()
                    self.state = 38
                    self._errHandler.sync(self)
                    _la = self._input.LA(1)

                self.state = 39
                self.match(SimpleLangParser.RBRACE)
                pass
            else:
                raise NoViableAltException(self)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class AssignmentContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def ID(self):
            return self.getToken(SimpleLangParser.ID, 0)

        def EQ(self):
            return self.getToken(SimpleLangParser.EQ, 0)

        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)


        def getRuleIndex(self):
            return SimpleLangParser.RULE_assignment

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitAssignment" ):
                return visitor.visitAssignment(self)
            else:
                return visitor.visitChildren(self)




    def assignment(self):

        localctx = SimpleLangParser.AssignmentContext(self, self._ctx, self.state)
        self.enterRule(localctx, 4, self.RULE_assignment)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 42
            self.match(SimpleLangParser.ID)
            self.state = 43
            self.match(SimpleLangParser.EQ)
            self.state = 44
            self.expression(0)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class IfStatementContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def IF(self):
            return self.getToken(SimpleLangParser.IF, 0)

        def LPAREN(self):
            return self.getToken(SimpleLangParser.LPAREN, 0)

        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)


        def RPAREN(self):
            return self.getToken(SimpleLangParser.RPAREN, 0)

        def statement(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.StatementContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.StatementContext,i)


        def ELSE(self):
            return self.getToken(SimpleLangParser.ELSE, 0)

        def getRuleIndex(self):
            return SimpleLangParser.RULE_ifStatement

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitIfStatement" ):
                return visitor.visitIfStatement(self)
            else:
                return visitor.visitChildren(self)




    def ifStatement(self):

        localctx = SimpleLangParser.IfStatementContext(self, self._ctx, self.state)
        self.enterRule(localctx, 6, self.RULE_ifStatement)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 46
            self.match(SimpleLangParser.IF)
            self.state = 47
            self.match(SimpleLangParser.LPAREN)
            self.state = 48
            self.expression(0)
            self.state = 49
            self.match(SimpleLangParser.RPAREN)
            self.state = 50
            self.statement()
            self.state = 53
            self._errHandler.sync(self)
            la_ = self._interp.adaptivePredict(self._input,3,self._ctx)
            if la_ == 1:
                self.state = 51
                self.match(SimpleLangParser.ELSE)
                self.state = 52
                self.statement()


        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class WhileStatementContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def WHILE(self):
            return self.getToken(SimpleLangParser.WHILE, 0)

        def LPAREN(self):
            return self.getToken(SimpleLangParser.LPAREN, 0)

        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)


        def RPAREN(self):
            return self.getToken(SimpleLangParser.RPAREN, 0)

        def statement(self):
            return self.getTypedRuleContext(SimpleLangParser.StatementContext,0)


        def getRuleIndex(self):
            return SimpleLangParser.RULE_whileStatement

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitWhileStatement" ):
                return visitor.visitWhileStatement(self)
            else:
                return visitor.visitChildren(self)




    def whileStatement(self):

        localctx = SimpleLangParser.WhileStatementContext(self, self._ctx, self.state)
        self.enterRule(localctx, 8, self.RULE_whileStatement)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 55
            self.match(SimpleLangParser.WHILE)
            self.state = 56
            self.match(SimpleLangParser.LPAREN)
            self.state = 57
            self.expression(0)
            self.state = 58
            self.match(SimpleLangParser.RPAREN)
            self.state = 59
            self.statement()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class PrintStatementContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def PRINT(self):
            return self.getToken(SimpleLangParser.PRINT, 0)

        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)


        def getRuleIndex(self):
            return SimpleLangParser.RULE_printStatement

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitPrintStatement" ):
                return visitor.visitPrintStatement(self)
            else:
                return visitor.visitChildren(self)




    def printStatement(self):

        localctx = SimpleLangParser.PrintStatementContext(self, self._ctx, self.state)
        self.enterRule(localctx, 10, self.RULE_printStatement)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 61
            self.match(SimpleLangParser.PRINT)
            self.state = 62
            self.expression(0)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class ExpressionContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return SimpleLangParser.RULE_expression

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)


    class MulDivExprContext(ExpressionContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.ExpressionContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expression(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.ExpressionContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,i)

        def MUL(self):
            return self.getToken(SimpleLangParser.MUL, 0)
        def DIV(self):
            return self.getToken(SimpleLangParser.DIV, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitMulDivExpr" ):
                return visitor.visitMulDivExpr(self)
            else:
                return visitor.visitChildren(self)


    class CompareExprContext(ExpressionContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.ExpressionContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expression(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.ExpressionContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,i)

        def GT(self):
            return self.getToken(SimpleLangParser.GT, 0)
        def LT(self):
            return self.getToken(SimpleLangParser.LT, 0)
        def GTE(self):
            return self.getToken(SimpleLangParser.GTE, 0)
        def LTE(self):
            return self.getToken(SimpleLangParser.LTE, 0)
        def EQEQ(self):
            return self.getToken(SimpleLangParser.EQEQ, 0)
        def NEQ(self):
            return self.getToken(SimpleLangParser.NEQ, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitCompareExpr" ):
                return visitor.visitCompareExpr(self)
            else:
                return visitor.visitChildren(self)


    class AtomExprContext(ExpressionContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.ExpressionContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def atom(self):
            return self.getTypedRuleContext(SimpleLangParser.AtomContext,0)


        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitAtomExpr" ):
                return visitor.visitAtomExpr(self)
            else:
                return visitor.visitChildren(self)


    class AddSubExprContext(ExpressionContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.ExpressionContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expression(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(SimpleLangParser.ExpressionContext)
            else:
                return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,i)

        def PLUS(self):
            return self.getToken(SimpleLangParser.PLUS, 0)
        def MINUS(self):
            return self.getToken(SimpleLangParser.MINUS, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitAddSubExpr" ):
                return visitor.visitAddSubExpr(self)
            else:
                return visitor.visitChildren(self)


    class UnaryMinusExprContext(ExpressionContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.ExpressionContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def MINUS(self):
            return self.getToken(SimpleLangParser.MINUS, 0)
        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)


        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitUnaryMinusExpr" ):
                return visitor.visitUnaryMinusExpr(self)
            else:
                return visitor.visitChildren(self)



    def expression(self, _p:int=0):
        _parentctx = self._ctx
        _parentState = self.state
        localctx = SimpleLangParser.ExpressionContext(self, self._ctx, _parentState)
        _prevctx = localctx
        _startState = 12
        self.enterRecursionRule(localctx, 12, self.RULE_expression, _p)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 68
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [17]:
                localctx = SimpleLangParser.UnaryMinusExprContext(self, localctx)
                self._ctx = localctx
                _prevctx = localctx

                self.state = 65
                self.match(SimpleLangParser.MINUS)
                self.state = 66
                self.expression(2)
                pass
            elif token in [5, 6, 7, 8, 20]:
                localctx = SimpleLangParser.AtomExprContext(self, localctx)
                self._ctx = localctx
                _prevctx = localctx
                self.state = 67
                self.atom()
                pass
            else:
                raise NoViableAltException(self)

            self._ctx.stop = self._input.LT(-1)
            self.state = 81
            self._errHandler.sync(self)
            _alt = self._interp.adaptivePredict(self._input,6,self._ctx)
            while _alt!=2 and _alt!=ATN.INVALID_ALT_NUMBER:
                if _alt==1:
                    if self._parseListeners is not None:
                        self.triggerExitRuleEvent()
                    _prevctx = localctx
                    self.state = 79
                    self._errHandler.sync(self)
                    la_ = self._interp.adaptivePredict(self._input,5,self._ctx)
                    if la_ == 1:
                        localctx = SimpleLangParser.CompareExprContext(self, SimpleLangParser.ExpressionContext(self, _parentctx, _parentState))
                        self.pushNewRecursionContext(localctx, _startState, self.RULE_expression)
                        self.state = 70
                        if not self.precpred(self._ctx, 5):
                            from antlr4.error.Errors import FailedPredicateException
                            raise FailedPredicateException(self, "self.precpred(self._ctx, 5)")
                        self.state = 71
                        _la = self._input.LA(1)
                        if not((((_la) & ~0x3f) == 0 and ((1 << _la) & 64512) != 0)):
                            self._errHandler.recoverInline(self)
                        else:
                            self._errHandler.reportMatch(self)
                            self.consume()
                        self.state = 72
                        self.expression(6)
                        pass

                    elif la_ == 2:
                        localctx = SimpleLangParser.AddSubExprContext(self, SimpleLangParser.ExpressionContext(self, _parentctx, _parentState))
                        self.pushNewRecursionContext(localctx, _startState, self.RULE_expression)
                        self.state = 73
                        if not self.precpred(self._ctx, 4):
                            from antlr4.error.Errors import FailedPredicateException
                            raise FailedPredicateException(self, "self.precpred(self._ctx, 4)")
                        self.state = 74
                        _la = self._input.LA(1)
                        if not(_la==16 or _la==17):
                            self._errHandler.recoverInline(self)
                        else:
                            self._errHandler.reportMatch(self)
                            self.consume()
                        self.state = 75
                        self.expression(5)
                        pass

                    elif la_ == 3:
                        localctx = SimpleLangParser.MulDivExprContext(self, SimpleLangParser.ExpressionContext(self, _parentctx, _parentState))
                        self.pushNewRecursionContext(localctx, _startState, self.RULE_expression)
                        self.state = 76
                        if not self.precpred(self._ctx, 3):
                            from antlr4.error.Errors import FailedPredicateException
                            raise FailedPredicateException(self, "self.precpred(self._ctx, 3)")
                        self.state = 77
                        _la = self._input.LA(1)
                        if not(_la==18 or _la==19):
                            self._errHandler.recoverInline(self)
                        else:
                            self._errHandler.reportMatch(self)
                            self.consume()
                        self.state = 78
                        self.expression(4)
                        pass

             
                self.state = 83
                self._errHandler.sync(self)
                _alt = self._interp.adaptivePredict(self._input,6,self._ctx)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.unrollRecursionContexts(_parentctx)
        return localctx


    class AtomContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return SimpleLangParser.RULE_atom

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)



    class NumberAtomContext(AtomContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.AtomContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def NUMBER(self):
            return self.getToken(SimpleLangParser.NUMBER, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitNumberAtom" ):
                return visitor.visitNumberAtom(self)
            else:
                return visitor.visitChildren(self)


    class StringAtomContext(AtomContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.AtomContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def STRING(self):
            return self.getToken(SimpleLangParser.STRING, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitStringAtom" ):
                return visitor.visitStringAtom(self)
            else:
                return visitor.visitChildren(self)


    class ParenExprContext(AtomContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.AtomContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def LPAREN(self):
            return self.getToken(SimpleLangParser.LPAREN, 0)
        def expression(self):
            return self.getTypedRuleContext(SimpleLangParser.ExpressionContext,0)

        def RPAREN(self):
            return self.getToken(SimpleLangParser.RPAREN, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitParenExpr" ):
                return visitor.visitParenExpr(self)
            else:
                return visitor.visitChildren(self)


    class FloatAtomContext(AtomContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.AtomContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def FLOAT(self):
            return self.getToken(SimpleLangParser.FLOAT, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitFloatAtom" ):
                return visitor.visitFloatAtom(self)
            else:
                return visitor.visitChildren(self)


    class IdAtomContext(AtomContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a SimpleLangParser.AtomContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def ID(self):
            return self.getToken(SimpleLangParser.ID, 0)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitIdAtom" ):
                return visitor.visitIdAtom(self)
            else:
                return visitor.visitChildren(self)



    def atom(self):

        localctx = SimpleLangParser.AtomContext(self, self._ctx, self.state)
        self.enterRule(localctx, 14, self.RULE_atom)
        try:
            self.state = 92
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [20]:
                localctx = SimpleLangParser.ParenExprContext(self, localctx)
                self.enterOuterAlt(localctx, 1)
                self.state = 84
                self.match(SimpleLangParser.LPAREN)
                self.state = 85
                self.expression(0)
                self.state = 86
                self.match(SimpleLangParser.RPAREN)
                pass
            elif token in [6]:
                localctx = SimpleLangParser.NumberAtomContext(self, localctx)
                self.enterOuterAlt(localctx, 2)
                self.state = 88
                self.match(SimpleLangParser.NUMBER)
                pass
            elif token in [5]:
                localctx = SimpleLangParser.IdAtomContext(self, localctx)
                self.enterOuterAlt(localctx, 3)
                self.state = 89
                self.match(SimpleLangParser.ID)
                pass
            elif token in [8]:
                localctx = SimpleLangParser.StringAtomContext(self, localctx)
                self.enterOuterAlt(localctx, 4)
                self.state = 90
                self.match(SimpleLangParser.STRING)
                pass
            elif token in [7]:
                localctx = SimpleLangParser.FloatAtomContext(self, localctx)
                self.enterOuterAlt(localctx, 5)
                self.state = 91
                self.match(SimpleLangParser.FLOAT)
                pass
            else:
                raise NoViableAltException(self)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx



    def sempred(self, localctx:RuleContext, ruleIndex:int, predIndex:int):
        if self._predicates == None:
            self._predicates = dict()
        self._predicates[6] = self.expression_sempred
        pred = self._predicates.get(ruleIndex, None)
        if pred is None:
            raise Exception("No predicate with index:" + str(ruleIndex))
        else:
            return pred(localctx, predIndex)

    def expression_sempred(self, localctx:ExpressionContext, predIndex:int):
            if predIndex == 0:
                return self.precpred(self._ctx, 5)
         

            if predIndex == 1:
                return self.precpred(self._ctx, 4)
         

            if predIndex == 2:
                return self.precpred(self._ctx, 3)
         




