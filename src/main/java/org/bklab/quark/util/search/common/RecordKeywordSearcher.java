package org.bklab.quark.util.search.common;

import dataq.core.data.schema.Record;
import dataq.core.data.schema.Recordset;
import dataq.core.operation.OperationContext;
import dataq.core.operation.OperationResult;
import org.bklab.quark.util.search.IKeywordSearcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class RecordKeywordSearcher implements IKeywordSearcher<Record> {

    public OperationResult find(OperationResult result, OperationContext context) {
        if (context == null) return result;
        String keyword = context.getString("keyword");
        if (keyword == null || keyword.trim().isEmpty()) return result;
        return OperationResult.fromSuccess(find(result.asList(), keyword)).setOperationName(context.getOperationName());
    }

    public Collection<Record> find(Recordset recordset, String keyword) {
        return find(recordset.asList(), keyword);
    }

    public Collection<Record> find(Collection<Record> records, String keyword) {
        return keyword == null || keyword.trim().isEmpty() ? records
                : records.stream().filter(r -> matchKeyword(r, keyword)).collect(Collectors.toList());
    }

    @Override
    public boolean matchKeyword(Record record, String keyword) {
        return Arrays.stream(record.getSchema().fields())
                .anyMatch(field -> new KeyWordSearcher<>(record.getObject(field.getName())).match(keyword));
    }
}
