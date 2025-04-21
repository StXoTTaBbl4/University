CREATE OR REPLACE PROCEDURE pg_temp.show_table_info(fullname text)
LANGUAGE plpgsql
AS $$
DECLARE
    rec record;
    counter integer := 0;
    col_comment text;
    constraint_info text;
    table_exists boolean;
BEGIN
    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = split_part(fullname, '.', 1)
        AND table_name = split_part(fullname, '.', 2)
    ) INTO table_exists;
       
    IF table_exists THEN
        RAISE NOTICE 'Информация о таблице: %', fullname;
        RAISE NOTICE ' No. Имя столбца                    Информация';
        RAISE NOTICE ' --- ------------------------------ ------------------------------------------';

        FOR rec IN
            SELECT
                a.attname,
                pg_catalog.format_type(a.atttypid, a.atttypmod) as data_type
            FROM
                pg_catalog.pg_attribute a
            WHERE
                a.attrelid = fullname::regclass
                AND a.attnum > 0
                AND NOT a.attisdropped
            ORDER BY a.attnum
        LOOP
            -- Комментарий к столбцу
            SELECT description INTO col_comment
            FROM pg_catalog.pg_description
            WHERE objoid = fullname::regclass
              AND objsubid = (SELECT a.attnum FROM pg_catalog.pg_attribute a
                              WHERE a.attrelid = fullname::regclass AND a.attname = rec.attname);

            -- Ограничения на столбец
            SELECT string_agg (
				conname || '  ' || (
					CASE contype
						WHEN 'p' THEN 'Primary key'
						WHEN 'u' THEN 'Unique'
						WHEN 'f' THEN 'Foreign key'
						WHEN 'c' THEN 'Check'
						WHEN 'n' THEN 'Not null'
						ELSE contype::text
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
                lpad(counter::text, 3) || '.',
                rpad(rec.attname, 30),
                rpad(rec.data_type, 30);
            RAISE NOTICE '                                    Commen: %',
                COALESCE(col_comment, 'нет комментария');

            IF constraint_info IS NOT NULL THEN
                RAISE NOTICE '                                    Constraint: %', constraint_info;
            END IF;
            RAISE NOTICE '';
        END LOOP;
    ELSE
        RAISE NOTICE 'Таблица % не существует или у вас нет к ней доступа', fullname;
    END IF;
END;
$$;

CALL pg_temp.show_table_info(:'fullname');
