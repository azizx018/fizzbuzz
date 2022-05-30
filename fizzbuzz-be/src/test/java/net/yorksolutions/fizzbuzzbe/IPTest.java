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
    void itShouldReturnAnIP() throws JsonProcessingException {
        final String ip = "1.2.3.4";
        IP expected = new IP("1.2.3.4");
        IP actual = new ObjectMapper().readValue("{\"ip\":\"" + ip + "\"}", IP.class);
       assertEquals(expected, actual);
    }

    @Test
    void itShouldJsonify() throws JsonProcessingException {
        final String ip = "1.2.3.4";
        assertEquals(
                "{\"ip\":\"" + ip + "\"}",
                new ObjectMapper().writeValueAsString(new IP(ip))
        );
    }
}
