CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL,
    name  VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description VARCHAR(200),
     release_date DATE,
     duration INTEGER NOT NULL,
     mpa_id BIGINT
);

CREATE TABLE IF NOT EXISTS friendsLike (
    userId BIGINT NOT NULL,
    friendId BIGINT NOT NULL,

    PRIMARY KEY (userId, friendId),

    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (friendId) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS filmsLike(
    filmId BIGINT NOT NULL,
    userId BIGINT NOT NULL,

    PRIMARY KEY (filmId, userId),

    FOREIGN KEY (filmId) REFERENCES films(id),
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS filmsGenre(
    filmId BIGINT NOT NULL,
    genreId BIGINT NOT NULL,

    PRIMARY KEY (filmId, genreId),

    FOREIGN KEY (filmId) REFERENCES films(id)
);