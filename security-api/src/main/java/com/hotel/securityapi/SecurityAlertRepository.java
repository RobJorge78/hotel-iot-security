
package com.hotel.securityapi;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAlertRepository
        extends JpaRepository<SecurityAlert, Long> {

    List<SecurityAlert> findBySeverity(String severity);

    Page<SecurityAlert> findBySeverity(
            String severity,
            Pageable pageable);
}
