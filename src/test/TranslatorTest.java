package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import util.Translator;
import static test.Common.*;

/**
 * This is tests for Translator class. I tried to cover most of the extreme cases which
 * could be when class functions are used. These tests check the determinism of the translation,
 * that we get same results when we translate all sentence using {@code Translator.parallelTranslate}
 * and when we split the sentence by the whitespaces, translate all of them separately
 * and join them back to the sentence, that result of translation via
 * {@code Translator.parallelTranslate} doesn't depend on whitespaces separating the words and some exceptions.
 */

public class TranslatorTest {

    @Test
    public void determinism1() {
        try {
            Translator translator = new Translator(EN, IT);
            String result = translator.parallelTranslate(HELLO);
            for (int i = 0; i < 3; i++) {
                assertEquals(result, translator.parallelTranslate(HELLO));
            }
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void determinism2() {
        try {
            Translator translator = new Translator(EN, RU);
            String ruString = HELLO + " " + WORLD;
            String result = translator.parallelTranslate(ruString);
            for (int i = 0; i < 3; i++) {
                assertEquals(result, translator.parallelTranslate(ruString));
            }
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void constructible() {
        try {
            Translator translator = new Translator(EN, ES);
            String tran1 = translator.parallelTranslate(HELLO), tran2 = translator.parallelTranslate(WORLD);
            assertEquals(tran1 + " " + tran2, translator.parallelTranslate(HELLO + " " + WORLD));
            assertEquals(tran2 + " " + tran1, translator.parallelTranslate(WORLD + " " + HELLO));
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void whitespaces() {
        try {
            Translator translator = new Translator(EN, FR);
            String result1 = translator.parallelTranslate(HELLO + " " + WORLD);
            String result2 = translator.parallelTranslate(WORLD + " " + HELLO);
            String[] whitespaces = new String[]{"\n", "\r", "  ", "\t", "\t\t", "\n\r"};
            for (String whitespace : whitespaces) {
                assertEquals(result1, translator.parallelTranslate(HELLO + whitespace + WORLD));
                assertEquals(result2, translator.parallelTranslate(WORLD + whitespace + HELLO));
            }
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void exceptions1() {
        assertThrows(Exception.class, () -> new Translator("enn", RU).parallelTranslate(HELLO));
        assertThrows(Exception.class, () -> new Translator("aba", FR).parallelTranslate(HELLO));
        assertThrows(Exception.class, () -> new Translator("y", IT).parallelTranslate(HELLO));
    }

    @Test
    public void exceptions2() {
        assertThrows(Exception.class, () -> new Translator(EN, "rru").parallelTranslate(HELLO));
        assertThrows(Exception.class, () -> new Translator(ES, "u").parallelTranslate(HELLO));
        assertThrows(Exception.class, () -> new Translator(FR, "google").parallelTranslate(HELLO));
    }

    @Test
    public void exceptions3() {
        assertThrows(Exception.class, () -> new Translator("e", "r").parallelTranslate(WORLD));
        assertThrows(Exception.class, () -> new Translator("r", "e").parallelTranslate(WORLD));
        assertThrows(Exception.class, () -> new Translator("", "").parallelTranslate(WORLD));
    }

    @Test
    public void exceptions4() {;
        assertThrows(Exception.class, () -> new Translator(EN, EN).parallelTranslate(WORLD));
        assertThrows(Exception.class, () -> new Translator(RU, RU).parallelTranslate(WORLD));
        assertThrows(Exception.class, () -> new Translator(ES, ES).parallelTranslate(WORLD));
    }

    @Test
    public void exceptions5() {
        assertThrows(Exception.class, () -> new Translator(EN, RU).parallelTranslate(""));
        assertThrows(Exception.class, () -> new Translator(FR, IT).parallelTranslate(""));
    }

}
