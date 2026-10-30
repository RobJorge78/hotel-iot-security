
package com.hotel.eventprocessor;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAlertRepository
        extends JpaRepository<SecurityAlert, Long> {

    boolean existsByDeviceIdAndAlertTypeAndDetectedAtAfter(
            String deviceId,
            String alertType,
            OffsetDateTime detectedAfter
    );
}
