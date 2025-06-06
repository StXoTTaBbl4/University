# Generated from SimpleLang.g4 by ANTLR 4.13.2
from antlr4 import *
if "." in __name__:
    from .SimpleLangParser import SimpleLangParser
else:
    from SimpleLangParser import SimpleLangParser

# This class defines a complete generic visitor for a parse tree produced by SimpleLangParser.

class SimpleLangVisitor(ParseTreeVisitor):

    # Visit a parse tree produced by SimpleLangParser#program.
    def visitProgram(self, ctx:SimpleLangParser.ProgramContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#AssignmentStmt.
    def visitAssignmentStmt(self, ctx:SimpleLangParser.AssignmentStmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#IfStmt.
    def visitIfStmt(self, ctx:SimpleLangParser.IfStmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#WhileStmt.
    def visitWhileStmt(self, ctx:SimpleLangParser.WhileStmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#PrintStmt.
    def visitPrintStmt(self, ctx:SimpleLangParser.PrintStmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#BlockStmt.
    def visitBlockStmt(self, ctx:SimpleLangParser.BlockStmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#assignment.
    def visitAssignment(self, ctx:SimpleLangParser.AssignmentContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#ifStatement.
    def visitIfStatement(self, ctx:SimpleLangParser.IfStatementContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#whileStatement.
    def visitWhileStatement(self, ctx:SimpleLangParser.WhileStatementContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#printStatement.
    def visitPrintStatement(self, ctx:SimpleLangParser.PrintStatementContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#MulDivExpr.
    def visitMulDivExpr(self, ctx:SimpleLangParser.MulDivExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#CompareExpr.
    def visitCompareExpr(self, ctx:SimpleLangParser.CompareExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#AtomExpr.
    def visitAtomExpr(self, ctx:SimpleLangParser.AtomExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#AddSubExpr.
    def visitAddSubExpr(self, ctx:SimpleLangParser.AddSubExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#UnaryMinusExpr.
    def visitUnaryMinusExpr(self, ctx:SimpleLangParser.UnaryMinusExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#ParenExpr.
    def visitParenExpr(self, ctx:SimpleLangParser.ParenExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#NumberAtom.
    def visitNumberAtom(self, ctx:SimpleLangParser.NumberAtomContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#IdAtom.
    def visitIdAtom(self, ctx:SimpleLangParser.IdAtomContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#StringAtom.
    def visitStringAtom(self, ctx:SimpleLangParser.StringAtomContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by SimpleLangParser#FloatAtom.
    def visitFloatAtom(self, ctx:SimpleLangParser.FloatAtomContext):
        return self.visitChildren(ctx)



del SimpleLangParser