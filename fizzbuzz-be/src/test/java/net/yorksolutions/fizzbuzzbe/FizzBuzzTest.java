package net.yorksolutions.fizzbuzzbe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * FizzBuzz -
 * - if the input is a multiple of 3, output Fizz
 * - if the input is a multiple of 5, output Buzz
 * - if the input is a multiple of 3 and 5, output FizzBuzz
 * - otherwise output the input
 */
public class FizzBuzzTest {

    @Test
    void shouldOutputTheInput() {
        Assertions.assertEquals("1", FizzBuzz.play(1));
        Assertions.assertEquals("2", FizzBuzz.play(2));
    }

    @Test
    void shouldOutputFizzForMultiplesOf3() {
        Assertions.assertEquals("Fizz", FizzBuzz.play(3));
        Assertions.assertEquals("Fizz", FizzBuzz.play(9));
    }

    @Test
    void shouldOutputBuzzForMultiplesOf5() {
        Assertions.assertEquals("Buzz", FizzBuzz.play(5));
        Assertions.assertEquals("Buzz", FizzBuzz.play(20));
    }

    @Test
    void shouldOutputFizzBuzzForMultiplesOf5and3() {
        Assertions.assertEquals("FizzBuzz", FizzBuzz.play(15));
        Assertions.assertEquals("FizzBuzz", FizzBuzz.play(30));
    }
}
