/*
 * Class: org.bklab.util.search.Searcher
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.search;

import java.util.StringJoiner;

public class Searcher<T> {
    private String name;
    private String description;
    private IKeywordSearcher<T> searcher;

    public Searcher(String name, String description, IKeywordSearcher<T> searcher) {
        this.name = name;
        this.description = description;
        this.searcher = searcher;
    }

    public String getName() {
        return name;
    }

    public Searcher<T> setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Searcher<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    public IKeywordSearcher<T> getSearcher() {
        return searcher;
    }

    public Searcher<T> setSearcher(IKeywordSearcher<T> searcher) {
        this.searcher = searcher;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Searcher.class.getSimpleName() + "{", "\n}")
                .add("\n\tname: '" + name + "'")
                .add("\n\tdescription: '" + description + "'")
                .add("\n\tsearcher: " + searcher)
                .toString();
    }
}
