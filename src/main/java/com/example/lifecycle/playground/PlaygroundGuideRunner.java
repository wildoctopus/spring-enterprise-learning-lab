package com.example.lifecycle.playground;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(-100)
public class PlaygroundGuideRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String selected = PlaygroundSelection.selectedTopic(args);
        if (selected.equals("all") || selected.equals("help")) {
            printCatalogAndUsage();
            return;
        }

        PlaygroundCatalog.topics().stream()
                .filter(topic -> topic.id().equals(selected))
                .findFirst()
                .ifPresentOrElse(this::printTopic, () -> {
                    throw new IllegalArgumentException("Unknown playground '" + selected
                            + "'. Choose one of: " + PlaygroundCatalog.topics().stream()
                                    .map(PlaygroundCatalog.Topic::id)
                                    .toList());
                });
    }

    private void printCatalog() {
        printCatalogAndUsage();
    }

    public static void printCatalogAndUsage() {
        System.out.println("\n--- Enterprise application playground ---");
        System.out.println("Run one topic with: --playground=<topic>");
        PlaygroundCatalog.topics().forEach(topic ->
                System.out.println("  " + topic.id() + " - " + topic.title()));
        System.out.println("Default mode runs every existing demonstration.");
    }

    private void printTopic(PlaygroundCatalog.Topic topic) {
        System.out.println("\n--- Playground: " + topic.title() + " [" + topic.id() + "] ---");
        System.out.println("Observe: " + topic.observe());
        System.out.println("Break it: " + topic.breakIt());
        System.out.println("Enterprise question: " + topic.enterpriseQuestion());
        System.out.println("Suggested command: mvn spring-boot:run -Dspring-boot.run.arguments=\"--playground="
                + topic.id() + "\"");
    }
}