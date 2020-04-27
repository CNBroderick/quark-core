package org.bklab.quark.entity;

import java.io.Serializable;

public class Entity implements Serializable {

    private long entityInstanceId;

    private final String name;

    public Entity(String name) {
        this.name = name;
    }
}
