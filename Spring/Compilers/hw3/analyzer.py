class LL1Parser:
    def __init__(self):
        self.terminals = {'a', 'b', 'c', 'd', '$'}
        self.non_terminals = {'S', 'A', 'B', 'B\'', 'C', 'C\'', 'C\'\''}
        self.start_symbol = 'S'
        self.parsing_table = {
            'S': {
                'a': ['A', 'B', 'C', 'C'],
                'c': ['A', 'B', 'C', 'C']
            },
            'A': {
                'a': ['a', 'A', 'a'],
                'c': ['c']
            },
            'B': {
                'b': ['b', 'B\'']
            },
            'B\'': {
                'a': ['a', 'B\''],
                'b': ['b', 'B\''],
                'd': ['d']  # B' -> d
            },
            'C': {
                'c': ['c', 'C\'']
            },
            'C\'': {
                'c': ['c', 'C\'\''],
                'd': ['d']  # C' -> d
            },
            'C\'\'': {
                'b': ['B', 'B'], # C'' -> BB
                'c': ['c', 'A']   # C'' -> cA
            }
        }
        self.error_messages = {} # Для пользовательских сообщений об ошибках

    def add_custom_error(self, non_terminal, terminal, message):
        if non_terminal not in self.error_messages:
            self.error_messages[non_terminal] = {}
        self.error_messages[non_terminal][terminal] = message

    def parse(self, input_string):
        input_tokens = list(input_string) + ['$']
        stack = ['$', self.start_symbol]
        
        idx = 0
        log = []
        errors_encountered = 0

        log.append(f"{'Stack':<30} | {'Input':<30} | Action")
        log.append("-" * 80)

        while stack[-1] != '$':
            current_stack_str = ' '.join(stack)
            current_input_str = ''.join(input_tokens[idx:])
            
            X = stack[-1]
            a = input_tokens[idx]

            log_entry_prefix = f"{current_stack_str:<30} | {current_input_str:<30} | "

            if X == a: # Случай 1: Вершина стека совпадает с текущим входным символом (оба терминалы)
                action = f"Match and pop '{a}'"
                log.append(log_entry_prefix + action)
                stack.pop()
                idx += 1
            elif X in self.terminals: # Случай 2: Вершина стека - терминал, но не совпадает с входным
                action = f"Error: Mismatch. Stack top: '{X}', Input: '{a}'"
                log.append(log_entry_prefix + action)
                errors_encountered += 1
                print("\nParsing Log:")
                print("\n".join(log))
                print(f"\nError {errors_encountered}: Mismatch. Expected '{X}' but got '{a}' at position {idx}.")
                return False, errors_encountered
            elif X in self.non_terminals: # Случай 3: Вершина стека - нетерминал
                if a in self.parsing_table[X]:
                    production = self.parsing_table[X][a]
                    action = f"Apply rule: {X} -> {''.join(production)}"
                    log.append(log_entry_prefix + action)
                    stack.pop()
                    # Помещаем символы из правой части продукции в стек в обратном порядке
                    # 'd' здесь трактуется как терминал, поэтому он добавляется в стек
                    for symbol in reversed(production):
                        stack.append(symbol)
                else: # Случай 4: Ошибка - нет правила в таблице M[X, a]
                    action = f"Error: No rule for M[{X}, {a}]"
                    log.append(log_entry_prefix + action)
                    errors_encountered += 1
                    
                    custom_msg = self.error_messages.get(X, {}).get(a)
                    error_detail = f"No production rule for non-terminal '{X}' and input symbol '{a}' at position {idx}."
                    if custom_msg:
                        error_detail += f" ({custom_msg})"
                    
                    print("\nParsing Log:")
                    print("\n".join(log))
                    print(f"\nError {errors_encountered}: {error_detail}")
                    return False, errors_encountered
            else: # Не должно произойти, если грамматика и таблица корректны
                action = f"Error: Unknown symbol on stack '{X}'"
                log.append(log_entry_prefix + action)
                errors_encountered += 1
                print("\nParsing Log:")
                print("\n".join(log))
                print(f"\nError {errors_encountered}: Unknown symbol '{X}' on stack.")
                return False, errors_encountered

            if errors_encountered > 0 and errors_encountered >=2: # Для примера, останавливаемся после 2 ошибок
                # В более сложных системах здесь могла бы быть попытка восстановления
                print(f"Stopped after {errors_encountered} errors.")
                # return False, errors_encountered # Раскомментировать если нужно останавливаться после N ошибок
                pass # Пока просто продолжаем для демонстрации

        # Конец основного цикла
        current_stack_str = ' '.join(stack)
        current_input_str = ''.join(input_tokens[idx:])
        log_entry_prefix = f"{current_stack_str:<30} | {current_input_str:<30} | "

        if stack[-1] == '$' and input_tokens[idx] == '$':
            log.append(log_entry_prefix + "Accept")
            print("\nParsing Log:")
            print("\n".join(log))
            print("\nInput string accepted.")
            return True, errors_encountered
        else:
            log.append(log_entry_prefix + "Error: Input remaining or stack not empty")
            errors_encountered +=1
            print("\nParsing Log:")
            print("\n".join(log))
            if input_tokens[idx] != '$':
                 print(f"\nError {errors_encountered}: Input remaining ('{''.join(input_tokens[idx:])}') when parsing should have finished.")
            else:
                 print(f"\nError {errors_encountered}: Stack not empty ('{' '.join(stack)}') when input exhausted.")
            return False, errors_encountered

# --- Пример использования ---
parser = LL1Parser()

# Добавление обработчиков для двух "промахов" (пустых ячеек)
# Промах 1: S не может начинаться с 'b'
parser.add_custom_error('S', 'b', "S должен начинаться с 'a' или 'c'.")
# Промах 2: A не может начинаться с 'b'
parser.add_custom_error('A', 'b', "A должен начинаться с 'a' или 'c'.")
# Промах 3: B' не может начинаться с 'c'
parser.add_custom_error('B\'', 'c', "B' может начинаться с 'a', 'b' или 'd'.")


print("--- Корректные цепочки ---")
# Пример 1: S -> ABCC -> (c)(bd)(cd)(cd) = cbdcdcd
# A -> c
# B -> bB' -> bd
# C -> cC' -> cd
# C -> cC' -> cd
correct_chain_1 = "cbdcdcd"
print(f"\nParsing '{correct_chain_1}':")
result, errors = parser.parse(correct_chain_1)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Пример 2: S -> ABCC -> (aAa)(bB'bB'd)(cC''cA)(cd) -> (aca)(bbd)(cc c)(cd) = acabbdcccd
# A -> aAa -> aca
# B -> bB' -> b bB' -> b b d
# C -> cC' -> c cC'' -> c c A -> c c c
# C -> cC' -> cd
correct_chain_2 = "acabdcdcd"
print(f"\nParsing '{correct_chain_2}':")
result, errors = parser.parse(correct_chain_2)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Пример 3: S -> ABCC -> (aAa)(bB'aB'd)(cC''BB)(cd) -> (aca)(bad)(c(bd)(bd))(cd) = acabdcbdcdcd
# A -> aAa -> aca
# B -> bB' -> b aB' -> b a d
# C -> cC' -> c cC'' -> c B B -> c (bd) (bd) -> cbdcd
# C -> cC' -> cd
correct_chain_3 = "cbabbdcccAccbdbbbd"
print(f"\nParsing '{correct_chain_3}':")
result, errors = parser.parse(correct_chain_3)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")


print("\n--- Ошибочные цепочки ---")
# Ошибка 1: S не может начинаться с 'b' (сработает пользовательское сообщение)
error_chain_1 = "bacd"
print(f"\nParsing '{error_chain_1}':")
result, errors = parser.parse(error_chain_1)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Ошибка 2: После 'a' в продукции A->aAa ожидается 'a' или 'c' (для внутреннего A), а не 'b'
# S -> A... -> aAa...
# Если вход ab...
# S -> ABCC
# A -> aAa (ожидает 'a' на входе, получает 'b')
# Stack: $ C C B a A a
# Input: b ... $
# Match 'a'
# Stack: $ C C B a A
# Input: b ... $
# X = A, input = b. M[A,b] - пусто. (сработает пользовательское сообщение для A,b)
error_chain_2 = "ab" 
print(f"\nParsing '{error_chain_2}':")
result, errors = parser.parse(error_chain_2)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Ошибка 3: Неожиданный символ в конце
error_chain_3 = "cbdcdcde"
print(f"\nParsing '{error_chain_3}':")
result, errors = parser.parse(error_chain_3)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Ошибка 4: Слишком короткая строка
error_chain_4 = "cb"
print(f"\nParsing '{error_chain_4}':")
result, errors = parser.parse(error_chain_4)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")

# Ошибка 5: Проверка B' -> c (ошибка, так как B' не может начинаться с 'c')
# S -> A B C C
# A -> c
# B -> b B'
# Если вход "cbca..."
# После 'cb', стек ... B', вход 'c'. M[B',c] - пусто.
error_chain_5 = "cbcdcdcd"
print(f"\nParsing '{error_chain_5}':")
result, errors = parser.parse(error_chain_5)
print(f"Result: {'Accepted' if result else 'Rejected'}, Errors: {errors}")