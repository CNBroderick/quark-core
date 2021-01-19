package org.bklab.quark.operation.http;

import dataq.core.operation.AbstractOperation;
import dataq.core.operation.OperationResult;
import org.bklab.quark.util.time.RunningTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class JsonRpcOperation<T> extends AbstractOperation {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private RunningTime runningTime;

    public JsonRpcOperation() {
        setOperationName(getClass().getSimpleName());
    }

    @Override
    public OperationResult doExecute() throws Exception {
        try {
            return successResult(parseResult(doHttpRequest(getRequestUrl(), getMethod(), getBodyPublisher(), getBodyHandler())));
        } catch (Exception e) {
            return GenerateOperationResult.fromException(e);
        }
    }

    protected abstract Object parseResult(T responseBody) throws Exception;

    protected <E> E doHttpRequest(String url, String method,
                                  HttpRequest.BodyPublisher bodyPublisher,
                                  HttpResponse.BodyHandler<E> bodyHandler
    ) throws Exception {
        return HttpClient.newBuilder().build()
                .send(HttpRequest.newBuilder()
                        .timeout(getTimeout())
                        .header("Content-Type", "application/json")
                        .method(method, bodyPublisher).uri(new URI(url)).build(), bodyHandler).body();
    }

    @Override
    public void beforeExecute() {
        super.beforeExecute();
        runningTime = new RunningTime();
    }

    @Override
    public void afterExecute() {
        super.afterExecute();
        logger.info("执行完成 用时：" + runningTime.time());
    }

    public String getRequestUrl() {
        String host = getRequestHost();
        String uri = getRequestUri();
        return host == null || uri == null
               ? null
               : uri.startsWith("/") || host.endsWith("/") ? host + uri : host + "/" + uri;
    }

    public abstract String getRequestHost();

    public abstract String getRequestUri();

    public abstract String getMethod();

    public HttpRequest.BodyPublisher getBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }

    public abstract HttpResponse.BodyHandler<T> getBodyHandler();

    public Duration getTimeout() {
        return Duration.ofMinutes(1);
    }

}
