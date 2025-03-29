class DFA:
    def __init__(self):
        self.states = {"q0", "q1", "q2", "q3", "q4","q5"}
        self.alphabet = {"a", "b", "c"}
        self.start_state = "q0"
        self.accept_states = "q5"
        self.exec_trace = ""

        # Таблица переходов (состояние -> {входной символ -> следующее состояние})
        self.transitions = {
            "q0": {"a": "q1", "b": "q3", "c": "q3"},    # S
            "q1": {"b": "q2", "c": "q2"},               # F
            "q2": {"b": "q1", "c": "q1", "#": "q5"},    # M
            "q3": {"a": "q2", "b": "q4", "c": "q4"},    # X
            "q4": {"a": "q1", "b": "q3", "c": "q3"},    # Z
        }

    def process_input(self, input_string):
        self.exec_trace = ""
        self.exec_trace += f"Получена строка: {input_string}" + "\n"
        state = self.start_state
        for i, symbol in enumerate(input_string):
            if symbol in self.transitions[state]:

                self.exec_trace += f"\tТекущий символ: {symbol}, позиция {i}, состояние: {state}" + "\n"

                state = self.transitions[state][symbol]

                self.exec_trace += f"\t\tСимвол найден в словаре, новое состояние: {state}" + "\n"


                if state == "q5":
                    return True
            else:
                self.exec_trace += f"Обнаружен недопустимый сценарий:\n\tСтрока: {input_string}\n\tЛитерал: {symbol}\n\tИндекс: {i}"
                return False  # Недопустимый символ или отсутствие перехода
        return False


dfa = DFA()

# TEST_CASES = [
#     ("a#", False),
#     ("aa#", False),
#     ("ac#", True),
#     ("ab#", True),
#     ("abc#", False),
#     ("acb#", False),
#     ("abcb#", True),
#     ("abcc#", True),
#     ("acbc#", True),
#     ("ca#", True),
#     ("ba#", True),
#     ("bca#", False),
#     ("cba#", False),
#     ("bcba#", True),
#     ("bcca#", True),
#     ("cbca#", True),
#     ("cabc#", True),
#     ("babc#", True),
#     ("cbac#", True),
#     ("bbac#", True),
#     ("cacc#", True),
#     ("babb#", True),
#     ("cabcc#", False),
#     ("babcc#", False),
#     ("cccacc#", True),
#     ("ccccac#", True),
#     ("bbbabb#", True),
#     ("bbbbab#", True),
#     ("bbbbba#", True),
#     ("abbbbb#", True),
# ]

TEST_CASES = [("bbba#", True)]

debug=False
display_trace = True
for _, (string, answer) in enumerate(TEST_CASES):
    result = dfa.process_input(string)
    if debug:
        print(f"Строка '{string}' -> {'Допустима' if result else 'Недопустима'}")
        print(dfa.exec_trace)
        continue

    # if not result and display_trace:
    #     print(f"\nСтрока {string} недопустима")

    if result != answer:
        print(f"\nОжидаемый ответ для строки {string}: {answer}. Получен: {result}")
        if display_trace:
            print(f"Этапы выполнения:")
            print(dfa.exec_trace)

