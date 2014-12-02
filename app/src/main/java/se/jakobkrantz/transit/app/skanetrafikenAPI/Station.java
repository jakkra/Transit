package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-18.
 */

public class Station {
    private String stationName;
    private int stationId;
    private double latitude;
    private double longitude;
    private String type;
    private String timeSearched;

    public Station(String stationName, int stationId, double latitude, double longitude, String type) {
        this.stationName = stationName;
        this.stationId = stationId;
        this.latitude = latitude; //https://github.com/goober/coordinate-transformation-library
        this.longitude = longitude; // RT90 format if needed use this library to change to long/lat
        this.type = type;
    }

    public Station() {

    }

    public String toString() {
        return stationName;
    }

    public boolean equals(Object o) {
        return o instanceof Station && stationId == ((Station) o).getStationId();
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimeSearched(String timeSearched) {
        this.timeSearched = timeSearched;
    }

    public String getTimeSearched() {
        return timeSearched;
    }

}
