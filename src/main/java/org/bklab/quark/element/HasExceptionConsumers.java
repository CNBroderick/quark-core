package org.bklab.quark.element;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public interface HasExceptionConsumers<E extends HasExceptionConsumers<E>> {

//  private final List<Consumer<Exception>> exceptionConsumers = new ArrayList<>();

    default void callExceptionConsumers(Exception e) {
        getExceptionConsumers().forEach(c -> c.accept(e));
    }

    default Consumer<Exception> createExceptionConsumer() {
        return e -> getExceptionConsumers().forEach(c -> c.accept(e));
    }

    default E addExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        getExceptionConsumers().add(exceptionConsumer);
        return (E) this;
    }

    default E removeExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        getExceptionConsumers().remove(exceptionConsumer);
        return (E) this;
    }

    List<Consumer<Exception>> getExceptionConsumers();
}
