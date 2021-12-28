CREATE TABLE card_lists
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100)     NOT NULL,
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(255)     NOT NULL,
    archived     BOOLEAN          NOT NULL,
    board_id     UUID UNIQUE,
    FOREIGN KEY (board_id) REFERENCES boards (id)
);