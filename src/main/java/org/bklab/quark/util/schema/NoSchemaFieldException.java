package org.bklab.quark.util.schema;

import com.google.gson.GsonBuilder;
import dataq.core.data.schema.Schema;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

public class NoSchemaFieldException extends NoSuchElementException {

    private final Schema schema;
    private final String field;

    public NoSchemaFieldException(Schema schema, String fldName) {
        super(MessageFormat.format(
                "No such filed ''{0}'' in schema{1}, exist fields: {2}.",
                fldName, schema.getClass() == Schema.class ? "" : "[" + schema.getClass().getName() + "]",
                schema.toString()));
        this.schema = schema;
        this.field = fldName;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}
