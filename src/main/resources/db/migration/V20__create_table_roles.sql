CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name)
SELECT DISTINCT "role"
FROM users;

CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission VARCHAR(100) NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

ALTER TABLE users ADD COLUMN role_id INT;

UPDATE users u SET role_id = r.id FROM roles r WHERE u."role" = r.name;

ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

ALTER TABLE users ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT;

ALTER TABLE users DROP COLUMN "role";

CREATE INDEX idx_users_role_id ON users(role_id);
