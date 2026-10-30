
package com.hotel.eventprocessor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class SecurityAlertController {

    private final SecurityAlertRepository repository;

    public SecurityAlertController(SecurityAlertRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SecurityAlert> getAllAlerts() {
        return repository.findAll();
    }
}
