package util;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.sql.*;

/**
 * This class is responsible for working with the database in the app.
 * The functionality allows you to add user requests to the database, print them
 * to the console and delete them. The database contains only one table named "user_requests" storing
 * all necessary information about all users requests.
 */

public final class DatabaseManager {

    private static final String GET_IP_URL = "https://checkip.amazonaws.com/";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fintech_lab_translator";
    private static final String USER = "univ_user";
    private static final String PASSWORD = "Alexey/Stepurin2512";

    private final String ip;


    /**
     * Gets an ip of this computer via amazon source <a href="https://checkip.amazonaws.com/">...</a>
     * and assigns it to field {@code private final String ip}
     * @throws RestClientException if exception occurs during the connection or communication with amazon source
     */
    public DatabaseManager() {
        this.ip = new RestTemplate().getForObject(GET_IP_URL, String.class);
    }

    /**
     * assigns ip to the field {@code private final String ip}
     * @param ip given String ip
     */
    public DatabaseManager(String ip) {
        this.ip = ip;
    }


    @FunctionalInterface
    private interface Action {
        void accept(PreparedStatement ps) throws SQLException;
    }

    private void executeSql(String sql, Action action) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            action.accept(ps);
        }
    }

    private void showTableRow(ResultSet rs) throws SQLException {
        System.out.println("id: " + rs.getString("id") +
                ", time: " + rs.getString("request_time") +
                ", source_language: " + rs.getString("source_language") +
                ", target_language: " + rs.getString("target_language"));
        String ls = System.lineSeparator();
        System.out.println("original_text: " + ls + rs.getString("original_text"));
        System.out.println("translated_text: " + ls + rs.getString("translated_text"));
        System.out.println("====================");
    }


    /**
     * Gets a user request data and adds it to the table "user_requests" where id is auto-increment and
     * request_time has a default value - current timestamp. user_ip of this request will be an ip-address which
     * this DatabaseManager was initialized with. If successful, prints to {@code System.out} success message.
     * @param fromLang source language of translation
     * @param toLang target language of translation
     * @param original original text to be translated
     * @param translated result of translation of original text
     * @throws SQLException if SQL error occurs during the connection or communication with db server
     */
    public void addUserRequest(String fromLang, String toLang, String original, String translated) throws SQLException {
        executeSql("INSERT INTO user_requests " +
                "(user_ip, source_language, target_language, original_text, translated_text) VALUES (?, ?, ?, ?, ?);",
                (ps) -> {
                    ps.setString(1, ip);
                    ps.setString(2, fromLang);
                    ps.setString(3, toLang);
                    ps.setString(4, original);
                    ps.setString(5, translated);
                    ps.executeUpdate();
                    System.out.println("YOUR REQUEST SUCCESSFULLY ADDED TO THE DATABASE");
                });
    }


    /**
     * Deletes all user requests where user_ip equals ip which this DatabaseManager was initialized with.
     * If successful, prints to {@code System.out} success message.
     * @throws SQLException if SQL error occurs during the connection or communication with db server
     */
    public void deleteAllUserRequests() throws SQLException {
        executeSql("DELETE FROM user_requests WHERE user_ip = ?;",
                (ps) -> {
                    ps.setString(1, ip);
                    ps.executeUpdate();
                    System.out.println("ALL YOUR REQUESTS SUCCESSFULLY DELETED FROM THE DATABASE");
                });
    }


    /**
     * Prints to {@code System.out} information about all requests except user_ip where ip equals to
     * this.ip in order these requests were added. After this prints total count of this user requests.
     * @throws SQLException if SQL error occurs during the connection or communication with db server
     */
    public void showAllUserRequests() throws SQLException {
        executeSql("SELECT * FROM user_requests WHERE user_ip = ? ORDER BY id ASC;",
                (ps) -> {
                    ps.setString(1, ip);
                    try (ResultSet rs = ps.executeQuery()) {
                        int count = 0;
                        while (rs.next()) {
                            count++;
                            showTableRow(rs);
                        }
                        System.out.println("TOTAL: " + count);
                    }
                });
    }


    /**
     * Prints to {@code System.out} information about last request in add-order where user_ip equals to this.ip
     * without printing user_ip. If such request doesn't exist (this user hasn't any requests) prints message:
     * "YOU HAVE NOT ANY REQUESTS YET"
     * @throws SQLException if SQL error occurs during the connection or communication with db server
     */
    public void showLastUserRequest() throws SQLException {
        executeSql("SELECT * FROM user_requests WHERE user_ip = ? ORDER BY id DESC LIMIT 1;",
                (ps) -> {
                    ps.setString(1, ip);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            showTableRow(rs);
                        } else {
                            System.out.println("YOU HAVE NOT ANY REQUESTS YET");
                        }
                    }
                });
    }

}
