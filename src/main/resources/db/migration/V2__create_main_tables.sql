CREATE TABLE measurement_unity (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movement_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movement_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE sector (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE subgroup (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE "group" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE business_partner (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    cnpj VARCHAR(20) UNIQUE NOT NULL,
    relation VARCHAR(50) NOT NULL
);

CREATE TABLE position (
    id SERIAL PRIMARY KEY,
    floor VARCHAR(50),
    side VARCHAR(50),
    "column" VARCHAR(50),
    box VARCHAR(50)
);

CREATE TYPE role_enum AS ENUM ('USER', 'ADMIN');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username_field VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role role_enum DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sector_id INT NOT NULL REFERENCES sector(id)
);

CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    group_id INT NOT NULL REFERENCES "group"(id),
    subgroup_id INT NOT NULL REFERENCES subgroup(id),
    dimensions VARCHAR(100),
    material VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    image_path VARCHAR(255),
    measurement_unity_id INT NOT NULL REFERENCES measurement_unity(id),
    main_supplier_id INT REFERENCES business_partner(id)
);

CREATE TABLE production_order (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    document VARCHAR(255),
    date TIMESTAMP NOT NULL,
    business_partner_id INT NOT NULL REFERENCES business_partner(id)
);

CREATE TABLE stock (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL REFERENCES item(id),
    current_amount DOUBLE PRECISION NOT NULL,
    minimum_amount DOUBLE PRECISION,
    maximum_amount DOUBLE PRECISION,
    current_value_in_money DOUBLE PRECISION NOT NULL,
    expired_date TIMESTAMP,
    average_price DOUBLE PRECISION NOT NULL,
    sector_id INT NOT NULL REFERENCES sector(id),
    position_id INT NOT NULL REFERENCES position(id)
);

CREATE TABLE movement (
    id SERIAL PRIMARY KEY,
    stock_id INT NOT NULL REFERENCES stock(id),
    production_order_id INT REFERENCES production_order(id),
    quantity DOUBLE PRECISION NOT NULL,
    price DOUBLE PRECISION,
    type_id INT NOT NULL REFERENCES movement_type(id),
    date TIMESTAMP NOT NULL DEFAULT NOW(),
    employee_id INT NOT NULL REFERENCES employee(id),
    observation TEXT,
    status_id INT NOT NULL REFERENCES movement_status(id),
    sector_id INT NOT NULL REFERENCES sector(id)
);
