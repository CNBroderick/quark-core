package org.bklab.quark.service;

import org.bklab.quark.common.request.Response;

import java.util.List;
import java.util.function.Consumer;

public class EntityService implements RequestService<EntityService> {

    public long nextEntityInstanceId() {
        return -1;
    }

    @Override
    public String getCurrentUserId() {
        return "Broderick";
    }

    @Override
    public List<Consumer<Exception>> getExceptionConsumers() {
        return null;
    }

    @Override
    public List<Consumer<Response>> getSaveListeners() {
        return null;
    }
}
