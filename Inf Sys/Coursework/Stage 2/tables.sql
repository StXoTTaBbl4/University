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

CREATE TABLE "AssignedRoles" (
  "employeeID" INTEGER NOT NULL,
  "role" Roles NOT NULL
);

CREATE TABLE "Employee" (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "email" VARCHAR NOT NULL,
  "password" VARCHAR NOT NULL
);

CREATE TABLE "TasksDescription" (
  "id" SERIAL PRIMARY KEY,
  "task_description" VARCHAR(256) NOT NULL
);

CREATE TABLE "TasksAssignment" (
  "taskID" INTEGER NOT NULL,
  "employeeID" INTEGER NOT NULL
);

CREATE TABLE "Levels" (
  "id" SERIAL PRIMARY KEY,
  "weight" INTEGER UNIQUE NOT NULL,
  "level" VARCHAR(32) UNIQUE,
  "description" VARCHAR(128)
);

CREATE TABLE "Products" (
  "id" SERIAL PRIMARY KEY,
  "product" TEXT UNIQUE NOT NULL,
  "block_type" BlockTypes NOT NULL
);

CREATE TABLE "Tasks" (
  "id" SERIAL PRIMARY KEY,
  "productID" INTEGER NOT NULL,
  "task" VARCHAR(64) NOT NULL
);

CREATE TABLE "EmployeeSkills" (
  "id" SERIAL PRIMARY KEY,
  "employeeID" INTEGER NOT NULL,
  "taskID" INTEGER NOT NULL,
  "levelID" INTEGER NOT NULL,
  "creationTime" TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE "CertificateCategory" (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR(256) NOT NULL
);

CREATE TABLE "CertifictateSubCategory" (
  "id" SERIAL PRIMARY KEY,
  "categoryID" INTEGER NOT NULL,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "Certificates" (
  "id" SERIAL PRIMARY KEY,
  "employeeID" INTEGER NOT NULL,
  "subcategoryID" INTEGER NOT NULL
);

ALTER TABLE "AssignedRoles" ADD FOREIGN KEY ("employeeID") REFERENCES "Employee" ("id") ON DELETE CASCADE;

ALTER TABLE "TasksAssignment" ADD FOREIGN KEY ("taskID") REFERENCES "TasksDescription" ("id") ON DELETE CASCADE;

ALTER TABLE "TasksAssignment" ADD FOREIGN KEY ("employeeID") REFERENCES "Employee" ("id") ON DELETE CASCADE;

ALTER TABLE "Tasks" ADD FOREIGN KEY ("productID") REFERENCES "Products" ("id") ON DELETE CASCADE;

ALTER TABLE "EmployeeSkills" ADD FOREIGN KEY ("employeeID") REFERENCES "Employee" ("id") ON DELETE CASCADE;

ALTER TABLE "EmployeeSkills" ADD FOREIGN KEY ("taskID") REFERENCES "Tasks" ("id") ON DELETE NO ACTION;

ALTER TABLE "EmployeeSkills" ADD FOREIGN KEY ("levelID") REFERENCES "Levels" ("id") ON DELETE NO ACTION;

ALTER TABLE "CertifictateSubCategory" ADD FOREIGN KEY ("categoryID") REFERENCES "CertificateCategory" ("id") ON DELETE CASCADE;

ALTER TABLE "Certificates" ADD FOREIGN KEY ("employeeID") REFERENCES "Employee" ("id") ON DELETE CASCADE;

ALTER TABLE "Certificates" ADD FOREIGN KEY ("subcategoryID") REFERENCES "CertifictateSubCategory" ("id") ON DELETE NO ACTION;
