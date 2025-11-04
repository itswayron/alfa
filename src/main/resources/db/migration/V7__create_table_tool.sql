CREATE TABLE tool (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    maximum_usages INT NOT NULL,
    actual_usages INT NOT NULL DEFAULT 0,
    subgroup_id INT NOT NULL REFERENCES subgroup(id),
    is_loan BOOLEAN NOT NULL
);
