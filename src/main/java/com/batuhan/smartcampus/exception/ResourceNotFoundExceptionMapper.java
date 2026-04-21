package com.batuhan.smartcampus.exception;

import com.batuhan.smartcampus.model.ApiError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        ApiError error = new ApiError(
                404,
                "Not Found",
                exception.getMessage()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}