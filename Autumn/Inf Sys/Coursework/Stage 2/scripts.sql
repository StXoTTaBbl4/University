CREATE TABLE user_log (
                         log_id SERIAL PRIMARY KEY,
                         new_user_id INTEGER NOT NULL,
                         new_user_name VARCHAR NOT NULL,
                         added_by_user VARCHAR NOT NULL DEFAULT 'SYSTEM',
                         added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION log_new_user()
    RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO user_log (new_user_id, new_user_name, added_by_user)
    VALUES (NEW.id, NEW.name, CURRENT_USER);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_employee_insert
    AFTER INSERT ON employee
    FOR EACH ROW
EXECUTE FUNCTION log_new_user();

CREATE OR REPLACE FUNCTION get_employees_with_level_above(p_level INTEGER)
    RETURNS TABLE(
                     employee_id BIGINT,
                     name VARCHAR,
                     email VARCHAR,
                     skill_id BIGINT
                 ) AS $$
BEGIN
    RETURN QUERY
        SELECT e.id, e.name, e.email, es.id
        FROM employee e
                 JOIN employee_skills es ON e.id = es."employee_id"
                 JOIN levels l ON es."level_id" = l.id
        WHERE l.weight > p_level;
END;
$$ LANGUAGE plpgsql;

-- НОВАЯ ФУГНЦИЯ НАФИГ СТАРУЮ
CREATE OR REPLACE FUNCTION get_employees_with_block_points_above_limit(p_min_hw_points INTEGER, p_min_sw_points INTEGER, p_min_pr_points INTEGER)
    RETURNS TABLE(
                     employee_id BIGINT,
                     name VARCHAR,
                     email VARCHAR,
                     hardware_points INTEGER,
                     software_points INTEGER,
                     processes_points INTEGER
                 ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            e.id AS employee_id,
            e.name,
            e.email,
            -- Сумма баллов для каждого блока
            COALESCE(SUM(CASE WHEN p.block_type = 'Hardware' THEN l.weight ELSE 0 END), 0)::INTEGER::INTEGER AS hardware_points,
            COALESCE(SUM(CASE WHEN p.block_type = 'Software' THEN l.weight ELSE 0 END), 0)::INTEGER AS software_points,
            COALESCE(SUM(CASE WHEN p.block_type = 'Processes' THEN l.weight ELSE 0 END), 0)::INTEGER AS processes_points
        FROM employee e
                 JOIN employee_skills es ON e.id = es."employee_id"
                 JOIN levels l ON es."level_id" = l.id
                 JOIN tasks t ON es."task_id" = t.id
                 JOIN products p ON t."product_id" = p.id
        GROUP BY e.id, e.name, e.email
        HAVING
            SUM(CASE WHEN p.block_type = 'Hardware' THEN l.weight ELSE 0 END) > p_min_hw_points AND
            SUM(CASE WHEN p.block_type = 'Software' THEN l.weight ELSE 0 END) > p_min_sw_points AND
            SUM(CASE WHEN p.block_type = 'Processes' THEN l.weight ELSE 0 END) > p_min_pr_points;
END;
$$ LANGUAGE plpgsql;




-- Создаем процедуру для назначения задачи
CREATE OR REPLACE PROCEDURE assign_task_to_employee(
    p_employee_id INTEGER,
    p_task_id INTEGER, 
	p_level_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    required_level_id INTEGER;
    employee_level_id INTEGER;
BEGIN
    -- Проверяем, какой уровень навыков требуется для задачи
    SELECT level_id
    INTO required_level_id
    FROM employee_skills
    WHERE task_id = p_task_id
    LIMIT 1;

    -- Проверяем, какой уровень навыков есть у сотрудника
    SELECT level_id
    INTO employee_level_id
    FROM employee_skills
    WHERE employee_id = p_employee_id AND task_id = p_task_id
    LIMIT 1;

    -- Если уровень навыков у сотрудника ниже необходимого, выбрасываем исключение
    IF employee_level_id IS NULL OR employee_level_id < required_level_id THEN
        RAISE EXCEPTION 'Employee % does not have the required skill level for task %', p_employee_id, p_task_id;
    END IF;

    -- Если все проверки пройдены, назначаем задачу
    INSERT INTO tasks_assignment (task_id, employee_id)
    VALUES (p_task_id, p_employee_id);

    RAISE NOTICE 'Task % successfully assigned to employee %', p_task_id, p_employee_id;
END;
$$;