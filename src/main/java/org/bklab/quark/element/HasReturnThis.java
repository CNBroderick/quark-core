package org.bklab.quark.element;

public interface HasReturnThis<E extends HasReturnThis<E>> {
    default E thisObject() {
        //noinspection unchecked
        return (E) this;
    }
}
