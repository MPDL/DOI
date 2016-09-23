CREATE TABLE users (username character varying(255) NOT NULL, email character varying(255), password char(60) NOT NULL, prefix character varying(255) NOT NULL, CONSTRAINT users_pkey PRIMARY KEY (username));

CREATE TABLE roles (username character varying(255) NOT NULL, role character varying(255) NOT NULL, CONSTRAINT roles_pkey PRIMARY KEY (username, role), CONSTRAINT fk_roles_username FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE unique_identifier (id serial NOT NULL PRIMARY KEY, value bigint);