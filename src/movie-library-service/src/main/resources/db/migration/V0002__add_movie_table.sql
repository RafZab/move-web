CREATE TABLE movies
(
    movie_id           SERIAL PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    title              VARCHAR(255) NOT NULL,
    director           VARCHAR(255) NOT NULL,
    year_of_production INT          NOT NULL,
    ranking            INT          NOT NULL,
    size               BIGINT       NOT NULL,
    file_path          VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE
);