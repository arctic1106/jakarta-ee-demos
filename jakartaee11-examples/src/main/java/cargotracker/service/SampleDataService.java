package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import cargotracker.entity.ApplicationSettings;
import cargotracker.entity.cargo.*;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.repository.HandlingEventRepository;
import cargotracker.entity.location.SampleLocations;
import cargotracker.entity.voyage.SampleVoyages;

@ApplicationScoped
public class SampleDataService {
    @Inject
    private Logger logger;
    @Inject
    private HandlingEventService handlingEventService;
    @Inject
    private HandlingEventRepository handlingEventRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void loadSampleData() {
        if (isSampleLoaded()) {
            logger.info("Sample data already loaded, skipping.");
            return;
        }
        logger.info("Loading sample data.");
        loadSampleLocations();
        loadSampleVoyages();
        // Jakarta Data repositories (used by HandlingEventService) run their own
        // queries against the database: flush so they can see the rows we just
        // persisted in this still-open transaction.
        entityManager.flush();
        loadSampleCargos();
        logger.info("Sample data loaded.");
    }

    private boolean isSampleLoaded() {
        ApplicationSettings settings = entityManager.find(ApplicationSettings.class, 1L,
                LockModeType.PESSIMISTIC_WRITE);
        if (settings == null) {
            throw new IllegalStateException("Application settings row missing; check initial-data.sql.");
        }
        boolean sampleLoaded = settings.isSampleLoaded();
        settings.setSampleLoaded(true);
        return sampleLoaded;
    }

    private void loadSampleLocations() {
        logger.info("Loading sample locations.");
        entityManager.persist(SampleLocations.HONGKONG);
        entityManager.persist(SampleLocations.MELBOURNE);
        entityManager.persist(SampleLocations.STOCKHOLM);
        entityManager.persist(SampleLocations.HELSINKI);
        entityManager.persist(SampleLocations.CHICAGO);
        entityManager.persist(SampleLocations.TOKYO);
        entityManager.persist(SampleLocations.HAMBURG);
        entityManager.persist(SampleLocations.SHANGHAI);
        entityManager.persist(SampleLocations.ROTTERDAM);
        entityManager.persist(SampleLocations.GOTHENBURG);
        entityManager.persist(SampleLocations.HANGZOU);
        entityManager.persist(SampleLocations.NEWYORK);
        entityManager.persist(SampleLocations.DALLAS);
    }

    private void loadSampleVoyages() {
        logger.info("Loading sample voyages.");
        entityManager.persist(SampleVoyages.HONGKONG_TO_NEW_YORK);
        entityManager.persist(SampleVoyages.NEW_YORK_TO_DALLAS);
        entityManager.persist(SampleVoyages.DALLAS_TO_HELSINKI);
        entityManager.persist(SampleVoyages.HELSINKI_TO_HONGKONG);
        entityManager.persist(SampleVoyages.DALLAS_TO_HELSINKI_ALT);
    }

    private void loadSampleCargos() {
        logger.info("Loading sample cargo data.");
        var trackingId1 = new TrackingId("ABC123");
        var routeSpecification1 = new RouteSpecification(SampleLocations.HONGKONG, SampleLocations.HELSINKI,
                LocalDate.now().plusDays(15));
        var abc123 = new Cargo(trackingId1, routeSpecification1);
        var itinerary1 = new Itinerary(Arrays.asList(
                new Leg(
                        SampleVoyages.HONGKONG_TO_NEW_YORK,
                        SampleLocations.HONGKONG,
                        SampleLocations.NEWYORK,
                        LocalDateTime.now().minusDays(7).minusHours(2).minusMinutes(4),
                        LocalDateTime.now().minusDays(1).minusHours(10).minusMinutes(52)),
                new Leg(
                        SampleVoyages.NEW_YORK_TO_DALLAS,
                        SampleLocations.NEWYORK,
                        SampleLocations.DALLAS,
                        LocalDateTime.now().plusDays(2).minusHours(18).minusMinutes(38),
                        LocalDateTime.now().plusDays(6).minusHours(3).minusMinutes(22)),
                new Leg(
                        SampleVoyages.DALLAS_TO_HELSINKI,
                        SampleLocations.DALLAS,
                        SampleLocations.HELSINKI,
                        LocalDateTime.now().plusDays(8).minusHours(8).minusMinutes(18),
                        LocalDateTime.now().plusDays(14).minusHours(12).minusMinutes(29))));
        abc123.assignToRoute(itinerary1);
        entityManager.persist(abc123);
        var event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(5).minusMinutes(5),
                LocalDateTime.now().minusDays(10).minusHours(9).minusMinutes(11),
                trackingId1, null,
                SampleLocations.HONGKONG.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        var event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(6).minusMinutes(46),
                LocalDateTime.now().minusDays(7).minusHours(2).minusMinutes(50),
                trackingId1,
                SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                SampleLocations.HONGKONG.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        var event3 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(15).minusMinutes(26),
                LocalDateTime.now().minusDays(1).minusHours(4).minusMinutes(32),
                trackingId1,
                SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                SampleLocations.NEWYORK.getUnLoCode(),
                HandlingEvent.Type.UNLOAD);
        entityManager.persist(event3);
        var handlingHistory1 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId1);
        abc123.deriveDeliveryProgress(handlingHistory1);
        entityManager.persist(abc123);

        var trackingId2 = new TrackingId("JKL567");
        var routeSpecification2 = new RouteSpecification(SampleLocations.HANGZOU, SampleLocations.STOCKHOLM,
                LocalDate.now().plusDays(18));
        var jkl567 = new Cargo(trackingId2, routeSpecification2);
        Itinerary itinerary2 = new Itinerary(Arrays.asList(
                new Leg(
                        SampleVoyages.HONGKONG_TO_NEW_YORK,
                        SampleLocations.HANGZOU,
                        SampleLocations.NEWYORK,
                        LocalDateTime.now().minusDays(10).minusHours(1).minusMinutes(19),
                        LocalDateTime.now().minusDays(3).minusHours(7).minusMinutes(2)),
                new Leg(
                        SampleVoyages.NEW_YORK_TO_DALLAS,
                        SampleLocations.NEWYORK,
                        SampleLocations.DALLAS,
                        LocalDateTime.now().minusDays(2).minusHours(17).minusMinutes(3),
                        LocalDateTime.now().plusDays(2).minusHours(5).minusMinutes(20)),
                new Leg(
                        SampleVoyages.DALLAS_TO_HELSINKI,
                        SampleLocations.DALLAS,
                        SampleLocations.STOCKHOLM,
                        LocalDateTime.now().plusDays(6).minusHours(11).minusMinutes(38),
                        LocalDateTime.now().plusDays(15).minusHours(4).minusMinutes(1))));
        jkl567.assignToRoute(itinerary2);
        entityManager.persist(jkl567);

        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(2).minusMinutes(44),
                LocalDateTime.now().minusDays(15).minusHours(14).minusMinutes(33),
                trackingId2, null,
                SampleLocations.HANGZOU.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(3).minusMinutes(12),
                LocalDateTime.now().minusDays(10).minusHours(3).minusMinutes(17),
                trackingId2,
                SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                SampleLocations.HANGZOU.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        event3 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(3).minusMinutes(21),
                LocalDateTime.now().minusDays(3).minusHours(22).minusMinutes(1),
                trackingId2,
                SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                SampleLocations.NEWYORK.getUnLoCode(),
                HandlingEvent.Type.UNLOAD);
        entityManager.persist(event3);
        var event4 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(18).minusMinutes(36),
                LocalDateTime.now().minusDays(2).minusHours(8).minusMinutes(7),
                trackingId2,
                SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                SampleLocations.NEWYORK.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event4);

        var handlingHistory2 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId2);
        jkl567.deriveDeliveryProgress(handlingHistory2);
        entityManager.persist(jkl567);

        var trackingId3 = new TrackingId("DEF789");
        var routeSpecification3 = new RouteSpecification(SampleLocations.HONGKONG, SampleLocations.MELBOURNE,
                LocalDate.now().plusMonths(2));
        var def789 = new Cargo(trackingId3, routeSpecification3);
        entityManager.persist(def789);

        var trackingId4 = new TrackingId("MNO456");
        var routeSpecification4 = new RouteSpecification(SampleLocations.NEWYORK, SampleLocations.DALLAS,
                LocalDate.now().minusDays(24));
        var mno456 = new Cargo(trackingId4, routeSpecification4);
        var itinerary4 = new Itinerary(List.of(
                new Leg(
                        SampleVoyages.NEW_YORK_TO_DALLAS,
                        SampleLocations.NEWYORK,
                        SampleLocations.DALLAS,
                        LocalDateTime.now().minusDays(34).minusHours(1).minusMinutes(1),
                        LocalDateTime.now().minusDays(28).minusHours(10).minusMinutes(1))));
        mno456.assignToRoute(itinerary4);
        entityManager.persist(mno456);

        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(16).minusMinutes(9),
                LocalDateTime.now().minusDays(37).minusHours(2).minusMinutes(21),
                trackingId4, null,
                SampleLocations.NEWYORK.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(3).minusMinutes(9),
                LocalDateTime.now().minusDays(34).minusHours(3).minusMinutes(8),
                trackingId4,
                SampleVoyages.NEW_YORK_TO_DALLAS.getVoyageNumber(),
                SampleLocations.NEWYORK.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        event3 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(14).minusMinutes(10),
                LocalDateTime.now().minusDays(28).minusHours(7).minusMinutes(7),
                trackingId4,
                SampleVoyages.NEW_YORK_TO_DALLAS.getVoyageNumber(),
                SampleLocations.DALLAS.getUnLoCode(),
                HandlingEvent.Type.UNLOAD);
        entityManager.persist(event3);
        event4 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(22).minusMinutes(1),
                LocalDateTime.now().minusDays(27).minusHours(18).minusMinutes(3),
                trackingId4, null,
                SampleLocations.DALLAS.getUnLoCode(),
                HandlingEvent.Type.CUSTOMS);
        entityManager.persist(event4);
        var event5 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(9).minusMinutes(30),
                LocalDateTime.now().minusDays(26).minusHours(14).minusMinutes(15),
                trackingId4, null,
                SampleLocations.DALLAS.getUnLoCode(),
                HandlingEvent.Type.CLAIM);
        entityManager.persist(event5);
        var handlingHistory3 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId4);
        mno456.deriveDeliveryProgress(handlingHistory3);
        entityManager.persist(mno456);

        // Cargo 5: Tokyo → Hamburg, IN_PORT at origin (just received, not yet loaded)
        var trackingId5 = new TrackingId("GHI123");
        var routeSpecification5 = new RouteSpecification(SampleLocations.TOKYO, SampleLocations.HAMBURG,
                LocalDate.now().plusDays(30));
        var ghi123 = new Cargo(trackingId5, routeSpecification5);
        var itinerary5 = new Itinerary(List.of(
                new Leg(
                        SampleVoyages.HELSINKI_TO_HONGKONG,
                        SampleLocations.TOKYO,
                        SampleLocations.HAMBURG,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(8))));
        ghi123.assignToRoute(itinerary5);
        entityManager.persist(ghi123);

        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusHours(2),
                trackingId5, null,
                SampleLocations.TOKYO.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        var handlingHistory5 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId5);
        ghi123.deriveDeliveryProgress(handlingHistory5);
        entityManager.persist(ghi123);

        // Cargo 6: Shanghai → Chicago, ONBOARD_CARRIER at sea (loaded, in transit)
        var trackingId6 = new TrackingId("PQR890");
        var routeSpecification6 = new RouteSpecification(SampleLocations.SHANGHAI, SampleLocations.CHICAGO,
                LocalDate.now().plusDays(25));
        var pqr890 = new Cargo(trackingId6, routeSpecification6);
        var itinerary6 = new Itinerary(Arrays.asList(
                new Leg(
                        SampleVoyages.HELSINKI_TO_HONGKONG,
                        SampleLocations.SHANGHAI,
                        SampleLocations.ROTTERDAM,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().plusDays(3)),
                new Leg(
                        SampleVoyages.DALLAS_TO_HELSINKI,
                        SampleLocations.ROTTERDAM,
                        SampleLocations.CHICAGO,
                        LocalDateTime.now().plusDays(4),
                        LocalDateTime.now().plusDays(12))));
        pqr890.assignToRoute(itinerary6);
        entityManager.persist(pqr890);

        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(7).minusHours(6),
                LocalDateTime.now().minusDays(8),
                trackingId6, null,
                SampleLocations.SHANGHAI.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(5).minusHours(2),
                LocalDateTime.now().minusDays(5),
                trackingId6,
                SampleVoyages.HELSINKI_TO_HONGKONG.getVoyageNumber(),
                SampleLocations.SHANGHAI.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        var handlingHistory6 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId6);
        pqr890.deriveDeliveryProgress(handlingHistory6);
        entityManager.persist(pqr890);

        // Cargo 7: Melbourne → Gothenburg, AT_DESTINATION (unloaded at final
        // destination, not claimed)
        var trackingId7 = new TrackingId("UVW345");
        var routeSpecification7 = new RouteSpecification(SampleLocations.MELBOURNE, SampleLocations.GOTHENBURG,
                LocalDate.now().plusDays(5));
        var uvw345 = new Cargo(trackingId7, routeSpecification7);
        var itinerary7 = new Itinerary(List.of(
                new Leg(
                        SampleVoyages.DALLAS_TO_HELSINKI,
                        SampleLocations.MELBOURNE,
                        SampleLocations.GOTHENBURG,
                        LocalDateTime.now().minusDays(15),
                        LocalDateTime.now().minusDays(2))));
        uvw345.assignToRoute(itinerary7);
        entityManager.persist(uvw345);

        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(18).minusHours(4),
                LocalDateTime.now().minusDays(18),
                trackingId7, null,
                SampleLocations.MELBOURNE.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(15).minusHours(1),
                LocalDateTime.now().minusDays(15),
                trackingId7,
                SampleVoyages.DALLAS_TO_HELSINKI.getVoyageNumber(),
                SampleLocations.MELBOURNE.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        event3 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(2).minusHours(3),
                LocalDateTime.now().minusDays(2),
                trackingId7,
                SampleVoyages.DALLAS_TO_HELSINKI.getVoyageNumber(),
                SampleLocations.GOTHENBURG.getUnLoCode(),
                HandlingEvent.Type.UNLOAD);
        entityManager.persist(event3);
        var handlingHistory7 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId7);
        uvw345.deriveDeliveryProgress(handlingHistory7);
        entityManager.persist(uvw345);

        // Cargo 8: Dallas → Helsinki, MISROUTED (assigned wrong itinerary, cargo sent
        // to wrong port)
        var trackingId8 = new TrackingId("XYZ789");
        var routeSpecification8 = new RouteSpecification(SampleLocations.DALLAS, SampleLocations.HELSINKI,
                LocalDate.now().plusDays(20));
        var xyz789 = new Cargo(trackingId8, routeSpecification8);
        var itinerary8 = new Itinerary(Arrays.asList(
                new Leg(
                        SampleVoyages.NEW_YORK_TO_DALLAS,
                        SampleLocations.DALLAS,
                        SampleLocations.NEWYORK,
                        LocalDateTime.now().minusDays(10),
                        LocalDateTime.now().minusDays(6)),
                new Leg(
                        SampleVoyages.HONGKONG_TO_NEW_YORK,
                        SampleLocations.NEWYORK,
                        SampleLocations.HELSINKI,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().plusDays(3))));
        xyz789.assignToRoute(itinerary8);
        entityManager.persist(xyz789);

        // Received at Dallas
        event1 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(12).minusHours(3),
                LocalDateTime.now().minusDays(12),
                trackingId8, null,
                SampleLocations.DALLAS.getUnLoCode(),
                HandlingEvent.Type.RECEIVE);
        entityManager.persist(event1);
        // Loaded onto correct first leg
        event2 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(10).minusHours(1),
                LocalDateTime.now().minusDays(10),
                trackingId8,
                SampleVoyages.NEW_YORK_TO_DALLAS.getVoyageNumber(),
                SampleLocations.DALLAS.getUnLoCode(),
                HandlingEvent.Type.LOAD);
        entityManager.persist(event2);
        // But unloaded at WRONG port (Chicago instead of New York) - misdirected!
        event3 = handlingEventService.createEvent(
                LocalDateTime.now().minusDays(6).minusHours(2),
                LocalDateTime.now().minusDays(6),
                trackingId8,
                SampleVoyages.NEW_YORK_TO_DALLAS.getVoyageNumber(),
                SampleLocations.CHICAGO.getUnLoCode(),
                HandlingEvent.Type.UNLOAD);
        entityManager.persist(event3);
        var handlingHistory8 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId8);
        xyz789.deriveDeliveryProgress(handlingHistory8);
        entityManager.persist(xyz789);

    }
}
