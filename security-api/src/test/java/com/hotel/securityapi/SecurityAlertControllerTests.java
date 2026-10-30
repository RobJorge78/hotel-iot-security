
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

class SecurityAlertControllerTests {

    // TEST 1: Check HIGH-severity filtering.
    @Test
    void filtersHighSeverityAlerts() {

        // Create a fake repository.
        SecurityAlertRepository repository =
                mock(SecurityAlertRepository.class);

        // Create the controller using our fake repository.
        SecurityAlertController controller =
                new SecurityAlertController(repository);

        // Define the expected pagination settings.
        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by("id").ascending()
        );

        // Prepare an empty page as the fake database response.
        Page<SecurityAlert> expectedPage =
                new PageImpl<>(List.of());

        // Tell the fake repository what to return.
        when(repository.findBySeverity("HIGH", pageable))
                .thenReturn(expectedPage);

        // Call the controller method.
        Page<SecurityAlert> result =
                controller.getAllAlerts("HIGH", 0, 2);

        // Check that the expected page was returned.
        assertEquals(expectedPage, result);

        // Verify the correct repository method was called.
        verify(repository).findBySeverity("HIGH", pageable);
    }

    // TEST 2: Check requests without a severity filter.
    @Test
    void returnsAllAlertsWhenNoSeverityFilter() {

        // Create a fake repository.
        SecurityAlertRepository repository =
                mock(SecurityAlertRepository.class);

        // Create the controller using our fake repository.
        SecurityAlertController controller =
                new SecurityAlertController(repository);

        // Define the pagination settings.
        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by("id").ascending()
        );

        // Prepare an empty page as the fake database response.
        Page<SecurityAlert> expectedPage =
                new PageImpl<>(List.of());

        // Tell the fake repository what to return.
        when(repository.findAll(pageable))
                .thenReturn(expectedPage);

        // Call the controller without a severity filter.
        Page<SecurityAlert> result =
                controller.getAllAlerts(null, 0, 2);

        // Check that the expected page was returned.
        assertEquals(expectedPage, result);

        // Verify that all alerts were requested.
        verify(repository).findAll(pageable);
    }
}
