package com.blog.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // Uses application-test.properties if present
class BlogApplicationTests {

    @Test
    void contextLoads() {
        // ✅ Verifies that the Spring context loads without issues
    }
}
