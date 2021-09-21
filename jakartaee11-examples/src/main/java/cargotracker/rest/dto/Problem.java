package cargotracker.rest.dto;

// RFC 9457 "problem details" body, served as application/problem+json.
public record Problem(String type, String title, int status, String detail) {

    public static final String MEDIA_TYPE = "application/problem+json";

    public static Problem of(int status, String title, String detail) {
        return new Problem("about:blank", title, status, detail);
    }
}
