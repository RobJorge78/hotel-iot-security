
package com.hotel.eventprocessor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class SecurityEventController {

    private final SecurityEventRepository repository;

    public SecurityEventController(SecurityEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SecurityEvent> getAllEvents() {
        return repository.findAll();
    }
}
