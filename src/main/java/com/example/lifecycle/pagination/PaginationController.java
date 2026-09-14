package com.example.lifecycle.pagination;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagination/records")
public class PaginationController {

    private final PaginationService paginationService;

    public PaginationController(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @GetMapping("/offset")
    public PageResponse<PaginationRecord> offset(
            @RequestParam(defaultValue = "tenant-a") String tenantId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return paginationService.offsetPage(tenantId, offset, limit);
    }

    @GetMapping("/cursor")
    public PageResponse<PaginationRecord> cursor(
            @RequestParam(defaultValue = "tenant-a") String tenantId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {
        return paginationService.cursorPage(tenantId, cursor, limit);
    }
}
