from SimpleLangParser import SimpleLangParser
from SimpleLangVisitor import SimpleLangVisitor

class Value:
    def __init__(self, value):
        self.value = value

    def is_int(self): return isinstance(self.value, int)
    def is_float(self): return isinstance(self.value, float)
    def is_string(self): return isinstance(self.value, str)
    def is_truthy(self):
        if self.is_int() or self.is_float():
            return self.value != 0
        if self.is_string():
            return len(self.value) > 0
        return False

    def __repr__(self):
        return f"Value({self.value}, {type(self.value).__name__})"
    
    def __str__(self):
        return str(self.value)

class MySimpleLangVisitor(SimpleLangVisitor):
    def __init__(self):
        self.memory = {}

    def _to_value(self, val_obj):
        """Извлекает Python значение из объекта Value"""
        if isinstance(val_obj, Value):
            return val_obj.value
        return val_obj

    def _get_variable(self, name):
        if name not in self.memory:
            raise NameError(f"Переменная '{name}' не была инициализирована.")
        return self.memory[name]


    def visitProgram(self, ctx:SimpleLangParser.ProgramContext):
        # print("Visiting Program")
        for stmt_ctx in ctx.statement():
            self.visit(stmt_ctx)
        return None

    def visitBlockStmt(self, ctx:SimpleLangParser.BlockStmtContext):
        # print("Visiting Block")
        for stmt_ctx in ctx.statement(): 
            self.visit(stmt_ctx)
        return None

    def visitAssignmentStmt(self, ctx:SimpleLangParser.AssignmentStmtContext):
        var_name = ctx.assignment().ID().getText()
        value_obj = self.visit(ctx.assignment().expression())
        # print(f"Assigning {var_name} = {value_obj}")
        self.memory[var_name] = value_obj
        return None

    def visitPrintStmt(self, ctx:SimpleLangParser.PrintStmtContext):
        value_obj = self.visit(ctx.printStatement().expression())
        print(self._to_value(value_obj))
        return None

    def visitIfStmt(self, ctx:SimpleLangParser.IfStmtContext):
        if_stmt_node = ctx.ifStatement()
        condition_obj = self.visit(if_stmt_node.expression())
        
        # print(f"If condition: {condition_obj.value} -> {condition_obj.is_truthy()}")

        if condition_obj.is_truthy():
            self.visit(if_stmt_node.statement(0))
        elif if_stmt_node.ELSE():
            self.visit(if_stmt_node.statement(1))
        return None

    def visitWhileStmt(self, ctx:SimpleLangParser.WhileStmtContext):
        while_stmt_node = ctx.whileStatement()
        # print("Entering While")
        while True:
            condition_obj = self.visit(while_stmt_node.expression())
            # print(f"While condition: {condition_obj.value} -> {condition_obj.is_truthy()}")
            if not condition_obj.is_truthy():
                break
            self.visit(while_stmt_node.statement())
        # print("Exiting While")
        return None

    def visitAtomExpr(self, ctx:SimpleLangParser.AtomExprContext):
        return self.visit(ctx.atom())

    def visitParenExpr(self, ctx:SimpleLangParser.ParenExprContext):
        return self.visit(ctx.expression())

    def visitNumberAtom(self, ctx:SimpleLangParser.NumberAtomContext):
        return Value(int(ctx.NUMBER().getText()))

    def visitIdAtom(self, ctx:SimpleLangParser.IdAtomContext):
        var_name = ctx.ID().getText()
        return self._get_variable(var_name)

    def visitStringAtom(self, ctx: SimpleLangParser.StringAtomContext):
        text = ctx.STRING().getText()
        return Value(text[1:-1].replace('\\"', '"').replace("\\'", "'").replace('\\\\', '\\'))


    def visitUnaryMinusExpr(self, ctx:SimpleLangParser.UnaryMinusExprContext):
        operand_obj = self.visit(ctx.expression())
        operand = self._to_value(operand_obj)

        if isinstance(operand, int):
            return Value(-operand)
        raise TypeError(f"Унарный минус не применим к типу {type(operand).__name__}")

    def _apply_arithmetic_op(self, left_obj, right_obj, op):
        left_py_val = self._to_value(left_obj)
        right_py_val = self._to_value(right_obj)

        if op == '+':
            if left_obj.is_string() or right_obj.is_string():
                return Value(str(left_py_val) + str(right_py_val))
        elif op == '*':
            if left_obj.is_string() and right_obj.is_int():
                return Value(left_py_val * right_py_val)
            if left_obj.is_int() and right_obj.is_string():
                return Value(left_py_val * right_py_val)
        
        if left_obj.is_float() or right_obj.is_float():
            if left_obj.is_string() or right_obj.is_string():
                raise TypeError(f"Нельзя '{op}' строку с числом, кроме '+' или '*' с int.")

            l_float = float(left_py_val)
            r_float = float(right_py_val)

            if op == '+': return Value(l_float + r_float)
            if op == '-': return Value(l_float - r_float)
            if op == '*': return Value(l_float * r_float)
            if op == '/':
                if r_float == 0.0: raise ZeroDivisionError("Деление на ноль (float)")
                return Value(l_float / r_float)
            raise Exception(f"Неизвестный арифметический оператор {op} для float")

        if left_obj.is_int() and right_obj.is_int():
            l_int = int(left_py_val)
            r_int = int(right_py_val)
            if op == '+': return Value(l_int + r_int)
            if op == '-': return Value(l_int - r_int)
            if op == '*': return Value(l_int * r_int)
            if op == '/':
                if r_int == 0: raise ZeroDivisionError("Деление на ноль (int)")
                # return Value(float(l_int) / r_int) 
                return Value(l_int // r_int)
            raise Exception(f"Неизвестный арифметический оператор {op} для int")

        raise TypeError(f"Неподдерживаемые типы для операции '{op}': "
                        f"{type(left_py_val).__name__} и {type(right_py_val).__name__}")


    def visitAddSubExpr(self, ctx:SimpleLangParser.AddSubExprContext):
            left_obj = self.visit(ctx.expression(0))
            right_obj = self.visit(ctx.expression(1))
            op = ctx.getChild(1).getText()
            return self._apply_arithmetic_op(left_obj, right_obj, op)

    def visitMulDivExpr(self, ctx:SimpleLangParser.MulDivExprContext):
            left_obj = self.visit(ctx.expression(0))
            right_obj = self.visit(ctx.expression(1))
            op = ctx.getChild(1).getText()
            return self._apply_arithmetic_op(left_obj, right_obj, op)

    def visitFloatAtom(self, ctx:SimpleLangParser.FloatAtomContext):
            return Value(float(ctx.FLOAT().getText()))

    def _apply_comparison_op(self, left_obj, right_obj, op):
        left_py_val = self._to_value(left_obj)
        right_py_val = self._to_value(right_obj)

        is_left_str = left_obj.is_string()
        is_right_str = right_obj.is_string()
        is_left_num = left_obj.is_int() or left_obj.is_float()
        is_right_num = right_obj.is_int() or right_obj.is_float()

        res = False
        can_compare = False

        if is_left_str and is_right_str:
            can_compare = True
        elif is_left_num and is_right_num:
            can_compare = True
        elif (op == '==' or op == '!=') and ( (is_left_num and is_right_str) or (is_left_str and is_right_num) ):
            res = (left_py_val == right_py_val) 
            return Value(1 if res else 0) if op == '==' else Value(1 if not res else 0)


        if not can_compare:
            raise TypeError(f"Нельзя сравнивать '{op}' типы {type(left_py_val).__name__} и {type(right_py_val).__name__}")

        if op == '==': res = (left_py_val == right_py_val)
        elif op == '!=': res = (left_py_val != right_py_val)
        elif op == '<':  res = (left_py_val < right_py_val)
        elif op == '<=': res = (left_py_val <= right_py_val)
        elif op == '>':  res = (left_py_val > right_py_val)
        elif op == '>=': res = (left_py_val >= right_py_val)
        else: raise Exception(f"Неизвестный оператор сравнения {op}")
        
        return Value(1 if res else 0)

    def visitCompareExpr(self, ctx:SimpleLangParser.CompareExprContext):
        left_obj = self.visit(ctx.expression(0))
        right_obj = self.visit(ctx.expression(1))
        op = ctx.getChild(1).getText()
        return self._apply_comparison_op(left_obj, right_obj, op)