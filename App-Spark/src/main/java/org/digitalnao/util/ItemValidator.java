package org.digitalnao.util;

import org.digitalnao.model.Item;
import org.digitalnao.model.error.ErrorResponse;

/**
 * Clase que centraliza la validación de datos del modelo Item.
 * Devuelve un ErrorResponse si encuentra errores, o null si todo es válido.
 */
public class ItemValidator {

    /**
     * Valida los campos del item.
     *
     * @param item objeto Item a validar
     * @return ErrorResponse si hay error, o null si es válido
     */
    public static ErrorResponse validate(Item item) {
        if (item == null) {
            return new ErrorResponse("El objeto item no puede ser nulo");
        }

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            return new ErrorResponse("El nombre del item es requerido");
        }

        return null; // ✅ Si todo está correcto
    }
}
