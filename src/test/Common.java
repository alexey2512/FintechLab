package test;

import static org.junit.jupiter.api.Assertions.*;

public class Common {

    static final String
            HELLO = "hello",
            PRIVET = "привет",
            WORLD = "world",
            EN = "en",
            RU = "ru",
            IT = "it",
            FR = "fr",
            ES = "es";

    static void failure(Exception e) {
        fail("Exception occurred, but not expected: " + e.getMessage());
    }

}
