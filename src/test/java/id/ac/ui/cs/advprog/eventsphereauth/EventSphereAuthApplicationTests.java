package id.ac.ui.cs.advprog.eventsphereauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import id.ac.ui.cs.advprog.eventsphereauth.security.JwtAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class EventSphereAuthApplicationTests {
    @MockBean JwtAuthenticationFilter jwtAuthFilter;
    @MockBean UserDetailsService userDetailsService;
    @Test
    void contextLoads() {
    }

}
