package com.example.lifecycle.playground;

import com.example.lifecycle.pagination.PageResponse;
import com.example.lifecycle.pagination.PaginationRecord;
import com.example.lifecycle.pagination.PaginationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(35)
public class PaginationPlaygroundRunner implements CommandLineRunner {

    private final PaginationService paginationService;

    public PaginationPlaygroundRunner(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("pagination", args)) {
            return;
        }
        PageResponse<PaginationRecord> offsetPage = paginationService.offsetPage("tenant-a", 0, 3);
        PageResponse<PaginationRecord> firstCursorPage = paginationService.cursorPage("tenant-a", null, 3);
        PageResponse<PaginationRecord> secondCursorPage = paginationService.cursorPage(
                "tenant-a", firstCursorPage.nextCursor(), 3);

        System.out.println("\n--- Pagination playground ---");
        System.out.println("Offset page ids: " + offsetPage.items().stream().map(PaginationRecord::id).toList());
        System.out.println("First cursor page ids: "
                + firstCursorPage.items().stream().map(PaginationRecord::id).toList());
        System.out.println("Next cursor: " + firstCursorPage.nextCursor());
        System.out.println("Second cursor page ids: "
                + secondCursorPage.items().stream().map(PaginationRecord::id).toList());
        System.out.println("Try changing the cursor or tenantId and observe the validation contract.");
    }
}