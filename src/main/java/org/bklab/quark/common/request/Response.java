package org.bklab.quark.common.request;

import dataq.core.ValueObject;
import dataq.core.data.schema.Recordset;
import dataq.core.operation.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Response {
    private Exception exception;
    private Object successData;
    private String operationName;

    public Response() {
    }

    public Response(Exception e) {
        this.exception = e;
    }

    public Response(String errMessage) {
        this.exception = new RuntimeException(errMessage);
    }

    public Response(OperationResult operationResult) {
        Response result = new Response();
        if (operationResult.isSuccess()) {
            this.successData = operationResult.asObject();
        } else if (operationResult.isException()) {
            this.exception = operationResult.getException();
        }
    }

    public Response(Object successData) {
        this.successData = successData;
    }


    public static Response fromException(Exception e) {
        Response result = new Response();
        result.exception = e;
        return result;
    }

    public static Response fromException(String errMessage) {
        Response result = new Response();
        result.exception = new RuntimeException(errMessage);
        return result;
    }

    public static Response fromSuccess(Object successData) {
        Response result = new Response();
        result.successData = successData;
        return result;
    }

    public static Response fromOperationResult(OperationResult opr) {
        Response result = new Response();
        if (opr.isSuccess()) return Response.fromSuccess(opr.asObject());

        return Response.fromException(opr.getException());
    }

    public String getOperationName() {
        return operationName;
    }

    public Response setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public String getErrorMessage() {
        if (isSuccess()) return null;
        return exception.getMessage();
    }

    public Boolean isNotFound() {
        //错误当成未无数据处理
        return successData == null;
    }

    public boolean isException() {
        return exception != null;
    }

    public boolean isSuccess() {
        return exception == null;
    }

    public Exception getException() {
        return exception;
    }

    public <T> List<T> asList(Class<T> tClass) {
        if (successData instanceof List<?>) {
            return ((List<?>) successData).stream().filter(tClass::isInstance)
                    .map(tClass::cast).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public <T> T asObject(Class<T> classT) {
        if (classT.isInstance(successData)) {
            return classT.cast(successData);
        }
        return null;
    }

    /*
     * Java 11 version
     */
    public <T> List<T> asList() {
        return successData == null ? null : (List<T>) successData;
    }

    /*
     * Java 11 version
     */
    public <T> T asObject() {
        return successData == null ? null : (T) successData;
    }

    public ValueObject asValueObject() {
        return successData == null ? null : (ValueObject) successData;
    }

    public List<ValueObject> asValueObjectList() {
        if (successData instanceof List<?>) {
            List<?> successData = (List<?>) this.successData;
            return successData.stream().filter(ValueObject.class::isInstance)
                    .map(ValueObject.class::cast).collect(Collectors.toList());
        }
        return null;
    }

    public <T> Response ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) consumer.accept(asObject());
        return this;
    }

    public <T> Response ifException(Consumer<Exception> consumer) {
        if (isException()) consumer.accept(exception);
        return this;
    }

    public String asString() {
        return successData == null ? null : (String) successData;
    }

    public Recordset asRecordset() {
        return successData == null ? null : (Recordset) successData;
    }

    public Number asNumber() {
        return successData == null ? null : (Number) successData;
    }

    public Integer asInt() {
        return successData == null ? null : (Integer) successData;
    }

}
