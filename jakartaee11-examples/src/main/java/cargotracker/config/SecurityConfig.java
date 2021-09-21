package cargotracker.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.identitystore.InMemoryIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.InMemoryIdentityStoreDefinition.Credentials;

// Jakarta Security 4.0 (EE 11) application-defined security, no server.xml registry.
//
// @InMemoryIdentityStoreDefinition is new in 4.0 and meant for demos and tests:
// replace it with @DatabaseIdentityStoreDefinition / @LdapIdentityStoreDefinition
// (or OpenID Connect) in production. Passwords here are demo-only test values.
//
// Only resources that declare constraints (e.g. @RolesAllowed on a JAX-RS method)
// ask for credentials; the Faces UI stays open so the demo is easy to explore.
// Open Liberty maps a group to the role of the same name, so no application-bnd
// is needed in server.xml.
@ApplicationScoped
@DeclareRoles({Roles.HANDLER, Roles.ADMIN})
@BasicAuthenticationMechanismDefinition(realmName = "cargo-tracker")
@InMemoryIdentityStoreDefinition({
        @Credentials(callerName = "handler", password = "handler", groups = Roles.HANDLER),
        @Credentials(callerName = "admin", password = "admin", groups = {Roles.ADMIN, Roles.HANDLER})
})
public class SecurityConfig {
}
