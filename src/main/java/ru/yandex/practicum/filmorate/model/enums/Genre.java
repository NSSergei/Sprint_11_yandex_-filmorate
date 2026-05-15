package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final long id;
    private final String name;

    Genre(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Genre fromId(long id) {
        for (Genre genre : Genre.values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        throw new NotFoundException("Жанр не найден");
    }

    @JsonCreator
    public static Genre fromJson(JsonNode node) {
        if (node.has("id")) {
            return fromId(node.get("id").asLong());
        }
        return fromId(node.asLong());
    }
}
