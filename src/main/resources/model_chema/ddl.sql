CREATE TABLE cases (
	id serial NOT NULL,
	country_region varchar(100) NOT NULL,
	last_update timestamp NOT NULL,
	confirmed int4 NOT NULL,
	deaths int4 NOT NULL,
	recovered int4 NOT NULL,
	CONSTRAINT cases_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_confirmed_deaths_recovered ON cases USING btree (confirmed, deaths, recovered);
CREATE INDEX lower_province_state_country_region ON cases USING btree (lower((country_region)::text));
CREATE INDEX time_last_update_last_update ON cases USING btree (last_update, source_update);

CREATE TABLE case_types (
	id serial NOT NULL,
    name varchar(100) NOT NULL,
	CONSTRAINT case_types_pkey PRIMARY KEY (id)
);