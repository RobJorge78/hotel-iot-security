
package com.hotel.securityapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SecurityApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
