package org.bklab.quark.element;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public interface HasSuccessListeners<T, E extends HasSuccessListeners<T, E>> {

//  private final List<Consumer<>> saveListeners = new ArrayList<>();

    default void callSuccessListeners(T object) {
        getSuccessListeners().forEach(s -> s.accept(object));
    }

    default E addSuccessListeners(Consumer<T> saveListener) {
        getSuccessListeners().add(saveListener);
        return (E) this;
    }

    default boolean removeSuccessListeners(Consumer<T> saveListener) {
        return getSuccessListeners().remove(saveListener);
    }

    List<Consumer<T>> getSuccessListeners();

}
