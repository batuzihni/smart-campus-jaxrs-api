package com.batuhan.smartcampus.exception;

import com.batuhan.smartcampus.model.ApiError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        ApiError error = new ApiError(
                500,
                "Internal Server Error",
                "An unexpected server error occurred."
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}