package org.digitalnao.util;

import org.jdbi.v3.core.Jdbi;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for executing SQL scripts located in the classpath.
 * Example usage:
 * DatabaseSeeder.run(jdbi, "sql/seed-items.sql");
 */
public class DatabaseSeeder {

    /**
     * Executes an SQL script from the classpath using a Jdbi instance.
     *
     * @param jdbi          Active Jdbi instance
     * @param resourcePath  Path inside src/main/resources (e.g., "sql/seed-items.sql")
     */
    public static void run(Jdbi jdbi, String resourcePath) {
        try {
            InputStream inputStream = DatabaseSeeder.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (inputStream == null) {
                throw new RuntimeException("SQL file not found in classpath: " + resourcePath);
            }

            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();

            String upperSql = sql.toUpperCase();
            if (upperSql.contains("DROP ") || upperSql.contains("ALTER ")) {
                throw new SecurityException("The script contains potentially unsafe commands: " + resourcePath);
            }

            jdbi.useHandle(handle -> handle.createScript(sql).execute());

            System.out.println("✅ SQL script executed successfully: " + resourcePath);
        } catch (Exception e) {
            System.err.println("⚠️ Error while executing SQL script (" + resourcePath + "): " + e.getMessage());
        }
    }
}
