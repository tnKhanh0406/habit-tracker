ALTER TABLE users ALTER COLUMN id DROP DEFAULT;

ALTER TABLE users
    ADD COLUMN system_role VARCHAR(20) NOT NULL DEFAULT 'USER',
    ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
    ADD CONSTRAINT ck_users_system_role
        CHECK (system_role IN ('USER', 'ADMIN'));

ALTER TABLE users
    ADD CONSTRAINT ck_users_status
        CHECK (
            status IN (
                   'ACTIVE',
                   'DISABLED',
                   'LOCKED',
                   'PENDING_VERIFICATION'
                )
            );

CREATE UNIQUE INDEX ux_users_email_lower
    ON users (LOWER(email));

CREATE UNIQUE INDEX ux_users_username_lower
    ON users (LOWER(username));