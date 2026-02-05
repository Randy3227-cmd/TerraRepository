package tools;

import java.sql.Connection;
import java.sql.DriverManager;

public class Db {

    private static final String URL = ConfigLoader.get("db.url");
    private static final String USER = ConfigLoader.get("db.username");
    private static final String PASSWORD = ConfigLoader.get("db.password");
    private static final String DRIVER = ConfigLoader.get("db.driver");

    static {
        try {
            Class.forName(DRIVER);
        } catch (Exception e) {
            throw new RuntimeException("Driver loading failed", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
