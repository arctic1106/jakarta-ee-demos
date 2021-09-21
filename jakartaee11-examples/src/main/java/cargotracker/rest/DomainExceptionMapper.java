package cargotracker.rest;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.NoSuchElementException;

import cargotracker.rest.dto.Problem;

// Translates domain failures into HTTP status codes instead of a generic 500.
// Bean Validation failures on resource parameters are already mapped to 400 by
// Jakarta REST itself.
@Provider
public class DomainExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        var cause = rootCause(exception);
        return switch (cause) {
            case NoSuchElementException e -> problem(Response.Status.NOT_FOUND, "Not found", e.getMessage());
            case IllegalArgumentException e -> problem(Response.Status.BAD_REQUEST, "Invalid request", e.getMessage());
            case OptimisticLockException e -> problem(Response.Status.CONFLICT, "Concurrent update",
                    "The cargo was changed by someone else, retry the request.");
            case jakarta.ws.rs.WebApplicationException e -> e.getResponse();
            default -> problem(Response.Status.INTERNAL_SERVER_ERROR, "Server error", "Unexpected error.");
        };
    }

    private static Throwable rootCause(Throwable failure) {
        // Look through container wrappers (e.g. TransactionalException, PersistenceException).
        var current = failure;
        while (current.getCause() != null && !(current instanceof NoSuchElementException)
                && !(current instanceof IllegalArgumentException) && !(current instanceof OptimisticLockException)
                && !(current instanceof jakarta.ws.rs.WebApplicationException))
            current = current.getCause();
        return current;
    }

    private static Response problem(Response.Status status, String title, String detail) {
        return Response.status(status)
                .type(Problem.MEDIA_TYPE)
                .entity(Problem.of(status.getStatusCode(), title, detail))
                .build();
    }
}
