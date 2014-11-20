package se.jakobkrantz.transit.app.database;/*
 * Created by krantz on 14-11-20.
 */

import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

public class SimpleJourney {

    private final Station fromStation;
    private final Station toStation;

    public SimpleJourney(Station fromStation, Station toStation){
        this.fromStation = fromStation;
        this.toStation = toStation;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public Station getToStation() {
        return toStation;
    }
}
