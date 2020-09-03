package org.bklab.quark.element;

import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public interface HasBiSaveListeners<T, P, E extends HasBiSaveListeners<T, P, E>> {

//  private final List<BiConsumer<>> saveListeners = new ArrayList<>();

    default void callSaveListeners(T object, P object2) {
        getSaveListeners().forEach(s -> s.accept(object, object2));
    }

    default E addSaveListeners(BiConsumer<T, P> saveListener) {
        getSaveListeners().add(saveListener);
        return (E) this;
    }

    default boolean removeSaveListeners(BiConsumer<T, P> saveListener) {
        return getSaveListeners().remove(saveListener);
    }

    List<BiConsumer<T, P>> getSaveListeners();

}
