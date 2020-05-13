package xyz.eginez.others;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class OtherTest {
    @Test
    public void other() {
        Assertions.assertThrows(RuntimeException.class, () -> { throw new RuntimeException("yes");});
    }
}
