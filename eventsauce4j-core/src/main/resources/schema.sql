-- DROP TABLE event_publication;

CREATE TABLE IF NOT EXISTS event_publication (
	id uuid NOT NULL,
	completion_attempts int4 NOT NULL,
	completion_date timestamptz(6) NULL,
	consumed_at timestamptz(6) NULL,
	last_resubmission_date timestamptz(6) NULL,
	meta_data varchar(255) NULL,
	publication_date timestamptz(6) NULL,
	serialized_event varchar(255) NULL,
	status varchar(255) NULL,
	routing_key varchar(255) NULL,
	CONSTRAINT event_publication_pkey PRIMARY KEY (id),
	CONSTRAINT event_publication_status_check CHECK (((status)::text = ANY ((ARRAY['PUBLISHED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying])::text[])))
);


-- DROP TABLE event_publication_dlq;

CREATE TABLE IF NOT EXISTS event_publication_dlq (
	id uuid NOT NULL,
	completion_attempts int4 NOT NULL,
	completion_date timestamptz(6) NULL,
	consumed_at timestamptz(6) NULL,
	headers varchar(255) NULL,
	last_resubmission_date timestamptz(6) NULL,
	publication_date timestamptz(6) NULL,
	serialized_event varchar(255) NULL,
	status varchar(255) NULL,
	meta_data varchar(255) NULL,
	CONSTRAINT event_publication_dlq_pkey PRIMARY KEY (id),
	CONSTRAINT event_publication_dlq_status_check CHECK (((status)::text = ANY ((ARRAY['PUBLISHED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying])::text[])))
);

-- DROP TABLE outbox_lock;

CREATE TABLE IF NOT EXISTS outbox_lock (
	lock_name varchar(255) NOT NULL,
	lock_at timestamptz(6) NULL,
	CONSTRAINT outbox_lock_pkey PRIMARY KEY (lock_name)
);