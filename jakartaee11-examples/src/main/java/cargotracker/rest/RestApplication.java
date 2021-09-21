package cargotracker.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

// OpenAPI document: /openapi   Swagger UI: /openapi/ui
@ApplicationPath("/rest")
@OpenAPIDefinition(info = @Info(title = "Cargo Tracker", version = "3.1.0",
        description = "Jakarta EE 11 + MicroProfile 7.1 reference application"))
@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class RestApplication extends Application {
}
