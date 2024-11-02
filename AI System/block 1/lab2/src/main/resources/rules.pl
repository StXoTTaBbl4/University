%Отбор георев по сторонам(плохие, хорошие, нейтарльные(условно)))
герои(Герой) :- организация('Овервотч', СписокГероев), member(Герой, СписокГероев).
злодеи(Герой) :- (организация('Коготь', СписокГероев), member(Герой, СписокГероев)); (организация('Банда мертвецов', СписокГероев), member(Герой, СписокГероев)); (организация('Нуль-сектор', СписокГероев), member(Герой, СписокГероев)).
нейтралы(Герой) :- организация('Нейтральный', СписокГероев), member(Герой, СписокГероев); организация('Вишкар', СписокГероев), member(Герой, СписокГероев);организация('Мусорщики', СписокГероев), member(Герой, СписокГероев).

танки(Герои) :- findall(Герой, (роль(Герой, Роль),Роль = 'танк'), Герои).
урон(Герои) :- findall(Герой, (роль(Герой, Роль),Роль = 'урон'), Герои).
поддержка(Герои) :- findall(Герой, (роль(Герой, Роль),Роль = 'поддержка'), Герои).

список_для_новичка(Герои) :- findall(Герой, (сложность(Герой, 'низкая'), актуальность(Герой, Актуальность),(Актуальность = 'средняя'; Актуальность = 'высокая')), Герои).
список_для_среднего(Герои) :- findall(Герой, (сложность(Герой, 'средняя'), актуальность(Герой, Актуальность),(Актуальность = 'средняя'; Актуальность = 'высокая')), Герои).
список_для_опытного(Герои) :- findall(Герой, (сложность(Герой, 'высокая'), актуальность(Герой, Актуальность),(Актуальность = 'высокая')), Герои).

список_актуальных(Герои) :- findall(Герой, (актуальность(Герой, Актуальность),Актуальность = 'высокая'), Герои).