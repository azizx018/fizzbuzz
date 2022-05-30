package net.yorksolutions.fizzbuzzbe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class IP {
    @JsonProperty("ip")
     final String ipAddress;

    @JsonCreator
    public IP(@JsonProperty("ip") String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP ip = (IP) o;
        return Objects.equals(ipAddress, ip.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress);
    }
}