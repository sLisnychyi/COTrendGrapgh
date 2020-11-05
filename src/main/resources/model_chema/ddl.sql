CREATE TABLE public.covid19 (
	id serial NOT NULL,
	country_region varchar(100) NOT NULL,
	last_update timestamp NOT NULL,
	confirmed int4 NOT NULL,
	deaths int4 NOT NULL,
	recovered int4 NOT NULL,
	source_update timestamp NOT NULL,
	CONSTRAINT covid19_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_confirmed_deaths_recovered ON public.covid19 USING btree (confirmed, deaths, recovered);
CREATE INDEX lower_province_state_country_region ON public.covid19 USING btree (lower((country_region)::text));
CREATE INDEX time_last_update_last_update ON public.covid19 USING btree (last_update, source_update);

CREATE OR REPLACE FUNCTION public.source_update()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
    BEGIN
       NEW.source_update := current_timestamp;
       RETURN NEW;
    END;
$function$;


CREATE TRIGGER inset_row_to_source_update BEFORE
insert on public.covid19
	FOR EACH row
    EXECUTE PROCEDURE source_update();