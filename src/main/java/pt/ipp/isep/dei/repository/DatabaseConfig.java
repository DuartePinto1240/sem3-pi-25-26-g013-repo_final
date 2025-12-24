package main.java.pt.ipp.isep.dei.repository;

public class DatabaseConfig {
    public static final String URL  = System.getenv().getOrDefault("ORACLE_URL",  "jdbc:oracle:thin:@//localhost:1521/XEPDB1");
    public static final String USER = System.getenv().getOrDefault("ORACLE_USER", "SYSTEM");
    public static final String PASS = System.getenv().getOrDefault("ORACLE_PASS", "salmaofrito123");
}
