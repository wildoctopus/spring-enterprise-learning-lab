package com.example.lifecycle.rest;

import java.util.List;
import com.example.lifecycle.playground.PlaygroundSelection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(60)
public class RestApiPlaygroundRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("rest", args)) {
            return;
        }

        List<RestApiPlayground.ApiScenario> scenarios = RestApiPlayground.scenarios();

        System.out.println("\n--- REST API playground ---");
        System.out.println("Use these scenarios to understand real API failures, retry safety, and clean resource design.");
        printStudyGuide();
        System.out.println("\nThis demo section shows the failure mode in code, then the fix that should be applied.");
        RestApiDesignDemo.printAll();

        System.out.println("\n--- Scenario explanations ---");
        for (RestApiPlayground.ApiScenario scenario : scenarios) {
            System.out.println("\n### Question");
            System.out.println(scenario.question());
            System.out.println("\n### Code");
            System.out.println(scenario.code());
            System.out.println("\n### What we are using");
            System.out.println(scenario.what());
            System.out.println("\n### Why we are using it");
            System.out.println(scenario.why());
            System.out.println("\n### Where it breaks");
            System.out.println(scenario.breakIt());
            System.out.println("\n### Best approach");
            System.out.println(scenario.bestApproach());
            System.out.println("\n### Lead insight");
            System.out.println(scenario.leadInsight());
            System.out.println("\n---");
        }
    }

    private void printStudyGuide() {
        System.out.println("\n--- Questions and code paths ---");
        for (RestApiStudyGuide.RestQuestion question : RestApiStudyGuide.questions()) {
            System.out.println("\nQuestion: " + question.question());
            System.out.println("Problem: " + question.problem());
            System.out.println("Bad example: " + question.badExample());
            System.out.println("  Code path: " + question.badCode());
            System.out.println("  Try: " + question.badEndpoint());
            System.out.println("Good example: " + question.goodExample());
            System.out.println("  Code path: " + question.goodCode());
            System.out.println("  Try: " + question.goodEndpoint());
        }
    }
}
