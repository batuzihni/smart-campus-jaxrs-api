package com.batuhan.smartcampus.exception;

import com.batuhan.smartcampus.model.ApiError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    @Override
    public Response toResponse(InvalidInputException exception) {
        ApiError error = new ApiError(
                400,
                "Bad Request",
                exception.getMessage()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}