package org.bklab.quark.util.schema;

import dataq.core.data.schema.Record;
import dataq.core.data.schema.Recordset;
import dataq.core.data.schema.Schema;

public class RecordFactory {

    public final Record record;

    public RecordFactory(Recordset recordset) {
        this.record = recordset.createRecord();
    }

    public RecordFactory(Record record) {
        this.record = record;
    }

    public RecordFactory(Schema schema) {
        this.record = new Record(schema);
    }

    public RecordFactory set(String name, Number value) {
        record.setNumber(name, value);
        return this;
    }

    public RecordFactory set(int name, Number value) {
        record.setNumber(name, value);
        return this;
    }

    public RecordFactory set(String name, Object value) {
        record.setObject(name, value);
        return this;
    }

    public RecordFactory set(int name, Object value) {
        record.setObject(name, value);
        return this;
    }

    public RecordFactory setOrConvert(String name, Object value) {
        record.setOrConvert(name, value);
        return this;
    }

    public RecordFactory setOrConvert(int name, Object value) {
        record.setOrConvert(name, value);
        return this;
    }

    public RecordFactory set(Schema schema) {
        record.setSchema(schema);
        return this;
    }

    public RecordFactory set(String name, String value) {
        record.setString(name, value);
        return this;
    }

    public RecordFactory set(int name, String value) {
        record.setString(name, value);
        return this;
    }

    public Record get() {
        return record;
    }
}