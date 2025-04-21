CREATE OR REPLACE PROCEDURE pg_temp.show_table_info(fullname text)
LANGUAGE plpgsql
AS $$
DECLARE
    rec record;
    counter integer := 0;
    col_comment text;
    index_info text;
    index_names text;
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
            SELECT description INTO col_comment
            FROM pg_catalog.pg_description
            WHERE objoid = fullname::regclass
              AND objsubid = (SELECT a.attnum FROM pg_catalog.pg_attribute a
                              WHERE a.attrelid = fullname::regclass AND a.attname = rec.attname);


            SELECT string_agg(
                   CASE
                       WHEN i.indisprimary THEN 'PK: ' || c.relname
                       WHEN i.indisunique THEN 'UNIQUE: ' || c.relname
                       ELSE 'INDEX: ' || c.relname
                   END, ', ') INTO index_names
            FROM pg_index i
            JOIN pg_class c ON c.oid = i.indexrelid
            JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey)
            WHERE i.indrelid = fullname::regclass
              AND a.attname = rec.attname;




            counter := counter + 1;
            RAISE NOTICE '% % Type: %',
                lpad(counter::text, 3) || '.',
                rpad(rec.attname, 30),
                rpad(rec.data_type, 30);
            RAISE NOTICE '                                    Commen: %',
                CASE WHEN col_comment IS NOT NULL THEN col_comment ELSE 'нет комментария' END;
            RAISE NOTICE '                                    Index: %',
                CASE WHEN index_names IS NOT NULL THEN index_names ELSE '' END;
            RAISE NOTICE '';
        END LOOP;
    ELSE
        RAISE NOTICE 'Таблица % не существует или у вас нет к ней доступа', fullname;
    END IF;
END;
$$;
