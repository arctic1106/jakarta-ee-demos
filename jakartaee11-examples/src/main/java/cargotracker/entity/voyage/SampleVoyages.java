package cargotracker.entity.voyage;

import java.time.LocalDateTime;

import cargotracker.entity.location.SampleLocations;

public class SampleVoyages {
    public static final Voyage HONGKONG_TO_NEW_YORK = new Voyage.Builder("0100S", SampleLocations.HONGKONG)
            .addMovement(SampleLocations.HANGZOU,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(1).plusHours(12),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(3).plusHours(14)
                            .plusMinutes(30))
            .addMovement(SampleLocations.TOKYO,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(4).plusHours(21),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(6).plusHours(6)
                            .plusMinutes(15))
            .addMovement(SampleLocations.MELBOURNE,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(9).plusHours(11),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(12).plusHours(11)
                            .plusMinutes(30))
            .addMovement(SampleLocations.NEWYORK,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(14).plusHours(12),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(23).plusHours(23)
                            .plusMinutes(10))
            .build();

    public static final Voyage NEW_YORK_TO_DALLAS = new Voyage.Builder("0200T", SampleLocations.NEWYORK)
            .addMovement(SampleLocations.CHICAGO,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(24).plusHours(7),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(24).plusHours(17)
                            .plusMinutes(45))
            .addMovement(SampleLocations.DALLAS,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(24).plusHours(21)
                            .plusMinutes(25),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(25).plusHours(19)
                            .plusMinutes(30))
            .build();

    public static final Voyage DALLAS_TO_HELSINKI = new Voyage.Builder("0300A", SampleLocations.DALLAS)
            .addMovement(SampleLocations.HAMBURG,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(29).plusHours(3)
                            .plusMinutes(30),
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(31).plusHours(14))
            .addMovement(SampleLocations.STOCKHOLM,
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(1).plusHours(15)
                            .plusMinutes(20),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(1).plusHours(18)
                            .plusMinutes(40))
            .addMovement(SampleLocations.HELSINKI,
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(2).plusHours(9),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(2).plusHours(11)
                            .plusMinutes(15))
            .build();

    public static final Voyage DALLAS_TO_HELSINKI_ALT = new Voyage.Builder("0301S", SampleLocations.DALLAS)
            .addMovement(SampleLocations.HELSINKI,
                    LocalDateTime.now().minusYears(1).plusMonths(10).plusDays(29).plusHours(3)
                            .plusMinutes(30),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(5).plusHours(15)
                            .plusMinutes(45))
            .build();

    public static final Voyage HELSINKI_TO_HONGKONG = new Voyage.Builder("0400S", SampleLocations.HELSINKI)
            .addMovement(SampleLocations.ROTTERDAM,
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(4).plusHours(5)
                            .plusMinutes(50),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(6).plusHours(14)
                            .plusMinutes(10))
            .addMovement(SampleLocations.SHANGHAI,
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(10).plusHours(21)
                            .plusMinutes(45),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(22).plusHours(16)
                            .plusMinutes(40))
            .addMovement(SampleLocations.HONGKONG,
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(24).plusHours(7),
                    LocalDateTime.now().minusYears(1).plusMonths(11).plusDays(28).plusHours(13)
                            .plusMinutes(37))
            .build();

    private SampleVoyages() {
    }
}
