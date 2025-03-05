CREATE TYPE "BlockTypes" AS ENUM (
  'Hardware',
  'Software',
  'Processes'
);

CREATE TYPE "Roles" AS ENUM (
  'EMPLOYEE',
  'LEADER',
  'ADMINISTRATOR'
);

CREATE TABLE "assigned_roles" (
  employee_id INTEGER NOT NULL,
  role "Roles" NOT NULL
);

CREATE TABLE "refresh_token" (
    "employee_id" INTEGER UNIQUE NOT NULL,
    "token" varchar(512) NOT NULL,
    "expires_at" TIMESTAMP NOT NULL,
    "revoked" BOOLEAN DEFAULT FALSE,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE "refresh_token" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE CASCADE;

CREATE TABLE "employee" (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "email" VARCHAR UNIQUE NOT NULL,
  "password" VARCHAR NOT NULL
);

CREATE TABLE "tasks_description" (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR(256) NOT NULL,
  "description" VARCHAR(256) NOT NULL
);

CREATE TABLE "tasks_assignment" (
  "task_id" INTEGER NOT NULL,
  "employee_id" INTEGER NOT NULL
);

CREATE TABLE "levels" (
    "id" SERIAL PRIMARY KEY,
    "weight" INTEGER UNIQUE NOT NULL,
    "level" VARCHAR(32) UNIQUE,
    "description" VARCHAR(128)
);

CREATE TABLE "products" (
    "id" SERIAL PRIMARY KEY,
    "product" TEXT UNIQUE NOT NULL,
    "block_type" "BlockTypes" NOT NULL
);

CREATE TABLE "tasks" (
    "id" SERIAL PRIMARY KEY,
    "product_id" INTEGER NOT NULL,
    "task" VARCHAR(64) NOT NULL
);

CREATE TABLE "employee_skills" (
    "id" SERIAL PRIMARY KEY,
    "employee_id" INTEGER NOT NULL,
    "task_id" INTEGER ,
    "level_id" INTEGER NOT NULL,
    "creation_time" TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE "certificate_category" (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR(256) NOT NULL
);

CREATE TABLE "certificate_sub_category" (
  "id" SERIAL PRIMARY KEY,
  "category_id" INTEGER NOT NULL,
  "name" VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE "certificates" (
  "id" SERIAL PRIMARY KEY,
  "name" TEXT UNIQUE NoT NULL,
  "employee_id" INTEGER NOT NULL,
  "subcategory_id" INTEGER NOT NULL,
  "file_path" varchar(256) UNIQUE NOT NULL
);

ALTER TABLE "tasks" ADD FOREIGN KEY ("product_id") REFERENCES "products" ("id") ON DELETE CASCADE;

ALTER TABLE "employee_skills" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE CASCADE;

ALTER TABLE "employee_skills" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id") ON DELETE NO ACTION;

ALTER TABLE "employee_skills" ADD FOREIGN KEY ("level_id") REFERENCES "levels" ("id") ON DELETE NO ACTION;

ALTER TABLE "assigned_roles" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE CASCADE;

ALTER TABLE "tasks_assignment" ADD FOREIGN KEY ("task_id") REFERENCES "tasks_description" ("id") ON DELETE CASCADE;

ALTER TABLE "tasks_assignment" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE CASCADE;

ALTER TABLE "certificate_sub_category" ADD FOREIGN KEY ("category_id") REFERENCES "certificate_category" ("id") ON DELETE CASCADE;

ALTER TABLE "certificates" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE CASCADE;

ALTER TABLE "certificates" ADD FOREIGN KEY ("subcategory_id") REFERENCES "certificate_sub_category" ("id") ON DELETE NO ACTION;

insert into "levels" (weight, level, description)
values
    (0, 'None', 'Have no knowledge on product, technology or process'),
    (1, 'Basic', 'Have general knowledge on product, technology or process; can follow prepared instructions'),
    (2, 'Middle', 'Have experience with product, technology or process; can find instructions and follow them'),
    (3, 'Professional', 'Navigate freely in product, technology or process; can provide instructions and advise to customer'),
    (4, 'Expert', 'Know all quirks of product, technology or process; can provide instructions, advise to customer and deliver trainings');
	
insert into "products" (product, block_type)
values
    ('ProLiant and Apollo','Hardware'),
    ('BladeSystem','Hardware'),
    ('Synergy','Hardware'),
    ('SimpliVity','Hardware'),
    ('B-series SAN','Hardware'),
    ('Aruba Switches','Hardware'),
    ('Aruba Routers','Hardware');
	
insert into "products" (product, block_type)
values
    ('Microsoft Windows Server','Software'),
    ('Red Hat Enterprise Linux','Software'),
    ('SUSE Linux Enterprise Server','Software'),
    ('VMware vSphere','Software'),
    ('Microsoft Hyper-V','Software'),
    ('Red Hat Enterprise Virtualization','Software');
	
insert into "products" (product, block_type)
values
    ('PRINCE2 Project Management','Processes'),
    ('PMI Project Management','Processes'),
    ('TOGAF Enterprise Architecture','Processes'),
    ('Technical Writing','Processes'),
    ('Negotiations with Customer','Processes'),
    ('Correspondence with Customer','Processes');
	
DO $$
    DECLARE
        product_ids INTEGER[] := ARRAY[1, 2, 3, 4, 5, 6, 7];
        task TEXT;
    BEGIN
        FOREACH task IN ARRAY ARRAY['Part Replacement', 'Rack Mounting', 'Cabling', 'Startup', 'Basic Configuration', 'Log Collection']
            LOOP
                INSERT INTO tasks ("product_id", task)
                SELECT unnest(product_ids), task;
            END LOOP;
    END $$;
	
DO $$
    DECLARE
        product_ids INTEGER[] := ARRAY[8,9,10,11,12,13]; 
        task TEXT;
    BEGIN
        FOREACH task IN ARRAY ARRAY['Installation', 'Basic Configuration', 'Fine Tuning', 'Performance Collection', 'Performance Tuning']
            LOOP
                INSERT INTO tasks ("product_id", task)
                SELECT unnest(product_ids), task;
            END LOOP;
    END $$;
	
DO $$
    DECLARE
        product_ids INTEGER[] := ARRAY[14,15,16,17,18,19];  -- Замените на нужные ID
        task TEXT;
    BEGIN
        FOREACH task IN ARRAY ARRAY['Level']
            LOOP
                INSERT INTO tasks ("product_id", task)
                SELECT unnest(product_ids), task;
            END LOOP;
    END $$;