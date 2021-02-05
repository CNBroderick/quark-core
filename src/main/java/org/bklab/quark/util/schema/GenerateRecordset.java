package org.bklab.quark.util.schema;

import dataq.core.data.schema.Record;
import dataq.core.data.schema.Recordset;
import dataq.core.data.schema.Schema;

public class GenerateRecordset extends Recordset {
    public GenerateRecordset(Schema schema) {
        super(schema);
    }

    @Override
    public Record createRecord() {
        GeneratedRecord generatedRecord = new GeneratedRecord(getSchema());
        addRecord(generatedRecord);
        return generatedRecord;
    }
}
