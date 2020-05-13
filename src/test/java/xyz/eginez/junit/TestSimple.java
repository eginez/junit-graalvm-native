package xyz.eginez.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.security.auth.login.Configuration;

@ExtendWith(NativeImageExtension.class)
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
