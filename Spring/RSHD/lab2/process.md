# Вариант 695238
виртуалка pg118, пользователь postgres3, BV7ArGsA

# Команды этапов

## Этап 0: Подключение

ssh -J s262512@helios.cs.ifmo.ru:2222 postgres3@pg118

## Этап 1: Инициализация кластера

mkdir -p $HOME/lyg29

export PGDATA=$HOME/lyg29  
export PGENCODING=ISO_8859_5  
export LC_ALL=ru_RU.ISO8859-5  
export LC_CTYPE=ru_RU.ISO8859-5 - сортировка		|  
export LC_COLLATE=ru_RU.ISO8859-5 - классификация	| Нужно указывать если для этих задач нужны другие локали, отлисные от той что в LC_ALL  

initdb

grep -E "lc_messages|lc_monetary|lc_numeric|lc_time|server_encoding" $PGDATA/postgresql.conf для проверки локалей


## Этап 2: Конфигурация и запуск сервера БД

### postgres.conf

port = 9238  
listen_addresses = 'localhost'  
unix_socket_directories = '/tmp'  
max_connections = 1000 - 300*3+100                
shared_buffers = 1GB                   
temp_buffers = 64MB  
work_mem = 8MB  
checkpoint_timeout = 15min  
effective_cache_size = 2GB           
fsync = on  
commit_delay = 100 (ms)  

log_destination = 'csvlog'  
logging_collector = on  
log_directory = 'log'  
log_filename = 'postgresql-%a.csv' - или дефолтное, оно тоже норм  
log_statement = 'none'  
log_duration = on  
log_connections = on  
log_disconnections = on  
log_min_messages = info  

pg_ctl -D "$PGDATA" -l logfile start - запуск  
pg_isready -p 9238 - проверка  
pg_ctl -D $PGDATA stop - остановка  

### pg_hba.conf

#### Peer-аутентификация по Unix-сокету
local   all             all                                     peer

#### TCP/IP только localhost, с паролем в открытом виде
host    all             all             127.0.0.1/32            password

psql -p 9238 -U postgres3 -h /tmp -d easyblackcity или postgres - peer  
psql -p 9238 -U postgres3 -h 127.0.0.1 -d postgres - TCP/IP -> сначала нужно сделать ALTER ROLE postgres3 WITH PASSWORD 'BV7ArGsA';


## Этап 3: Дополнительные табличные пространства и наполнение базы

mkdir -p $HOME/zlb45 $HOME/hvn59
chmod 700 $HOME/zlb45 $HOME/hvn59

CREATE TABLESPACE temp_space1 LOCATION '/var/db/postgres3/zlb45'; CREATE TABLESPACE temp_space2 LOCATION '/var/db/postgres3/hvn59';

`SELECT * FROM pg_tablespace;` - для проверки

ALTER DATABASE easyblackcity SET temp_tablespaces = temp_space1,temp_space2; - для всех ролей этой БД

`SHOW temp_tablespaces;` - для проверки пространств для временных объектов


psql -p 9238 -U postgres3 -d postgres <<EOF
CREATE DATABASE easyblackcity TEMPLATE template0 TABLESPACE pg_default;
EOF

psql потом \c easyblackcity для проверки что БД действительно создана

CREATE ROLE blackuser LOGIN PASSWORD 'securepass';
GRANT CONNECT ON DATABASE easyblackcity TO blackuser;
GRANT CREATE ON DATABASE easyblackcity TO blackuser;
GRANT USAGE,CREATE ON SCHEMA public TO blackuser;
GRANT TEMPORARY ON DATABASE easyblackcity TO blackuser;
GRANT CREATE ON TABLESPACE temp_space1 TO blackuser;
GRANT CREATE ON TABLESPACE temp_space2 TO blackuser;\q

`SELECT * from pg_roles where rolname='blackuser';`

psql -h 127.0.0.1 -p 9238 -U blackuser -d easyblackcity + securepass

CREATE TABLE t_default (id serial, data text);  
CREATE TABLE t_temp1 (id serial, data text) TABLESPACE temp_space1;  
CREATE TABLE t_temp2 (id serial, data text) TABLESPACE temp_space2;  

INSERT INTO t_default SELECT generate_series(1, 1000), md5(random()::text);  
INSERT INTO t_temp1 SELECT generate_series(1, 1000), md5(random()::text);  
INSERT INTO t_temp2 SELECT generate_series(1, 1000), md5(random()::text);  

SELECT * FROM t_temp1;
SELECT * FROM t_temp2; - эти там останутся 

CREATE TEMP TABLE tmp_test(id int); - после переподключения таблицы там не будет


Все табличные пространства:  `SELECT * FROM pg_tablespace;`BV

SELECT n.nspname AS schema, c.relname AS object, COALESCE(t.spcname, 'pg_default') AS tablespace FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace LEFT JOIN pg_tablespace t ON c.reltablespace = t.oid WHERE c.relkind IN ('r', 'i', 't');
