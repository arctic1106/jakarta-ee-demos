(function (global) {
    "use strict";

    const markerColors = {
        NOT_ROUTED: "grey",
        MISROUTED: "red",
        NOT_RECEIVED: "gold",
        IN_PORT: "green",
        ONBOARD_CARRIER: "green",
        MISDIRECTED: "red",
        AT_DESTINATION: "black",
        CLAIMED: "black",
        UNKNOWN: "red"
    };

    function markerIcon(color) {
        return L.divIcon({
            className: "cargo-marker",
            html: '<span class="cargo-marker-dot cargo-marker-' + color + '"></span>',
            iconSize: [22, 22],
            iconAnchor: [11, 11]
        });
    }

    function createMap(elementId) {
        const map = L.map(elementId).setView({lon: 0, lat: 0}, 2);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution:
                '<a target="_blank" href="https://openstreetmap.org/copyright">' +
                "© OpenStreetMap Contributors</a>"
        }).addTo(map);
        L.control.scale().addTo(map);
        return map;
    }

    function position(location) {
        return {
            lon: location.coordinates.longitude,
            lat: location.coordinates.latitude
        };
    }

    function popupContent(cargo) {
        return "Tracking ID: " + cargo.trackingId + "<br/>" +
            "Routing status: " + cargo.routingStatus + "<br/>" +
            "Misdirected: " + (cargo.misdirected ? "Yes" : "No") + "<br/>" +
            "Transport status: " + cargo.transportStatus + "<br/>" +
            "At destination: " + (cargo.atDestination ? "Yes" : "No") + "<br/>" +
            "Origin: " + cargo.origin.name + "<br/>" +
            "Last known location: " + cargo.lastKnownLocation.name;
    }

    function addMarker(map, location, color, tooltip) {
        return L.marker(position(location), {icon: markerIcon(color)})
            .bindTooltip(tooltip)
            .addTo(map);
    }

    function initialize(options) {
        const map = createMap(options.elementId);
        const status = document.getElementById(options.statusElementId);
        const cargoMarkers = new Map();
        const cargoEventSource = new EventSource(options.cargoUri);

        status.addEventListener("click", function () {
            global.location.reload();
        });
        cargoEventSource.onopen = function () {
            status.textContent = "Online";
        };
        cargoEventSource.onmessage = function (event) {
            const cargo = JSON.parse(event.data);
            const marker = L.marker(position(cargo.location), {
                icon: markerIcon(markerColors[cargo.statusCode] || "red")
            })
                .bindTooltip(popupContent(cargo))
                .on("click", function () {
                    options.onCargoSelected(cargo);
                });

            if (cargoMarkers.has(cargo.trackingId)) {
                cargoMarkers.get(cargo.trackingId).removeFrom(map);
            }
            marker.addTo(map);
            cargoMarkers.set(cargo.trackingId, marker);
            status.textContent = "Updated";
        };
        cargoEventSource.onerror = function () {
            status.textContent = "Connection error";
        };
    }

    async function initializeCargo(options) {
        const response = await fetch(options.cargoUri, {
            headers: {Accept: "application/json"}
        });
        if (!response.ok) {
            throw new Error("Cargo map request failed with status " + response.status);
        }

        const cargo = await response.json();
        const map = createMap(options.elementId);
        const markers = [
            addMarker(map, cargo.origin, "black", "Origin: " + cargo.origin.name),
            addMarker(map, cargo.destination, "black", "Destination: " + cargo.destination.name),
            addMarker(
                map,
                cargo.lastKnownLocation,
                markerColors[cargo.statusCode] || "red",
                "Last known location: " + cargo.lastKnownLocation.name
            )
        ];
        map.fitBounds(L.featureGroup(markers).getBounds().pad(0.2), {maxZoom: 6});
    }

    global.CargoMap = {
        initialize: initialize,
        initializeCargo: initializeCargo
    };
}(window));
