package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-20.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XMLQueryJourneyHandler extends DefaultHandler {
    String elementValue = null;
    boolean elementOn = false;
    private ArrayList<Journey> journeys;
    private Journey t;
    private Station tempStation;
    private ArrayList<RouteLink> routeLinks;
    private RouteLink r;
    private boolean isOnRouteLinksElement;
    private boolean isOnLineElement;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribute) {
        elementOn = true;
        if (localName.equals("Journeys")) {
            journeys = new ArrayList<Journey>();
        } else if (localName.equals("Journey")) {
            t = new Journey();
        } else if (localName.equals("From")) {
            tempStation = new Station();
        } else if (localName.equals("To")) {
            tempStation = new Station();
        } else if (localName.equals("Line")) {
            isOnLineElement = true;
        } else if (localName.equals("RouteLinks")) {
            routeLinks = new ArrayList<RouteLink>();
            isOnRouteLinksElement = true;
        } else if (localName.equals("RouteLink")) {
            r = new RouteLink();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        elementOn = false;
        //Journey
        if (localName.equals("SequenceNo")) {
            t.setSequenceNbr(elementValue);
        } else if (localName.equals("DepDateTime") && !isOnRouteLinksElement) {
            t.setDepDateTime(elementValue);
        } else if (localName.equals("ArrDateTime") && !isOnRouteLinksElement) {
            t.setArrDateTime(elementValue);
        } else if (localName.equals("DepWalkDist")) {
            t.setDepWalkDist(elementValue);
        } else if (localName.equals("ArrWalkDist")) {
            t.setArrWalkDist(elementValue);
        } else if (localName.equals("NoOfChanges")) {
            t.setNbrOfChanges(elementValue);
        } else if (localName.equals("Guaranteed")) {
            t.setGuaranteed(elementValue);
        } else if (localName.equals("CO2factor")) {
            t.setCO2Factor(elementValue);
        } else if (localName.equals("NoOfZones")) {
            t.setNbrOfZones(elementValue);
        } else if (localName.equals("JourneyKey")) {
            t.setJourneyKey(elementValue);

            //RouteLink start
        } else if (localName.equals("RouteLinkKey")) {
            r.setRouteLinkKey(elementValue);
        } else if (localName.equals("DepDateTime") && isOnRouteLinksElement) {
            r.setDepDateTime(elementValue);
        } else if (localName.equals("DepIsTimingPoint")) {
            r.setDepIsTimingPoint(elementValue);
        } else if (localName.equals("ArrDateTime") && isOnRouteLinksElement) {
            r.setArrDateTime(elementValue);
        } else if (localName.equals("ArrIsTimingPoint")) {
            r.setArrIsTimingPoint(elementValue);
        } else if (localName.equals("CallTrip")) {
            r.setCallTrip(elementValue);
        } else if (localName.equals("DepTimeDeviation")) {
            r.setDepTimeDeviation(elementValue);
        } else if (localName.equals("DepDeviationAffect")) {
            r.setDepDeviationAffect(elementValue);
        } else if (localName.equals("ArrTimeDeviation")) {
            r.setArrTimeDeviation(elementValue);
        } else if (localName.equals("ArrDeviationAffect")) {
            r.setArrDeviationAffect(elementValue);
        } else if (localName.equals("Id")) {
            tempStation.setStationId(Integer.parseInt(elementValue));
        } else if (localName.equals("Name") && !isOnLineElement) {
            tempStation.setStationName(elementValue);
        } else if (localName.equals("StopPoint")) {
            tempStation.setStopPoint(elementValue);

            //Line
        } else if (localName.equals("Name") && isOnLineElement) {
            r.setLineName(elementValue);
        } else if (localName.equals("No")) {
            r.setLineNbr(elementValue);
        } else if (localName.equals("RunNo")) {
            r.setRunNbr(elementValue);
        } else if (localName.equals("LineTypeId")) {
            r.setLineTypeId(elementValue);
        } else if (localName.equals("LineTypeName")) {
            r.setLineTypeName(elementValue);
        } else if (localName.equals("TransportModeId")) {
            r.setTransportMode(elementValue);
        } else if (localName.equals("TransportModeName")) {
            r.setTransportModeName(elementValue);
        } else if (localName.equals("TrainNo")) {
            r.setTrainNbr(elementValue);
        } else if (localName.equals("Towards")) {
            r.setTowardDirection(elementValue);
        } else if (localName.equals("OperatorId")) {
            r.setOperatorId(elementValue);
        } else if (localName.equals("OperatorName")) {
            r.setOperatorName(elementValue);
        } else if (localName.equals("Text")) {
            r.setText(elementValue);
        } else if (localName.equals("PublicNote")) {
            r.setPublicNote(elementValue);
        } else if (localName.equals("Header")) {
            r.setHeader(elementValue);
        } else if (localName.equals("Summary")) {
            r.setSummary(elementValue);
        } else if (localName.equals("ShortText")) {
            r.setShortText(elementValue);

        } else if (localName.equals("DepTimeDeviation")) {
            r.setDepTimeDeviation(elementValue);
        } else if (localName.equals("ArrTimeDeviation")) {
            r.setArrTimeDeviation(elementValue);
        } else if (localName.equals("Accessibility")) {
            r.setAccessibility(elementValue);


            //Done with RouteLink
        } else if (localName.equals("Distance")) {
            t.setDistance(elementValue);
        } else if (localName.equals("CO2value")) {
            t.setCO2value(elementValue);

        } else if (localName.equals("From")) {
            routeLinks.get(routeLinks.size() - 1).setFromStation(tempStation);
        } else if (localName.equals("To")) {
            routeLinks.get(routeLinks.size() - 1).setToStation(tempStation);
        } else if (localName.equals("Line")) {
            isOnLineElement = false;
        } else if (localName.equals("RouteLink")) {
            isOnRouteLinksElement = false;
        } else if (localName.equals("Journey")) {
            t.setRouteLinks(routeLinks);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementOn) {
            elementValue = new String(ch, start, length);
            elementOn = false;
        }
    }

    public ArrayList<Journey> getJourneys() {
        return journeys;
    }

}
