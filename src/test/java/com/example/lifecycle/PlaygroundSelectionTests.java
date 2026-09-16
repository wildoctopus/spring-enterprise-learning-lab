package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.playground.PlaygroundCatalog;
import com.example.lifecycle.playground.PlaygroundSelection;
import org.junit.jupiter.api.Test;

class PlaygroundSelectionTests {

    @Test
    void defaultsToTheCompleteLearningLab() {
        assertThat(PlaygroundSelection.selectedTopic()).isEqualTo("all");
        assertThat(PlaygroundSelection.includes("streams")).isTrue();
        assertThat(PlaygroundSelection.selectedTopic("--playground=help")).isEqualTo("help");
        assertThat(PlaygroundSelection.isHelp("--playground=help")).isTrue();
    }

    @Test
    void selectsOneTopicAndExcludesOtherTopics() {
        String[] arguments = {"--playground=JPA"};

        assertThat(PlaygroundSelection.selectedTopic(arguments)).isEqualTo("jpa");
        assertThat(PlaygroundSelection.includes("jpa", arguments)).isTrue();
        assertThat(PlaygroundSelection.includes("streams", arguments)).isFalse();
        assertThat(PlaygroundSelection.isKnownTopic(arguments)).isTrue();
        assertThat(PlaygroundSelection.isKnownTopic("--playground=missing")).isFalse();
    }

    @Test
    void catalogCoversTheApplicationLearningAreas() {
        assertThat(PlaygroundCatalog.topics()).extracting(PlaygroundCatalog.Topic::id)
                .containsExactly("lifecycle", "features", "concurrency", "strings", "jvm",
                        "jpa", "sql", "streams", "design", "pagination", "rest");
    }
}