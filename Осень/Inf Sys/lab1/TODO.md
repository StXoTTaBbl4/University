### TODO Лабораторная работа 1
- [x] Страница с таблицей. Показывает в строке информацию о vehicle
- [x] Страница с таблицей. Показывает в строке информацию о coordinates, открывается на отдельной вкладке на странице
- [x] Кнопка добавления сущности 
- [x] Сортировка по возрастанию/убыванию
- [x] По нажатию на строку происходит переход на страницу с развернутой информацией (модификации, связи)
+ здесь же реализовать редактуру строк
- [x] Добавить `websocket` реализацию на ивент удаления/добавления строк таблицы
- [x] Добавить проверку типа пользователя перед удалением объекта (админ/юзер)
- [x] Поиск по колонкам
- [x] Кнопка Хочу в админы
- [x] Сверху в меню ник пользователя если зареган
- [x] Добавить админ-консоль, откуда можно управлять ролями пользователей, а так же создавать их напрямую
- [x] у админов(если есть) уведомление о хотении в админы
- [x] В системе должен быть реализован отдельный пользовательский интерфейс для выполнения специальных операций над объектами:
  - [x] Удалить все объекты, значение поля fuelType которого эквивалентно заданному.
  - [x] Вернуть один (любой) объект, значение поля name которого является максимальным.
  - [x] Вернуть массив объектов, значение поля fuelConsumption которых больше заданного.
  - [x] "Скрутить" счётчик пробега транспортного средства с заданным id до нуля.
  - [x] Добавить транспортному средству с заданным id указанное число колёс.
  - Представленные операции должны быть реализованы в рамках компонентов бизнес-логики приложения без прямого использования функций и процедур БД.

### TODO Лабораторная работа 2
- [x] загрузка csv файла
- [x] доработать сервисы, добавив блокировки в транзакциях
- [x] Добавить уникальность поля Vehicle:name
- [x] Добавить верхнее ограничение к Vehicle:numberOfWheels
- [x] Добавить ограничение not null к Vehicle:numberOfWheels
- [x] Добавить верхнее значение к Coordinates:y
- [x] Реализовать сценарий с использованием Apache JMeter
  - [x] Одновременное:
    - [x] Создание
    - [x] Редактирование
    - [x] Удаление
    - [x] импорт объектов
  - [x] Корректность поведения системы при попытке нескольких пользователей обновить и\или удалить один и тот же объект
  - [x] Корректность соблюдения системой ограничений уникальности предметной области при одновременной попытке нескольких пользователей создать объект с одним и тем же уникальным значением
### TODO Лабораторная работа 3
- [] TransactionManager – координатор транзакции.
- [] интерфейс участников (БД, MinIO).
- [] DatabaseTransactionParticipant – работа с БД через Hibernate.
- [] MinioTransactionParticipant – работа с MinIO через S3 SDK.
- [] ImportService – сервис, запускающий импорт с транзакцией.