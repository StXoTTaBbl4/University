import sys
from antlr4 import FileStream, CommonTokenStream, ParseTreeWalker
from SimpleLangLexer import SimpleLangLexer
from SimpleLangParser import SimpleLangParser
from MySimpleLangVisitor import MySimpleLangVisitor

def main(argv):
    if len(argv) < 2:
        print("Использование: python interpreter.py <файл_с_программой>")
        return

    input_file = argv[1]
    try:
        input_stream = FileStream(input_file, encoding='utf-8')
    except FileNotFoundError:
        print(f"Ошибка: Файл '{input_file}' не найден.")
        return
    except Exception as e:
        print(f"Ошибка при чтении файла: {e}")
        return

    lexer = SimpleLangLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = SimpleLangParser(stream)
    
    # parser.removeErrorListeners() 
    # parser.addErrorListener(MyErrorListener())

    try:
        tree = parser.program()
    except Exception as e:
        print(f"Ошибка синтаксиса: {e}")
        return

    if parser.getNumberOfSyntaxErrors() > 0:
        print(f"Обнаружено {parser.getNumberOfSyntaxErrors()} синтаксических ошибок. Интерпретация прервана.")
        return

    visitor = MySimpleLangVisitor()
    try:
        visitor.visit(tree)
    except NameError as e:
        print(f"Ошибка во время выполнения: {e}")
    except ZeroDivisionError as e:
        print(f"Ошибка во время выполнения: {e}")
    except TypeError as e:
        print(f"Ошибка типа во время выполнения: {e}")
    except Exception as e:
        print(f"Непредвиденная ошибка во время выполнения: {e}")

if __name__ == '__main__':
    main(sys.argv)