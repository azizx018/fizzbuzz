package net.yorksolutions.fizzbuzzbe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPTest {
    @Test
    void itShouldReturnAnIP() {
        IP ip = new IP("1.2.3.4");
//        assertEquals("1.2.3.4", ip.getIp());
    }

    @Test
    void itShouldJsonify() throws JsonProcessingException {
        final String ip = "1.2.3.4";
        assertEquals(
                "{\"ip\":\""+ip+"\"}",
                new ObjectMapper().writeValueAsString(new IP(ip))
        );
    }

    class Controller {
        String getDate() {
            final var now = LocalDateTime.now();
            return now.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        }
    }

    @Test
    void itShouldReturnARecentTime() {
        final var controller = new Controller();
        final var expected = LocalDateTime.of(2020, 1, 1, 1, 1, 1, 1);
        Mockito.mockStatic(LocalDateTime.class).when(() -> LocalDateTime.now())
                .thenReturn(expected);
        assertEquals("01-01-2020", controller.getDate());
    }
}