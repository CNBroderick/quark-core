package org.bklab.quark.util.schema;

import dataq.core.data.schema.Record;
import dataq.core.data.schema.*;

public class SchemaFactory {

    private final Schema schema;
    private final Recordset recordset;

    public SchemaFactory() {
        this.schema = new Schema();
        this.recordset = new GenerateRecordset(schema);
    }

    public SchemaFactory(Schema schema) {
        this(schema, new Recordset(schema));
    }

    public SchemaFactory(Schema schema, Recordset recordset) {
        this.schema = schema;
        this.recordset = recordset;
    }

    public SchemaFactory unknownTypeField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.UNKNOWN_TYPE));
            }
        }
        return this;
    }

    public SchemaFactory intField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.INT));
            }
        }
        return this;
    }

    public SchemaFactory longField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.LONG));
            }
        }
        return this;
    }

    public SchemaFactory floatField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.FLOAT));
            }
        }
        return this;
    }

    public SchemaFactory doubleField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.DOUBLE));
            }
        }
        return this;
    }

    public SchemaFactory string(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.STRING));
            }
        }
        return this;
    }

    public SchemaFactory stringField(String... names) {
        return string(names);
    }

    public SchemaFactory dateField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.DATE));
            }
        }
        return this;
    }

    public SchemaFactory datetimeField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.DATETIME));
            }
        }
        return this;
    }

    public SchemaFactory booleanField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.BOOLEAN));
            }
        }
        return this;
    }

    public SchemaFactory blobField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.BLOB));
            }
        }
        return this;
    }

    public SchemaFactory objectField(String... names) {
        if (names != null) {
            for (String name : names) {
                schema.addField(new Field(name, DataType.OBJECT));
            }
        }
        return this;
    }

    public Recordset recordset() {
        return recordset;
    }

    public Record record() {
        return new GeneratedRecord(schema);
    }

    public RecordFactory recordFactory() {
        return new RecordFactory(record());
    }

    public Schema get() {
        return schema;
    }
}
