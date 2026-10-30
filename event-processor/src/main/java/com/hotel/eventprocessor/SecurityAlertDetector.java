
package com.hotel.eventprocessor;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SecurityAlertDetector {

    private final SecurityEventRepository repository;

    public SecurityAlertDetector(SecurityEventRepository repository) {
        this.repository = repository;
    }

    // Detect three failed logins within 60 seconds.
    public boolean hasRepeatedFailedLogins(String deviceId) {

        return countRecentEvents(deviceId, "LOGIN_FAILED") >= 3;
    }

    // Detect three device disconnections within 60 seconds.
    public boolean hasRepeatedDisconnections(String deviceId) {

        return countRecentEvents(deviceId, "DEVICE_DISCONNECTED") >= 3;
    }

    // Count matching events for a device during the last minute.
    private long countRecentEvents(String deviceId, String eventType) {

        OffsetDateTime oneMinuteAgo =
                OffsetDateTime.now().minusMinutes(1);

        List<SecurityEvent> events = repository.findAll();

        return events.stream()
                .filter(event -> deviceId.equals(event.getDeviceId()))
                .filter(event -> eventType.equals(event.getEventType()))
                .filter(event -> event.getTimestamp() != null)
                .filter(event -> event.getTimestamp().isAfter(oneMinuteAgo))
                .count();
    }
}
