package org.digitalnao.util;

import org.jdbi.v3.core.Jdbi;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase utilitaria para ejecutar scripts SQL externos.
 * Ejemplo de uso:
 * DatabaseSeeder.run(jdbi, "src/main/resources/sql/seed-items.sql");
 */
public class DatabaseSeeder {

    /**
     * Ejecuta un archivo .sql utilizando un objeto Jdbi.
     *
     * @param jdbi    instancia activa de Jdbi
     * @param sqlPath ruta del archivo .sql a ejecutar
     */
    public static void run(Jdbi jdbi, String sqlPath) {
        try {
            String sql = Files.readString(Path.of(sqlPath));
            jdbi.useHandle(handle -> handle.createScript(sql).execute());
            System.out.println("✅ Script ejecutado correctamente: " + sqlPath);
        } catch (Exception e) {
            System.err.println("⚠️ Error al ejecutar el script SQL (" + sqlPath + "): " + e.getMessage());
        }
    }
}
