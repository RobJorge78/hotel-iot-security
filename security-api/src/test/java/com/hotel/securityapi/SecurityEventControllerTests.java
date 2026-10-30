
package com.hotel.securityapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class SecurityEventControllerTests {

    // TEST 1: Check LOGIN_FAILED event filtering.
    @Test
    void filtersFailedLoginEvents() {

        // Create a fake repository.
        SecurityEventRepository repository =
                mock(SecurityEventRepository.class);

        // Create the controller using our fake repository.
        SecurityEventController controller =
                new SecurityEventController(repository);

        // Define the expected pagination settings.
        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("id").ascending()
        );

        // Prepare an empty page as the fake database response.
        Page<SecurityEvent> expectedPage =
                new PageImpl<>(List.of());

        // Tell the fake repository what to return.
        when(repository.findByEventType("LOGIN_FAILED", pageable))
                .thenReturn(expectedPage);

        // Call the controller with a filter.
        Page<SecurityEvent> result =
                controller.getAllEvents("LOGIN_FAILED", 0, 5);

        // Check the returned result.
        assertEquals(expectedPage, result);

        // Verify the correct repository method was called.
        verify(repository).findByEventType(
                "LOGIN_FAILED",
                pageable
        );
    }

    // TEST 2: Check requests without an event type filter.
    @Test
    void returnsAllEventsWhenNoEventTypeFilter() {

        // Create a fake repository.
        SecurityEventRepository repository =
                mock(SecurityEventRepository.class);

        // Create the controller using our fake repository.
        SecurityEventController controller =
                new SecurityEventController(repository);

        // Define the expected pagination settings.
        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("id").ascending()
        );

        // Prepare an empty page as the fake database response.
        Page<SecurityEvent> expectedPage =
                new PageImpl<>(List.of());

        // Tell the fake repository what to return.
        when(repository.findAll(pageable))
                .thenReturn(expectedPage);

        // Call the controller without an event type filter.
        Page<SecurityEvent> result =
                controller.getAllEvents(null, 0, 5);

        // Check the returned result.
        assertEquals(expectedPage, result);

        // Verify that all events were requested.
        verify(repository).findAll(pageable);
    }
}
