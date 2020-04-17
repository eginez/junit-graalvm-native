package xyz.eginez.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

//@ExtendWith(NativeImageExtension.class)
public class TestSimple {
    @Test
    public  void one() {
        System.out.println("hello");
    }
}
