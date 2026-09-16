package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaginationApiTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void cursorPaginationReturnsNextCursorAndContinuesFromIt() {
        String base = "http://localhost:" + port + "/api/pagination/records/cursor";
        ResponseEntity<Map> first = restTemplate.getForEntity(base + "?limit=3", Map.class);

        assertThat(first.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat((List<?>) first.getBody().get("items")).hasSize(3);
        String nextCursor = (String) first.getBody().get("nextCursor");
        assertThat(nextCursor).isNotBlank();

        ResponseEntity<Map> second = restTemplate.getForEntity(
                base + "?limit=3&cursor=" + nextCursor, Map.class);

        assertThat((List<?>) second.getBody().get("items")).hasSize(3);
        Map<?, ?> firstItem = (Map<?, ?>) ((List<?>) first.getBody().get("items")).get(0);
        Map<?, ?> secondItem = (Map<?, ?>) ((List<?>) second.getBody().get("items")).get(0);
        assertThat(((Number) secondItem.get("id")).longValue())
                .isGreaterThan(((Number) firstItem.get("id")).longValue());
    }

    @Test
    void offsetPaginationReturnsRequestedPage() {
        String url = "http://localhost:" + port
                + "/api/pagination/records/offset?offset=100&limit=5";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat((List<?>) response.getBody().get("items")).hasSize(5);
    }

    @Test
    void invalidCursorUsesGlobalErrorContract() {
        String url = "http://localhost:" + port
                + "/api/pagination/records/cursor?cursor=not-valid";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("code", "INVALID_CURSOR");
    }

    @Test
    void invalidLimitUsesGlobalErrorContract() {
        String url = "http://localhost:" + port
                + "/api/pagination/records/cursor?limit=101";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("code", "INVALID_PAGINATION_REQUEST");
    }

    @Test
    void cursorCannotBeReplayedAcrossTenants() {
        String base = "http://localhost:" + port + "/api/pagination/records/cursor";
        ResponseEntity<Map> first = restTemplate.getForEntity(base + "?tenantId=tenant-a&limit=3", Map.class);
        String cursor = (String) first.getBody().get("nextCursor");

        ResponseEntity<Map> response = restTemplate.getForEntity(
                base + "?tenantId=tenant-b&limit=3&cursor=" + cursor, Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("code", "INVALID_CURSOR");
    }
}
