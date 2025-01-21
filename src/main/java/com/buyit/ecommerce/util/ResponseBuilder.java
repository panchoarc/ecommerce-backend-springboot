package com.buyit.ecommerce.util;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.Map;


public class ResponseBuilder {

    private ResponseBuilder() {
    }

    /**
     * Método para construir una respuesta exitosa con datos y paginación.
     *
     * @param <T>        Tipo de los datos.
     * @param message    Mensaje de la respuesta.
     * @param data       Datos exitosos (genérico).
     * @param pagination Información de paginación (objeto Pagination).
     * @return ApiResponse con datos y paginación.
     */
    public static <T> ApiResponse<T> successPaginated(String message, T data, Pagination pagination) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.SUCCESS);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        if (pagination != null) {
            apiResponse.setPagination(pagination);
        }
        return apiResponse;
    }

    /**
     * Método para construir una respuesta exitosa sin paginación.
     *
     * @param <T>     Tipo de los datos.
     * @param message Mensaje de la respuesta.
     * @param data    Datos exitosos (genérico).
     * @return ApiResponse con datos.
     */
    public static <T> ApiResponse<T> success(String message, T data) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.SUCCESS);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    /**
     * Método para manejar errores con un mapa de errores.
     *
     * @param <T>     Tipo de los datos.
     * @param message Mensaje de error.
     * @param errors  Mapa de errores.
     * @return ApiResponse con errores.
     */
    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.ERROR);
        apiResponse.setMessage(message);
        apiResponse.setErrors(errors);
        return apiResponse;
    }

    /**
     * Método para manejar errores con un único error.
     *
     * @param <T>          Tipo de los datos.
     * @param message      Mensaje de error.
     * @param field        Campo con error.
     * @param errorMessage Mensaje de error.
     * @return ApiResponse con un único error.
     */
    public static <T> ApiResponse<T> error(String message, String field, String errorMessage) {

        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponse.Status.ERROR);
        apiResponse.setMessage(message);
        apiResponse.setErrors(Collections.singletonMap(field, errorMessage));
        return apiResponse;
    }

    /**
     * Método utilitario para construir un objeto de paginación a partir de un objeto Page de Spring.
     *
     * @param <T>  Tipo de los elementos de la página.
     * @param page Objeto Page proporcionado por Spring.
     * @return Objeto Pagination con los datos calculados.
     */
    public static <T> Pagination buildPagination(Page<T> page) {

        Pagination pagination = new Pagination();
        pagination.setTotalCount((int) page.getTotalElements());
        pagination.setTotalPages((long) page.getTotalPages());
        pagination.setCurrentPage(page.getNumber());
        pagination.setPageSize(page.getSize());

        return pagination;
    }
}
