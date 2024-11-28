CREATE TABLE UserLog (
    log_id SERIAL PRIMARY KEY,
    new_user_id INTEGER NOT NULL,
    new_user_name VARCHAR NOT NULL,
    added_by_user VARCHAR NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION log_new_user()
RETURNS TRIGGER AS $$
BEGIN
    -- Вставляем данные о новом пользователе в таблицу логов
    INSERT INTO UserLog (new_user_id, new_user_name, added_by_user)
    VALUES (NEW.id, NEW.name, CURRENT_USER);

    RETURN NEW; -- Возвращаем новую строку, чтобы продолжить вставку
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_employee_insert
AFTER INSERT ON Employee
FOR EACH ROW
EXECUTE FUNCTION log_new_user();

CREATE OR REPLACE FUNCTION get_employees_with_level_above(p_level INTEGER)
RETURNS TABLE(
    employee_id INTEGER,
    name VARCHAR,
    email VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.name, e.email
    FROM Employee e
    JOIN EmployeeSkills es ON e.id = es.employeeID
    JOIN Levels l ON es.levelID = l.id
    WHERE l.weight > p_level;
END;
$$ LANGUAGE plpgsql;

-- Создаем процедуру для назначения задачи
CREATE OR REPLACE PROCEDURE assign_task_to_employee(
    p_employee_id INTEGER,
    p_task_id INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    required_level_id INTEGER;
    employee_level_id INTEGER;
BEGIN
    -- Проверяем, какой уровень навыков требуется для задачи
    SELECT levelID
    INTO required_level_id
    FROM EmployeeSkills
    WHERE taskID = p_task_id
    LIMIT 1;

    -- Проверяем, какой уровень навыков есть у сотрудника
    SELECT levelID
    INTO employee_level_id
    FROM EmployeeSkills
    WHERE employeeID = p_employee_id AND taskID = p_task_id
    LIMIT 1;

    -- Если уровень навыков у сотрудника ниже необходимого, выбрасываем исключение
    IF employee_level_id IS NULL OR employee_level_id < required_level_id THEN
        RAISE EXCEPTION 'Employee % does not have the required skill level for task %', p_employee_id, p_task_id;
    END IF;

    -- Если все проверки пройдены, назначаем задачу
    INSERT INTO TasksAssignment (taskID, employeeID)
    VALUES (p_task_id, p_employee_id);

    RAISE NOTICE 'Task % successfully assigned to employee %', p_task_id, p_employee_id;
END;
$$;