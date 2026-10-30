
package com.hotel.securityapi;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {

    List<SecurityEvent> findByEventType(String eventType);

    Page<SecurityEvent> findByEventType(
            String eventType,
            Pageable pageable);
}
