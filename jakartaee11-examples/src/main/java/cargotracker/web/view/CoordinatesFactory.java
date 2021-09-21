package cargotracker.web.view;

import static cargotracker.entity.location.Location.UNKNOWN;
import static cargotracker.entity.location.SampleLocations.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cargotracker.entity.location.Location;
import cargotracker.entity.location.UnLoCode;

public class CoordinatesFactory {
    private static final Map<String, Coordinates> COORDINATES_MAP;

    static {
        Map<String, Coordinates> map = new HashMap<>();
        map.put(HONGKONG.getUnLoCode().getCode(), new Coordinates(22, 114));
        map.put(MELBOURNE.getUnLoCode().getCode(), new Coordinates(-38, 145));
        map.put(STOCKHOLM.getUnLoCode().getCode(), new Coordinates(59, 18));
        map.put(HELSINKI.getUnLoCode().getCode(), new Coordinates(60, 25));
        map.put(CHICAGO.getUnLoCode().getCode(), new Coordinates(42, -88));
        map.put(TOKYO.getUnLoCode().getCode(), new Coordinates(36, 140));
        map.put(HAMBURG.getUnLoCode().getCode(), new Coordinates(54, 10));
        map.put(SHANGHAI.getUnLoCode().getCode(), new Coordinates(31, 121));
        map.put(ROTTERDAM.getUnLoCode().getCode(), new Coordinates(52, 5));
        map.put(GOTHENBURG.getUnLoCode().getCode(), new Coordinates(58, 12));
        map.put(HANGZOU.getUnLoCode().getCode(), new Coordinates(30, 120));
        map.put(NEWYORK.getUnLoCode().getCode(), new Coordinates(41, -74));
        map.put(DALLAS.getUnLoCode().getCode(), new Coordinates(33, -97));
        map.put(UNKNOWN.getUnLoCode().getCode(), new Coordinates(-90, 0)); // The South Pole.
        COORDINATES_MAP = Collections.unmodifiableMap(map);
    }

    private CoordinatesFactory() {
    }

    public static Coordinates find(Location location) {
        return find(location.getUnLoCode());
    }

    public static Coordinates find(UnLoCode unLoCode) {
        return find(unLoCode.getCode());
    }

    public static Coordinates find(String unLoCode) {
        return COORDINATES_MAP.get(unLoCode);
    }
}
