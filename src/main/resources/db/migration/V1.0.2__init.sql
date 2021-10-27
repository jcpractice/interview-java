--subject table
ALTER TABLE subject
    ADD creation_date TIMESTAMP WITH TIME ZONE DEFAULT(CURRENT_TIMESTAMP);
ALTER TABLE subject
    ADD profile_type varchar(2) DEFAULT('R');
ALTER TABLE subject
    ADD status int DEFAULT(1);

-- Default user group
INSERT INTO user_group (id, name) VALUES (1, 'Default Group');

