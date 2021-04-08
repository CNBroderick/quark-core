package org.bklab.quark.util.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParameterWriter {
    private final String template;

    public TemplateParameterWriter(String template) {
        this.template = template;
    }

    public String write(JsonObject data) {
        String body = template;
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            JsonElement jsonElement = entry.getValue();

            if (jsonElement.isJsonNull()) {
                body = replace(body, entry.getKey(), "");
                continue;
            }

            if (jsonElement.isJsonPrimitive()) {
                body = replace(body, entry.getKey(), jsonElement.getAsString());
                continue;
            }

            body = replace(body, entry.getKey(), jsonElement.toString());
        }
        return clearBodyParameters(body);
    }

    private String replace(String source, String name, String value) {
        return source.replaceAll("\\$\\{" + name + "}", value);
    }

    private String clearBodyParameters(String body) {
        Matcher matcher = Pattern.compile("(?<=(?<!\\\\)\\$\\{)(.*?)(?=(?<!\\\\)})").matcher("" + body);
        while (matcher.find()) {
            body = replace(body, matcher.group(), "");
        }
        return body;
    }
}
