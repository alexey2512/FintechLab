package util;

import java.io.*;
import java.util.Properties;

public class PropertiesReader {

    static final String
            JDBC_URL,
            USER,
            PASSWORD,
            GET_IP_URL,
            MEMORY_URL;

    static {
        String jdbcUrl = null, user = null, password = null, getIpUrl = null, memoryUrl = null;
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("props.properties")) {
            properties.load(input);
            jdbcUrl = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
            getIpUrl = properties.getProperty("get_ip.url");
            memoryUrl = properties.getProperty("memory.url");
        } catch (IOException e) {
            System.err.println("Failed to upload properties: " + e.getMessage());
        }
        JDBC_URL = jdbcUrl;
        USER = user;
        PASSWORD = password;
        GET_IP_URL = getIpUrl;
        MEMORY_URL = memoryUrl;
    }

}
