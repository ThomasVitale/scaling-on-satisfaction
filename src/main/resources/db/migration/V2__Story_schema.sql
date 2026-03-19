CREATE TABLE story_fragment (
    id      UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    theme   VARCHAR(50)  NOT NULL,
    part    INTEGER      NOT NULL,
    style   VARCHAR(50)  NOT NULL,
    model   VARCHAR(50)  NOT NULL,
    content TEXT         NOT NULL
);
