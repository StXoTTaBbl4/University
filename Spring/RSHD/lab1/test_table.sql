CREATE TABLE test_dep (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE test (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    age INTEGER CHECK (age >= 18),
    department_id INTEGER,
    CONSTRAINT fk_department FOREIGN KEY (department_id)
        REFERENCES test_dep(id)
);

COMMENT ON COLUMN test.id IS 'Уникальный идентификатор сотрудника';
COMMENT ON COLUMN test.username IS 'Имя пользователя';
COMMENT ON COLUMN test.email IS 'Электронная почта сотрудника';
COMMENT ON COLUMN test.age IS 'Возраст сотрудника (не моложе 18)';
COMMENT ON COLUMN test.department_id IS 'Идентификатор отдела';

COMMENT ON COLUMN test_dep.id IS 'ID отдела';
COMMENT ON COLUMN test_dep.name IS 'Название отдела';