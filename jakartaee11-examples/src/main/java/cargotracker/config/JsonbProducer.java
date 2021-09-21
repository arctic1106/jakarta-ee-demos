package cargotracker.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

// Jsonb instances are thread-safe and expensive to create: share one.
@ApplicationScoped
public class JsonbProducer {

    @Produces
    @ApplicationScoped
    Jsonb jsonb() {
        return JsonbBuilder.create();
    }

    void close(@Disposes Jsonb jsonb) throws Exception {
        jsonb.close();
    }
}
