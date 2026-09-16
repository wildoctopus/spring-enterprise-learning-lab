package com.example.lifecycle.playground;

import java.util.Arrays;
import java.util.Locale;

/** Selects one learning topic while preserving the full-demo default. */
public final class PlaygroundSelection {

    private PlaygroundSelection() {
    }

    public static String selectedTopic(String... args) {
        return Arrays.stream(args)
                .filter(argument -> argument.startsWith("--playground="))
                .map(argument -> argument.substring("--playground=".length()))
                .map(value -> value.trim().toLowerCase(Locale.ROOT))
                .findFirst()
                .orElse("all");
    }

    public static boolean isHelp(String... args) {
        return selectedTopic(args).equals("help");
    }

    public static boolean isKnownTopic(String... args) {
        String selected = selectedTopic(args);
        return selected.equals("all") || selected.equals("help")
                || PlaygroundCatalog.topics().stream().anyMatch(topic -> topic.id().equals(selected));
    }

    public static boolean includes(String topic, String... args) {
        String selected = selectedTopic(args);
        return selected.equals("all") || selected.equals(topic);
    }
}