def print_error(inpt, symbol, index):
    print(f"Обнаружен недопустимый сценарий:\n\tСтрока: {inpt}\n\tЛитерал: {symbol}\n\tИндекс: {index}")


class DFA:
    def __init__(self):
        # Определение состояний
        # S→aX|bX|cX
        # X→bF|cF|
        # F→bX|cX|aG
        # G→bD|cD|E
        # D→bG|cG
        # E→ε
        self.states = {"q0", "q1", "q2", "q3", "q4", "q5", "q6", "q7"}  # Узлы
        self.alphabet = {"a", "b", "c"}  # Допустимые символы
        self.start_state = "q0"  # Начальное состояние
        self.accept_states = {"q7"}  # Конечное состояние

        # Таблица переходов (состояние -> {входной символ -> следующее состояние})
        self.transitions = {
            "q0": {"a": "q1", "b": "q3", "c": "q3"},    # S
            "q1": {"b": "q2", "c": "q2"},               # F
            "q2": {"b": "q1", "c": "q1", "#": "q7"},    # M
            "q3": {"a": "q4", "b": "q6", "c": "q6"},    # X
            "q4": {"b": "q5", "c": "q5", "#": "q7"},    # Z
            "q5": {"b": "q4", "c": "q4"},               # K
            "q6": {"a": "q8", "b": "q3", "c": "q3"},               # Y
            "q8": {"b": "q4", "c": "q4"},               # Y
        }

    def process_input(self, input_string, debug=False):
        if debug:
            print(f"Получена строка: {input_string}")
        state = self.start_state
        for i, symbol in enumerate(input_string):
            if symbol in self.transitions[state]:
                if debug:
                    print(f"\tТекущий символ: {symbol}, позиция {i}, состояние: {state}")

                state = self.transitions[state][symbol]

                if debug:
                    print(f"\t\tСимвол найден в словаре, новое состояние: {state}")

                if state == "q4":
                    return True
            else:
                print_error(input_string, symbol, i)
                return False  # Недопустимый символ или отсутствие перехода
        return False


dfa = DFA()

TEST_CASES = ["cbcbc#", "cbcbbca#", "ccccca#", "abacb#", "abc#", "abcb#", "bcba#", "bcac#", "cbac#", "ccac#", "bbbab#", "cbcacb"]
for string in TEST_CASES:
    result = dfa.process_input(string, debug=False)
    print(f"Строка '{string}' -> {'Допустима' if result else 'Недопустима'}")
