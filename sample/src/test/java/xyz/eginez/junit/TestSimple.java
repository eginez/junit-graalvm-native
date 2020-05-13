package xyz.eginez.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestSimple {
    @Test
    void one() {
        Assertions.assertEquals(2, 1 + 1);
    }

    @Test
    void two() {
        Assertions.assertEquals(2, 1 + 1);
    }
}
