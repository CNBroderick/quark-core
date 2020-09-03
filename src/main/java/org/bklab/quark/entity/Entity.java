package org.bklab.quark.entity;

import org.bklab.quark.entity.dao.EntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

public class Entity implements Serializable {

    public final static String ENTITY_INSTANCE_ID = "entityInstanceId";

    private final EntitySchema schema;
    private final String name;
    private long entityInstanceId;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Entity(EntitySchema schema) {
        this.schema = schema;
        this.name = schema.getName();
        this.entityInstanceId = schema.getEntityInstanceId();
    }

    public long getEntityInstanceId() {
        return entityInstanceId;
    }

    public Entity setEntityInstanceId(long entityInstanceId) {
        this.entityInstanceId = entityInstanceId;
        this.schema.setValue(ENTITY_INSTANCE_ID, entityInstanceId);
        return this;
    }

    public EntityDao dao() {
        return new EntityDao(this);
    }

    public EntitySchema getSchema() {
        return schema;
    }

    public Collection<EntityProperty<?>> properties() {
        return schema.getProperties().values();
    }

    public <T> Entity value(String name, T value) {
        schema.setValue(name, value);
        return this;
    }

    public <T> EntityProperty<T> getProperty(String name) {
        return schema.get(name);
    }

    public <T> T getValue(String name) {
        return schema.getValue(name);
    }

    public String getName() {
        return name;
    }
}
