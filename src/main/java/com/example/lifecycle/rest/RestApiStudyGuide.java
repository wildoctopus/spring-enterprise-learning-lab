package com.example.lifecycle.rest;

import java.util.List;

public final class RestApiStudyGuide {

    private RestApiStudyGuide() {
    }

    public static List<RestQuestion> questions() {
        return List.of(
                new RestQuestion(
                        "Where should resource identity and collection filters live?",
                        "An action-style route mixes identity with an operation and becomes harder to secure and document.",
                        "Bad: GET /playground/rest/users/getUser?id=101",
                        "Good: GET /playground/rest/users/101 and GET /playground/rest/users?role=admin",
                        "RestApiDemoController.badGetUser -> RestApiDemoService.readBadUser",
                        "RestApiDemoController.goodGetUser -> RestApiDemoService.readGoodUser",
                        "curl http://localhost:8080/playground/rest/users/getUser?id=101",
                        "curl http://localhost:8080/playground/rest/users/101"),
                new RestQuestion(
                        "What happens when a POST is retried after a timeout?",
                        "A non-idempotent create can produce two orders even though the client intended one.",
                        "Bad: POST /playground/rest/orders/bad-create",
                        "Good: POST /playground/rest/orders/good-create with Idempotency-Key",
                        "RestApiDemoController.badCreateOrder -> RestApiDemoService.createBadOrder",
                        "RestApiDemoController.goodCreateOrder -> RestApiDemoService.createGoodOrder -> RestApiDemoRepository",
                        "curl -X POST http://localhost:8080/playground/rest/orders/bad-create",
                        "curl -X POST -H 'Idempotency-Key: checkout-42' http://localhost:8080/playground/rest/orders/good-create"),
                new RestQuestion(
                        "Is a PATCH operation safe to retry?",
                        "A delta-based update applies the side effect again on every retry and drifts the final state.",
                        "Bad: PATCH /playground/rest/balance/bad with {\"delta\":50}",
                        "Good: PATCH /playground/rest/balance/good with {\"newBalance\":150}",
                        "RestApiDemoController.badPatchBalance -> RestApiDemoService.patchBadBalance",
                        "RestApiDemoController.goodPatchBalance -> RestApiDemoService.patchGoodBalance",
                        "curl -X PATCH http://localhost:8080/playground/rest/balance/bad",
                        "curl -X PATCH http://localhost:8080/playground/rest/balance/good"),
                new RestQuestion(
                        "What should a repeated DELETE do?",
                        "A retry must not run cleanup side effects again or return an unpredictable result.",
                        "Bad: DELETE /playground/rest/users/101/bad-delete",
                        "Good: DELETE /playground/rest/users/101/good-delete",
                        "RestApiDemoController.badDelete -> RestApiDemoService.deleteBad",
                        "RestApiDemoController.goodDelete -> RestApiDemoService.deleteGood",
                        "curl -X DELETE http://localhost:8080/playground/rest/users/101/bad-delete",
                        "curl -X DELETE http://localhost:8080/playground/rest/users/101/good-delete"),
                new RestQuestion(
                        "Where should payload and query complexity be controlled?",
                        "Unbounded filters and request bodies can consume gateway, memory, CPU, and database capacity.",
                        "Bad: accept arbitrary filters or a large body without an explicit policy.",
                        "Good: validate at the controller boundary, cap limits, and reject oversized payloads early.",
                        "RestApiDemoController.manyQueryParams and RestApiDemoController.largePayload",
                        "RestApiDemoService.manyQueryParams and RestApiDemoService.largePayload",
                        "GET /playground/rest/many-query-params",
                        "POST /playground/rest/large-payload"));
    }

    public record RestQuestion(String question, String problem, String badExample, String goodExample,
                               String badCode, String goodCode, String badEndpoint, String goodEndpoint) {
    }
}
