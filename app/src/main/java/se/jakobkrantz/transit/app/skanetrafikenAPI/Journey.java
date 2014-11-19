package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-19.
 */

public class Journey {
    private final Station startStation;
    private final Station endStation;

    public Journey(Station startStation, Station endStation){

        this.startStation = startStation;
        this.endStation = endStation;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }


}
