CREATE TABLE active_story_part (
    id      BOOLEAN PRIMARY KEY DEFAULT TRUE,
    part    INT NOT NULL,
    CONSTRAINT single_row CHECK (id = TRUE)
);

INSERT INTO active_story_part (part) VALUES (0);