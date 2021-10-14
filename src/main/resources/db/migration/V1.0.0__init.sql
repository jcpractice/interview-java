-- SUBJECT (USER)
CREATE TABLE IF NOT EXISTS subject
(
    id
    bigserial
    primary
    key,
    username
    varchar
(
    200
) not null,
    email
    varchar (500)not null,
    password varchar
(
    200
) not null
    );

CREATE INDEX IF NOT EXISTS ix_subject_id on subject (id);
CREATE INDEX IF NOT EXISTS ix_subject_username on subject (username);

-- USER GROUP
CREATE TABLE IF NOT EXISTS user_group
(
    id
    bigserial
    primary
    key,
    name
    varchar
(
    200
) not null
    );

CREATE INDEX IF NOT EXISTS ix_user_group_id on user_group (id);

-- USER GROUP
CREATE TABLE subject_user_group
(
    subject_id    bigint not null,
    user_group_id bigint not null,
    constraint subject_user_group_pkey primary key (subject_id, user_group_id),
    constraint fk_subject foreign key (subject_id) references subject (id),
    constraint fk_user_group foreign key (user_group_id) references user_group (id)
);
