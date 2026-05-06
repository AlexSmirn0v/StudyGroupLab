CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS study_groups_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS admin_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS coord_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS semester_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS color_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER DEFAULT nextval('users_id_seq') PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password_hash BYTEA NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS coordinates (
    id INTEGER DEFAULT nextval('coord_id_seq') PRIMARY KEY,
    x BIGINT NOT NULL,
    y BIGINT
);

CREATE TABLE IF NOT EXISTS semesters (
    id INTEGER DEFAULT nextval('semester_id_seq') PRIMARY KEY,
    val VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS colors (
    id INTEGER DEFAULT nextval('color_id_seq') PRIMARY KEY,
    val VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS admins (
    id INTEGER DEFAULT nextval('admin_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL CHECK (trim(name) <> ''),
    height INTEGER NOT NULL CHECK (height > 0),
    passportID VARCHAR(20) NOT NULL,
    hair_color_id INTEGER REFERENCES colors(id)
);

CREATE TABLE IF NOT EXISTS study_groups (
    id INTEGER DEFAULT nextval('study_groups_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    coordinates_id INTEGER NOT NULL REFERENCES coordinates(id),
    creation_date TIMESTAMP NOT NULL,
    students_count BIGINT CHECK (students_count > 0),
    transferred_students INTEGER NOT NULL CHECK (transferred_students > 0),
    average_mark INTEGER NOT NULL CHECK (average_mark > 0),
    semester_id INTEGER REFERENCES semesters(id),
    group_admin_id INTEGER REFERENCES admins(id),
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_study_groups_owner ON study_groups(owner_id);
CREATE INDEX IF NOT EXISTS idx_users_login ON users(login);