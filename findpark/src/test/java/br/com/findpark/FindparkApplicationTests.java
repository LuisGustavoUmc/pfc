package br.com.findpark;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "mail.username=teste@email.com",
        "mail.password=senha123",
        "mail.templates.path=/templates/email"
})
class FindparkApplicationTests {

    @Test
    void contextLoads() {
    }

}
