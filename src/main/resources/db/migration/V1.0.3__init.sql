ALTER TABLE subject
    ADD CONSTRAINT constraint_subject_name UNIQUE (username),
    ADD CONSTRAINT constraint_subject_email UNIQUE (email);

ALTER TABLE user_group
    ADD CONSTRAINT constraint_user_group_name UNIQUE (name);
