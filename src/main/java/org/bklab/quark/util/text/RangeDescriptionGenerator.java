package org.bklab.quark.util.text;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class RangeDescriptionGenerator<T> {

    private final String title;
    private final Function<T, String> captionGenerator;

    public RangeDescriptionGenerator(String title) {
        this(title, Objects::toString);
    }

    public RangeDescriptionGenerator(String title, Function<T, String> captionGenerator) {
        this.title = title;
        this.captionGenerator = captionGenerator;
    }

    public String create(T start, T end) {
        if (start != null && end != null) {
            return title + "：" + captionGenerator.apply(start) + " - " + captionGenerator.apply(end);
        }

        if (start != null) {
            return title + "：≥ " + captionGenerator.apply(start);
        }

        if (end != null) {
            return title + "：≤ " + captionGenerator.apply(end);
        }

        return null;
    }

    public void create(T start, T end, Consumer<String> hasDescription, Consumer<Object> noDescription) {
        String s = create(start, end);
        if (s != null) hasDescription.accept(s);
        else noDescription.accept(null);
    }
}
