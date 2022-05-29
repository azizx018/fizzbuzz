package net.yorksolutions.fizzbuzzbe;

public class FizzBuzz {
    public static String play(final int i) {
        if (i % 3 == 0)
            if (i % 5 == 0)
                return "FizzBuzz";
            else
                return "Fizz";

        if (i % 5 == 0)
            return "Buzz";

        return String.valueOf(i);
    }
}
