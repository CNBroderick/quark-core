package org.bklab.quark.entity.record;

import org.bklab.quark.util.json.GsonJsonObjectUtil;
import org.bklab.quark.util.schema.RecordFactory;

import java.util.function.BiConsumer;

public interface IJsonRecordConsumer extends BiConsumer<GsonJsonObjectUtil, RecordFactory> {

    @Override
    void accept(GsonJsonObjectUtil json, RecordFactory record);
}
