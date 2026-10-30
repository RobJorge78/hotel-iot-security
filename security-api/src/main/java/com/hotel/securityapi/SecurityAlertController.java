
package com.hotel.securityapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class SecurityAlertController {

    private final SecurityAlertRepository repository;

    public SecurityAlertController(SecurityAlertRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<SecurityAlert> getAllAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Create pagination settings.
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        // If severity is provided, return matching alerts.
        if (severity != null && !severity.isBlank()) {
            return repository.findBySeverity(severity, pageable);
        }

        // Otherwise, return all alerts using pagination.
        return repository.findAll(pageable);
    }
}
