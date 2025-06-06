CREATE OR REPLACE PROCEDURE pg_temp.show_table_info(p_fullname text)
LANGUAGE plpgsql
AS $$
DECLARE
    v_counter integer := 0;
    v_col_comment text;
    v_index_names text;
    v_table_found boolean := false;
    v_tab_schema text;
    v_tab_name text;
    v_search_path_schemas text[];
    v_schema text;
    v_dot_count int;
    v_candidate regclass;
    rec record;
BEGIN
    -- Считаем количество точек
    v_dot_count := length(p_fullname) - length(replace(p_fullname, '.', ''));

    IF v_dot_count = 2 THEN
        -- db.schema.table: пропускаем имя базы
        v_tab_schema := split_part(p_fullname, '.', 2);
        v_tab_name := split_part(p_fullname, '.', 3);
    ELSIF v_dot_count = 1 THEN
        -- schema.table
        v_tab_schema := split_part(p_fullname, '.', 1);
        v_tab_name := split_part(p_fullname, '.', 2);
    ELSE
        -- только table
        v_tab_schema := NULL;
        v_tab_name := p_fullname;
    END IF;

    -- Если схема не задана, ищем по search_path
    IF v_tab_schema IS NULL THEN
        SELECT string_to_array(current_setting('search_path'), ',') INTO v_search_path_schemas;
        -- Добавляем явно нужные схемы
        v_search_path_schemas := array_prepend('pg_temp', v_search_path_schemas);
        v_search_path_schemas := array_append(v_search_path_schemas, 'pg_catalog');

        FOREACH v_schema IN ARRAY v_search_path_schemas LOOP
            PERFORM 1 FROM information_schema.tables
            WHERE table_schema = trim(both '"' from v_schema)
              AND table_name = v_tab_name;
            IF FOUND THEN
                v_tab_schema := trim(both '"' from v_schema);
                v_table_found := true;
                EXIT;
            END IF;
        END LOOP;

        IF NOT v_table_found THEN
            RAISE NOTICE 'Таблица "%" не найдена в схемах search_path', v_tab_name;
            RETURN;
        END IF;
    ELSE
        -- Если схема указана явно, проверяем существование таблицы
        PERFORM 1 FROM information_schema.tables
        WHERE table_schema = v_tab_schema
          AND table_name = v_tab_name;
        IF FOUND THEN
            v_table_found := true;
        ELSE
            RAISE NOTICE 'Таблица "%.% " не найдена или нет доступа', v_tab_schema, v_tab_name;
            RETURN;
        END IF;
    END IF;

    -- Вывод заголовка
    RAISE NOTICE 'Информация о таблице: %.%', v_tab_schema, v_tab_name;
    RAISE NOTICE ' No. Имя столбца                    Информация';
    RAISE NOTICE ' --- ------------------------------ --------------------------------------------';

    -- Обход столбцов
    FOR rec IN
        SELECT
            a.attnum,
            a.attname,
            pg_catalog.format_type(a.atttypid, a.atttypmod) as data_type,
            a.attnotnull as is_not_null,
            d.description
        FROM
            pg_catalog.pg_attribute a
        LEFT JOIN
            pg_catalog.pg_description d
        ON
            d.objoid = a.attrelid AND d.objsubid = a.attnum
        WHERE
            a.attrelid = to_regclass(quote_ident(v_tab_schema) || '.' || quote_ident(v_tab_name))
            AND a.attnum > 0
            AND NOT a.attisdropped
        ORDER BY
            a.attnum
    LOOP
        -- Получаем индексы по столбцу
        SELECT string_agg(
                   CASE
                       WHEN i.indisprimary THEN 'PK: ' || c.relname
                       WHEN i.indisunique THEN 'UNIQUE: ' || c.relname
                       ELSE 'INDEX: ' || c.relname
                   END, ', '
               )
        INTO v_index_names
        FROM pg_index i
        JOIN pg_class c ON c.oid = i.indexrelid
        JOIN pg_attribute a2 ON a2.attrelid = i.indrelid AND a2.attnum = ANY(i.indkey)
        WHERE i.indrelid = to_regclass(quote_ident(v_tab_schema) || '.' || quote_ident(v_tab_name))
          AND a2.attname = rec.attname;

        -- Вывод информации
        v_counter := v_counter + 1;
        RAISE NOTICE '% % Type: %',
            lpad(v_counter::text, 3) || '.',
            rpad(rec.attname, 30),
            rec.data_type;

        IF rec.is_not_null THEN
            RAISE NOTICE '                                    Constraint: NOT NULL';
        END IF;

        RAISE NOTICE '                                    Comment: %',
            COALESCE(rec.description, 'нет комментария');

        RAISE NOTICE '                                    Index: %',
            COALESCE(v_index_names, 'нет индексов');

        RAISE NOTICE '';
    END LOOP;
END;
$$;

CALL pg_temp.show_table_info(:'fullname');
