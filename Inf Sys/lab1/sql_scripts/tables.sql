CREATE TYPE vehicle_type AS ENUM (
     'CAR', 'PLANE', 'BOAT', 'BICYCLE', 'MOTORCYCLE';
);

CREATE TYPE fuel_type AS ENUM (
    'ELECTRICITY', 'DIESEL', 'ALCOHOL', 'MANPOWER', 'NUCLEAR'
);

CREATE TABLE coordinates (
    id SERIAL PRIMARY KEY,
	x BIGINT NOT NULL CHECK (x > -694),  -- поле x с ограничением
    y BIGINT NOT NULL    
);

CREATE TABLE vehicle (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    coordinates_id BIGINT,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vehicle_type vehicle_type,
    engine_power DOUBLE PRECISION NOT NULL CHECK (engine_power > 0),
    number_of_wheels BIGINT CHECK (number_of_wheels > 0),
    capacity BIGINT CHECK (capacity > 0),
    distance_travelled BIGINT NOT NULL CHECK (distance_travelled > 0),
    fuel_consumption BIGINT CHECK (fuel_consumption > 0),
    fuel_type fuel_type NOT NULL,
    FOREIGN KEY (coordinates_id) REFERENCES coordinates(id) ON DELETE CASCADE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL
);

CREATE TABLE vehicle_interactions (
    id SERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    modifier_id BIGINT DEFAULT NULL,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
	FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
	FOREIGN KEY (modifier_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE coordinates_interactions (
    id SERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    coordinate_id BIGINT NOT NULL,
    modifier_id BIGINT DEFAULT NULL,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coordinate_id) REFERENCES coordinates(id) ON DELETE CASCADE,
	FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
	FOREIGN KEY (modifier_id) REFERENCES users(id) ON DELETE CASCADE
);

	
	