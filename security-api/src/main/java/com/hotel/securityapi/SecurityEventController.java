
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
@RequestMapping("/api/events")
public class SecurityEventController {

    private final SecurityEventRepository repository;

    public SecurityEventController(SecurityEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<SecurityEvent> getAllEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Create pagination settings.
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        // If an event type was provided, return matching events.
        if (eventType != null && !eventType.isBlank()) {
            return repository.findByEventType(eventType, pageable);
        }

        // Otherwise, return all events using pagination.
        return repository.findAll(pageable);
    }
}
