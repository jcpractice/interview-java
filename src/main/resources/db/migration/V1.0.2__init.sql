--subject table
ALTER TABLE subject
    ADD creation_date TIMESTAMP WITH TIME ZONE DEFAULT(CURRENT_TIMESTAMP);
ALTER TABLE subject
    ADD profile_type varchar(2) DEFAULT('R');
ALTER TABLE subject
    ADD status int DEFAULT(1);
