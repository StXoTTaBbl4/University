def create_recognizer_for_regex():
    STATE_A = "A (не заканчивается на b)"
    STATE_B = "B (заканчивается на b)"

    initial_state = STATE_A
    final_states = {STATE_B}
    alphabet = {'a', 'b', 'c'}

    transitions = {
        STATE_A: {
            'a': STATE_A,
            'b': STATE_B,
            'c': STATE_A
        },
        STATE_B: {
            'a': STATE_A,
            'b': STATE_B,
            'c': STATE_A
        }
    }

    def recognizer(input_string: str) -> bool:
        current_state = initial_state
        print(f"\nПроверка строки: '{input_string}'")
        print(f"Начальное состояние: {current_state}")

        for char in input_string:
            if char not in alphabet:
                print(f"Ошибка: символ '{char}' не принадлежит алфавиту {{a, b, c}}.")
                return False

            previous_state = current_state
            current_state = transitions[current_state][char]
            print(f"Символ: '{char}', Переход: {previous_state} -> {current_state}")

        is_accepted = current_state in final_states
        print(f"Конечное состояние: {current_state}")
        print(f"Результат: Строка {'принята' if is_accepted else 'отклонена'}.")
        return is_accepted

    return recognizer

if __name__ == "__main__":
    my_recognizer = create_recognizer_for_regex()

    # Примеры правильных предложений
    print("--- Тестирование на правильных строках ---")
    my_recognizer("b")
    my_recognizer("ab")
    my_recognizer("cacabb")
    my_recognizer("bb")

    print("\n--- Тестирование на неправильных строках ---")
    my_recognizer("a")
    my_recognizer("bca")
    my_recognizer("")
    my_recognizer("d")