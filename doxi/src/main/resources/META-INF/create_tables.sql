CREATE TABLE users (username character varying(255) NOT NULL, email character varying(255), password char(60) NOT NULL, prefix character varying(255) NOT NULL, CONSTRAINT users_pkey PRIMARY KEY (username));

CREATE TABLE roles (username character varying(255) NOT NULL, role character varying(255) NOT NULL, CONSTRAINT roles_pkey PRIMARY KEY (username, role), CONSTRAINT fk_roles_username FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE unique_identifier (id serial NOT NULL PRIMARY KEY, value bigint);

-- Table: pid_cache
-- DROP TABLE pid_cache;

CREATE TABLE pid_cache
(
  identifier character varying NOT NULL,
  created timestamp without time zone NOT NULL,
  CONSTRAINT pid_cache_pk PRIMARY KEY (identifier)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pid_cache
  OWNER TO postgres;

-- Index: pid_cache_created_idx
-- DROP INDEX pid_cache_created_idx;

CREATE INDEX pid_cache_created_idx
  ON pid_cache
  USING btree
  (created);

-- Table: pid_queue
-- DROP TABLE pid_queue;

CREATE TABLE pid_queue
(
  identifier character varying NOT NULL,
  url character varying NOT NULL,
  created timestamp without time zone NOT NULL,
  CONSTRAINT pid_queue_pk PRIMARY KEY (identifier)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pid_queue
  OWNER TO postgres;

-- Index: pid_queue_created_idx
-- DROP INDEX pid_queue_created_idx;

CREATE INDEX pid_queue_created_idx
  ON pid_queue
  USING btree
  (created);

-- Index: pid_queue_url_idx
-- DROP INDEX pid_queue_url_idx;

CREATE INDEX pid_queue_url_idx
  ON pid_queue
  USING btree
  (url COLLATE pg_catalog."default");

