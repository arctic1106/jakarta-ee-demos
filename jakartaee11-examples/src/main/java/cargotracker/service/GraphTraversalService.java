package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import cargotracker.pathfinder.TransitEdge;
import cargotracker.pathfinder.TransitPath;

// Stand-in for an external routing system (the "path finder" bounded context).
// It is a plain JAX-RS resource; RoutingService calls it in-process.
@ApplicationScoped
@Path("/graph-traversal")
public class GraphTraversalService {
    private static final long ONE_MIN_MS = 1000 * 60;
    private static final long ONE_DAY_MS = ONE_MIN_MS * 60 * 24;
    private static final String UNLOCODE_PATTERN_VIOLATION_MESSAGE = """
            UN location code value must be five characters long, the first two must be alphabetic and the last three must be alphanumeric (excluding 0 and 1).""";
    private static final List<String> LOCATIONS = List.of(
            "CNHKG", "AUMEL", "SESTO", "FIHEL", "USCHI", "JNTKO", "DEHAM", "CNSHA", "NLRTM", "SEGOT", "CNHGH", "USNYC",
            "USDAL");
    @Inject
    @ConfigProperty(name = "cargo.pathfinder.max-candidates", defaultValue = "5")
    private int maxCandidates;

    private static final List<String> VOYAGE_NUMBERS = List.of("0100S", "0200T", "0300A", "0301S", "0400S");

    @GET
    @Path("/shortest-path")
    @Produces("application/json")
    public List<TransitPath> findShortestPath(
            @NotNull(message = "Missing origin UN location code.") @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}", message = "Origin "
                    + UNLOCODE_PATTERN_VIOLATION_MESSAGE) @QueryParam("origin") String originUnLoCode,
            @NotNull(message = "Missing destination UN location code.") @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}", message = "Destination "
                    + UNLOCODE_PATTERN_VIOLATION_MESSAGE) @QueryParam("destination") String destinationUnLoCode,
            @Size(min = 8, max = 8, message = "Deadline value must be eight characters long.") @QueryParam("deadline") String deadline) {
        List<String> allVertices = new ArrayList<>(LOCATIONS);
        allVertices.remove(originUnLoCode);
        allVertices.remove(destinationUnLoCode);

        int candidateCount = getRandomNumberOfCandidates();
        List<TransitPath> candidates = new ArrayList<>(candidateCount);

        for (int i = 0; i < candidateCount; i++) {
            allVertices = getRandomChunkOfLocations(allVertices);
            List<TransitEdge> transitEdges = new ArrayList<>(allVertices.size() - 1);
            var fromUnLoCode = originUnLoCode;
            LocalDateTime date = LocalDateTime.now();

            for (int j = 0; j <= allVertices.size(); ++j) {
                LocalDateTime fromDate = nextDate(date);
                LocalDateTime toDate = nextDate(fromDate);
                String toUnLoCode = (j >= allVertices.size() ? destinationUnLoCode : allVertices.get(j));
                transitEdges.add(new TransitEdge(
                        VOYAGE_NUMBERS.get(ThreadLocalRandom.current().nextInt(VOYAGE_NUMBERS.size())),
                        fromUnLoCode,
                        toUnLoCode,
                        fromDate,
                        toDate));
                fromUnLoCode = toUnLoCode;
                date = nextDate(toDate);
            }
            candidates.add(new TransitPath(transitEdges));
        }
        return candidates;
    }

    private LocalDateTime nextDate(LocalDateTime date) {
        return date.plus(ONE_DAY_MS + (ThreadLocalRandom.current().nextInt(1000) - 500) * ONE_MIN_MS, ChronoUnit.MILLIS);
    }

    private int getRandomNumberOfCandidates() {
        return 1 + ThreadLocalRandom.current().nextInt(Math.max(1, maxCandidates));
    }

    private List<String> getRandomChunkOfLocations(List<String> allLocations) {
        Collections.shuffle(allLocations, ThreadLocalRandom.current());
        int total = allLocations.size();
        int chunk = total > 4 ? 1 + ThreadLocalRandom.current().nextInt(5) : total;
        return allLocations.subList(0, chunk);
    }
}
