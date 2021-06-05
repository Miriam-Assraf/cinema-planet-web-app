package backend.cinemaplanet.view;

public class LocationBoundary {
    private String lat;
    private String lng;

    public LocationBoundary(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Default constructor
    public LocationBoundary() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "LocationBoundary [lat=" + lat + ", lng=" + lng + "]";
    }

}
