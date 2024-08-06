package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static test.Common.*;

/**
 * These are sick tests for the DatabaseManager class. There are few tests, and
 * they do not cover all the functionality of this class. However, they allow you
 * to make sure that the connection to the database is correct, that SQL queries
 * are compiled correctly, and much more. Initially, the class itself was implemented
 * in such a way that it is very difficult to test it. To test the work of the class
 * in more detail, you need to establish a database connection inside the test class,
 * write a bunch of SQL queries, which, unfortunately, I did not have time for.
 * That's why I wrote simple tests, just to make sure that the program at least works.
 */

public class SQLTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final DatabaseManager manager = new DatabaseManager("test");

    private void addRuEn() throws SQLException {
        manager.addUserRequest(RU, EN, PRIVET, HELLO);
    }

    private void addEnRu() throws SQLException {
        manager.addUserRequest(EN, RU, HELLO, PRIVET);
    }

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void adding() {
        try {
            addRuEn();
            assertEquals("YOUR REQUEST SUCCESSFULLY ADDED TO THE DATABASE", outContent.toString().trim());
            manager.deleteAllUserRequests();
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void deleting() {
        try {
            manager.deleteAllUserRequests();
            assertEquals("ALL YOUR REQUESTS SUCCESSFULLY DELETED FROM THE DATABASE", outContent.toString().trim());
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void zero() {
        try {
            manager.deleteAllUserRequests();
            manager.showAllUserRequests();
            String[] content = outContent.toString().split("\\s+");
            assertEquals("TOTAL:", content[8]);
            assertEquals("0", content[9]);
            manager.deleteAllUserRequests();
        } catch (Exception e) {
            failure(e);
        }
    }


    @Test
    public void last() {
        try {
            addEnRu();
            manager.showLastUserRequest();
            String[] content = outContent.toString().split("\\s+");
            assertEquals(EN + ",", content[13]);
            assertEquals(RU, content[15]);
            assertEquals(HELLO, content[17]);
            assertEquals(PRIVET, content[19]);
            manager.deleteAllUserRequests();
        } catch (Exception e) {
            failure(e);
        }
    }

    @Test
    public void any() {
        try {
            manager.deleteAllUserRequests();
            addRuEn();
            addEnRu();
            manager.showAllUserRequests();
            String[] content = outContent.toString().split("\\s+");
            assertEquals(RU + ",", content[28]);
            assertEquals(EN, content[30]);
            assertEquals(PRIVET, content[32]);
            assertEquals(HELLO, content[34]);
            assertEquals(EN + ",", content[42]);
            assertEquals(RU, content[44]);
            assertEquals(HELLO, content[46]);
            assertEquals(PRIVET, content[48]);
            manager.deleteAllUserRequests();
        } catch (Exception e) {
            failure(e);
        }
    }

}
