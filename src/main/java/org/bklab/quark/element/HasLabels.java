package org.bklab.quark.element;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public interface HasLabels<T extends HasLabels<T>> {


    default T addLabel(String... labels) {
        getLabels().addAll(Arrays.asList(labels));
        return (T) this;
    }

    default boolean hasLabel(String label) {
        return getLabels().contains(label);
    }

    default String toLabelJson() {
        return getLabels().stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll).toString();
    }


    default T setLabelsFromJson(String jsonArray) {
        HashSet<String> hashSet = new LinkedHashSet<>();
        try {
            new Gson().fromJson(jsonArray, JsonArray.class).forEach(a -> hashSet.add(a.getAsString()));
        } catch (JsonSyntaxException | NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return setLabels(hashSet);
    }

    Set<String> getLabels();

    T setLabels(Set<String> labels);
}
