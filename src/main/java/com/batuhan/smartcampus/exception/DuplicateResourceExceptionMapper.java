package com.batuhan.smartcampus.exception;

import com.batuhan.smartcampus.model.ApiError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DuplicateResourceExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

    @Override
    public Response toResponse(DuplicateResourceException exception) {
        ApiError error = new ApiError(
                409,
                "Conflict",
                exception.getMessage()
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .build();
    }
}