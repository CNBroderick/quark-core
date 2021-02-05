package org.bklab.quark.util.schema;

import dataq.core.data.schema.Schema;

import java.lang.reflect.Field;

public class GeneratedRecord extends dataq.core.data.schema.Record{
    public GeneratedRecord() {
    }

    public GeneratedRecord(dataq.core.data.schema.Record source) {
        this(source.getSchema());
        try {
            Field value = getClass().getField("value");
            value.setAccessible(true);
            value.set(this, value.get(source));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GeneratedRecord(Schema schema) {
        super(schema);
    }

    public Object getObject(String fldName) {
        int index = getSchema().getFieldIndex(fldName);
        if (index < 0) throw new NoSchemaFieldException(getSchema(), fldName);
        return super.getObject(fldName);
    }
}
