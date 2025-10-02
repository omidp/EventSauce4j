-- DROP TABLE event_publication;

CREATE TABLE IF NOT EXISTS event_publication (
	id uuid NOT NULL,
	completion_attempts int4 NOT NULL,
	completion_date timestamptz(6) NULL,
	event_type varchar(255) NULL,
	last_resubmission_date timestamptz(6) NULL,
	listener_id varchar(255) NULL,
	meta_data varchar(255) NULL,
	publication_date timestamptz(6) NULL,
	serialized_event varchar(255) NULL,
	status varchar(255) NULL,
	consumed_at timestamptz(6) NULL,
	CONSTRAINT event_publication_pkey PRIMARY KEY (id),
	CONSTRAINT event_publication_status_check CHECK (((status >= 0) AND (status <= 3)))
);


-- DROP TABLE event_publication_dlq;

CREATE TABLE IF NOT EXISTS event_publication_dlq (
	id uuid NOT NULL,
	completion_attempts int4 NOT NULL,
	completion_date timestamptz(6) NULL,
	event_type varchar(255) NULL,
	headers varchar(255) NULL,
	last_resubmission_date timestamptz(6) NULL,
	listener_id varchar(255) NULL,
	publication_date timestamptz(6) NULL,
	serialized_event varchar(255) NULL,
	status varchar(255) NULL,
	consumed_at timestamptz(6) NULL,
	CONSTRAINT event_publication_dlq_pkey PRIMARY KEY (id),
	CONSTRAINT event_publication_dlq_status_check CHECK (((status >= 0) AND (status <= 3)))
);

-- DROP TABLE outbox_lock;

CREATE TABLE outbox_lock (
	lock_name varchar(255) NOT NULL,
	lock_at timestamptz NOT NULL,
	CONSTRAINT outbox_lock_pk PRIMARY KEY (lock_name)
);