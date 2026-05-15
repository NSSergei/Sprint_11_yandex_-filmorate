package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Getter
public enum MpaRating {
    G(1,"G"),
    PG(2,"PG"),
    PG_13(3,"PG_13"),
    R(4,"R"),
    NC_17(5,"NC_17"),
    Empty(6, "Empty");

    private final long id;
    private final String name;

    MpaRating(long id, String name) {
        this.id = id;
        this.name = name;
        }

    public static MpaRating fromId(long id) {
        for (MpaRating r : MpaRating.values()) {
            if (r.getId() == id) {
                return  r;
            }
        }
        throw new NotFoundException("Рейтинг не найден");
    }
}