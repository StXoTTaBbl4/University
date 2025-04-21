CREATE OR REPLACE PROCEDURE pg_temp.show_table_info(fullname text)
LANGUAGE plpgsql
AS $$
DECLARE
    rec record;              -- Информация о столбцах таблицы
    counter integer := 0;    -- Счетчик столбцов
    col_comment text;        -- Комментария к столбцу
    constraint_info text;    -- Информация об ограничениях
    table_exists boolean;    -- Флаг существования таблицы
	schema_name text;		 --|
    table_only_name text;	 --| Для проверки наличия имени схемы и ее поиска
    search_path_schema text; --|
BEGIN
    -- Проверяем, указана ли схема в fullname (есть ли точка)
    IF position('.' in fullname) = 0 THEN
        -- Если схема не указана, берем первую из search_path
        SELECT split_part(setting, ',', 1) INTO search_path_schema
        FROM pg_settings WHERE name = 'search_path';
        
        search_path_schema := trim(both '"' from search_path_schema);
        search_path_schema := trim(search_path_schema);
		
        schema_name := search_path_schema;
        table_only_name := fullname;
        fullname := schema_name || '.' || table_only_name;
        
        RAISE NOTICE 'Схема не указана, используем схему из search_path: %', schema_name;
    ELSE
        -- Если схема указана, разбиваем на части
        schema_name := split_part(fullname, '.', 1);
        table_only_name := split_part(fullname, '.', 2);
    END IF;
    -- Проверяем, существует ли таблица с указанным именем
    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = split_part(fullname, '.', 1)  -- Схема
        AND table_name = split_part(fullname, '.', 2)      -- Имя таблицы
    ) INTO table_exists;
       
    IF table_exists THEN
        RAISE NOTICE 'Информация о таблице: %', fullname;
        RAISE NOTICE ' No. Имя столбца                    Информация';
        RAISE NOTICE ' --- ------------------------------ ------------------------------------------';

        FOR rec IN
            SELECT
                a.attname,  -- Имя столбца
                pg_catalog.format_type(a.atttypid, a.atttypmod) as data_type
            FROM
                pg_catalog.pg_attribute a
            WHERE
                a.attrelid = fullname::regclass  	-- Идентификатор таблицы
                AND a.attnum > 0                	-- Исключаем системные столбцы
                AND NOT a.attisdropped           	-- Исключаем удаленные столбцы
            ORDER BY a.attnum  						-- По номеру столбца
        LOOP
            -- Комментарий
            SELECT description INTO col_comment
            FROM pg_catalog.pg_description
            WHERE objoid = fullname::regclass
              AND objsubid = (SELECT a.attnum FROM pg_catalog.pg_attribute a
                              WHERE a.attrelid = fullname::regclass AND a.attname = rec.attname);

            -- Ограничения
            SELECT string_agg (
                conname || '  ' || (
                    CASE contype
                        WHEN 'p' THEN 'Primary key'   	-- Первичный ключ
                        WHEN 'u' THEN 'Unique'       	-- Уникальное ограничение
                        WHEN 'f' THEN 'Foreign key'   	-- Внешний ключ
                        WHEN 'c' THEN 'Check'        	-- Проверочное ограничение
                        WHEN 'n' THEN 'Not null'      	-- Ограничение NOT NULL
                        ELSE contype::text            	-- Другие типы ограничений
                    END
                ),
                ','
            )
            INTO constraint_info
            FROM pg_constraint c	
            JOIN pg_class t ON t.oid = c.conrelid
            JOIN pg_namespace n ON n.oid = t.relnamespace
            JOIN unnest(c.conkey) AS k(attnum)
                ON k.attnum = (SELECT a.attnum FROM pg_attribute a
                               WHERE a.attrelid = t.oid AND a.attname = rec.attname)
            WHERE t.oid = fullname::regclass;

            counter := counter + 1;
            RAISE NOTICE '% % Type: %',
                lpad(counter::text, 3) || '.',  -- Номер столбца с выравниванием
                rpad(rec.attname, 30),          -- Имя столбца с выравниванием
                rpad(rec.data_type, 30);       -- Тип данных с выравниванием
            
            -- Выводим комментарий к столбцу (если есть)
            RAISE NOTICE '                                    Commen: %',
                COALESCE(col_comment, 'нет комментария');

            -- Выводим ограничения (если есть)
            IF constraint_info IS NOT NULL THEN
                RAISE NOTICE '                                    Constraint: %', constraint_info;
            END IF;
            RAISE NOTICE '';  -- Пустая строка для разделения информации о столбцах
        END LOOP;
    ELSE
        -- Сообщение, если таблица не существует или нет доступа
        RAISE NOTICE 'Таблица % не существует или у вас нет к ней доступа', fullname;
    END IF;
END;
$$;

-- Вызов процедуры с параметром fullname (передается как переменная)
CALL pg_temp.show_table_info(:'fullname');