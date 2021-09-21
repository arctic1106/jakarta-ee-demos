package cargotracker.web.view;

import cargotracker.entity.location.Location;

public class LocationViewAdapter {
    private final Location location;

    public LocationViewAdapter(Location location) {
        this.location = location;
    }

    public String getUnLocode() {
        return location.getUnLoCode().getCode();
    }

    public String getName() {
        return location.getName();
    }

    public Coordinates getCoordinates() {
        return CoordinatesFactory.find(location);
    }
}
