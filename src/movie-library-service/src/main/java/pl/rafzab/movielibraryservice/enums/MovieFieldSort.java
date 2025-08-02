package pl.rafzab.movielibraryservice.enums;

import lombok.Getter;

@Getter
public enum MovieFieldSort {
    RANKING(new String[]{"ranking"}),
    SIZE(new String[]{"size"}),
    ALL(new String[]{"ranking", "size"});

    private final String[] values;

    MovieFieldSort(String[] values) {
        this.values = values;
    }
}

