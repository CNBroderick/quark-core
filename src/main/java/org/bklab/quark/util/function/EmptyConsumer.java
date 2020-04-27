/*
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 * Author: Broderick Johansson
 * E-mail: z@bkLab.org
 * Modify date：2020-04-02 11:36:17
 * _____________________________
 * Project name: vaadin-14-flow
 * Class name：org.bklab.flow.function.EmptyConsumer
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util.function;

import java.util.function.Consumer;

public class EmptyConsumer<T> implements Consumer<T> {
    @Override
    public void accept(T t) {

    }
}
