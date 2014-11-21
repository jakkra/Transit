package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-19.
 */

import java.util.List;

public class Journey {
    private String sequenceNbr; // Position in an ordered list of Journeys
    private String depDateTime; // Departure date and time
    private String arrDateTime; // Arrival date and time
    private String depWalkDist; // Walk distance in m. between starting point for journey (if address or POI) and Stop Area for departure
    private String arrWalkDist; // Walk distance in m. between Stop Area for arrival and journey end point (if address or POI)
    private String nbrOfChanges; // No of change
    private String guaranteed; // Denotes if journey is guaranteed by transport authority, according to rules for "Travel Guarnantee"
    private String CO2Factor; // Journeys impact on the environment - environmental index based on the carbon dioxide (CO2) emissions, Values between 0(lowest impact) and 100
    private String nbrOfZones; // No of passing zones in a zoned fare stucture defined by transport authority
    private String journeyKey; // Used by the Elmer search engine to identify an object uniquely in the scope of a traffic data. Information may be used by back-end services like Map Service to draw itinerary on map.

    private List<RouteLink> routeLinks; // All part distances, if more than one you need to change bus/tran..
    private String distance; // Distance i meters.
    private String CO2value; // CO2 value in kg/person/km


    public void setRouteLinks(List<RouteLink> routeLinks) {
        this.routeLinks = routeLinks;
    }

    public void setSequenceNbr(String sequenceNbr) {
        this.sequenceNbr = sequenceNbr;
    }

    public String getSequenceNbr() {
        return sequenceNbr;
    }

    public void setDepDateTime(String depDateTime) {
        this.depDateTime = depDateTime;
    }

    public String getDepDateTime() {
        return depDateTime;
    }

    public void setArrDateTime(String arrDateTime) {
        this.arrDateTime = arrDateTime;
    }

    public String getArrDateTime() {
        return arrDateTime;
    }

    public void setDepWalkDist(String depWalkDist) {
        this.depWalkDist = depWalkDist;
    }

    public String getDepWalkDist() {
        return depWalkDist;
    }

    public void setArrWalkDist(String arrWalkDist) {
        this.arrWalkDist = arrWalkDist;
    }

    public String getArrWalkDist() {
        return arrWalkDist;
    }

    public void setNbrOfChanges(String nbrOfChanges) {
        this.nbrOfChanges = nbrOfChanges;
    }

    public String getNbrOfChanges() {
        return nbrOfChanges;
    }

    public void setGuaranteed(String guaranteed) {
        this.guaranteed = guaranteed;
    }

    public String getGuaranteed() {
        return guaranteed;
    }

    public void setCO2Factor(String CO2Factor) {
        this.CO2Factor = CO2Factor;
    }

    public String getCO2Factor() {
        return CO2Factor;
    }

    public void setNbrOfZones(String nbrOfZones) {
        this.nbrOfZones = nbrOfZones;
    }

    public String getNbrOfZones() {
        return nbrOfZones;
    }

    public void setJourneyKey(String journeyKey) {
        this.journeyKey = journeyKey;
    }

    public String getJourneyKey() {
        return journeyKey;
    }


    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setCO2value(String CO2value) {
        this.CO2value = CO2value;
    }

    public String getCO2value() {
        return CO2value;
    }

    public Station getStartStation() {
        return routeLinks.get(routeLinks.size() - 1).getFromStation();
    }

    public Station getEndStation() {
        return routeLinks.get(0).getToStation();
    }

    public String toString() {
        return "______Journey_Start_____\n" +
                "Start journey: " + getStartStation() + "\n" +
                "To journey: " + getEndStation() + "\n" +
                "Dep journey: " + getDepDateTime() + "\n" +
                "Arr journey: " + getArrDateTime() + "\n" +
                "Nbr changes: " + getNbrOfChanges() + "\n" +
                "Total distance: " + getDistance() + "\n" +
                "Part routes: " + "\n" + routeLinks.toString() +
                "\n_________Journey Snd________\n";

    }
}
