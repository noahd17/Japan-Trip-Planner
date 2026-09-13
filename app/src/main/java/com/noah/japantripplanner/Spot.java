package com.noah.japantripplanner;

/**
 * A location-tagged place of interest, stored in: users/{uid}/spots/{spotId}
 */
public class Spot {

    private String id;
    private String name;
    private double lat;
    private double lng;
    private long timestamp;

    public Spot() {
    }

    public Spot(String name, double lat, double lng, long timestamp) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
