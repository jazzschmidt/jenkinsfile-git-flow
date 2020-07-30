package com.github.jazzschmidt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest {

    @Test
    void greetsWithName() {
        // given
        var greeter = new Greeter();

        // when
        var greeting = greeter.greet("Mylord");

        // then
        assertEquals("Hello Mylord", greeting);
    }

}