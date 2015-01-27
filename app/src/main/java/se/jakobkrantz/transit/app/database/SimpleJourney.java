package se.jakobkrantz.transit.app.database;/*
 * Created by krantz on 14-11-20.
 */

import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

public class SimpleJourney {

    private Station fromStation;
    private Station toStation;

    public SimpleJourney(Station fromStation, Station toStation) {
        this.fromStation = fromStation;
        this.toStation = toStation;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public Station getToStation() {
        return toStation;
    }

    public String toString() {
        return fromStation + " -> " + toStation;
    }

    public void setFromStation(Station s) {
        this.fromStation = s;
    }

    public void setToStation(Station toStation) {
        this.toStation = toStation;
    }
}
