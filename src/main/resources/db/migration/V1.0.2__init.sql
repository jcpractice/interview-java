--subject table
ALTER TABLE subject
    ADD creation_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE subject
    ADD profileType CHAR(2) DEFAULT 'R';
ALTER TABLE subject
    ADD status int DEFAULT 1;
