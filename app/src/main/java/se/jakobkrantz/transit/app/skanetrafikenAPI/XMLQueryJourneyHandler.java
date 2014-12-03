package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-20.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XMLQueryJourneyHandler extends DefaultHandler {
    boolean elementOn = false;
    private ArrayList<Journey> journeys;
    private Journey t;
    private Station tempStation;
    private ArrayList<RouteLink> routeLinks;
    private RouteLink r;
    private boolean isOnRouteLinksElement;
    private boolean isOnLineElement;
    private boolean isOnPriceZoneElement;
    private StringBuilder sb;

    public XMLQueryJourneyHandler() {
        sb = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribute) {
        elementOn = true;
        sb.delete(0, sb.length());
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
        } else if (localName.equals("PriceZones")) {
            isOnPriceZoneElement = true;
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
            t.setSequenceNbr(sb.toString());
        } else if (localName.equals("DepDateTime") && !isOnRouteLinksElement) {
            t.setDepDateTime(sb.toString());
        } else if (localName.equals("ArrDateTime") && !isOnRouteLinksElement) {
            t.setArrDateTime(sb.toString());
        } else if (localName.equals("DepWalkDist")) {
            t.setDepWalkDist(sb.toString());
        } else if (localName.equals("ArrWalkDist")) {
            t.setArrWalkDist(sb.toString());
        } else if (localName.equals("NoOfChanges")) {
            t.setNbrOfChanges(sb.toString());
        } else if (localName.equals("Guaranteed")) {
            t.setGuaranteed(sb.toString());
        } else if (localName.equals("CO2factor")) {
            t.setCO2Factor(sb.toString());
        } else if (localName.equals("NoOfZones")) {
            t.setNbrOfZones(sb.toString());
        } else if (localName.equals("JourneyKey")) {
            t.setJourneyKey(sb.toString());

            //RouteLink start
        } else if (localName.equals("RouteLinkKey")) {
            r.setRouteLinkKey(sb.toString());
        } else if (localName.equals("DepDateTime") && isOnRouteLinksElement) {
            r.setDepDateTime(sb.toString());
        } else if (localName.equals("DepIsTimingPoint")) {
            r.setDepIsTimingPoint(sb.toString());
        } else if (localName.equals("ArrDateTime") && isOnRouteLinksElement) {
            r.setArrDateTime(sb.toString());
        } else if (localName.equals("ArrIsTimingPoint")) {
            r.setArrIsTimingPoint(sb.toString());
        } else if (localName.equals("CallTrip")) {
            r.setCallTrip(sb.toString());
        } else if (localName.equals("DepTimeDeviation")) {
            r.setDepTimeDeviation(sb.toString());
        } else if (localName.equals("DepDeviationAffect")) {
            r.setDepDeviationAffect(sb.toString());
        } else if (localName.equals("ArrTimeDeviation")) {
            r.setArrTimeDeviation(sb.toString());
        } else if (localName.equals("ArrDeviationAffect")) {
            r.setArrDeviationAffect(sb.toString());
        } else if (localName.equals("Id") && !isOnPriceZoneElement) {
            tempStation.setStationId(Integer.parseInt(sb.toString()));
        } else if (localName.equals("Name") && !isOnLineElement) {
            tempStation.setStationName(sb.toString());


            //Line
        } else if (localName.equals("Name") && isOnLineElement) {
            r.setLineName(sb.toString());
        } else if (localName.equals("No")) {
            r.setLineNbr(sb.toString());
        } else if (localName.equals("RunNo")) {
            r.setRunNbr(sb.toString());
        } else if (localName.equals("LineTypeId")) {
            r.setLineTypeId(sb.toString());
        } else if (localName.equals("LineTypeName")) {
            r.setLineTypeName(sb.toString());
        } else if (localName.equals("TransportModeId")) {
            r.setTransportMode(sb.toString());
        } else if (localName.equals("TransportModeName")) {
            r.setTransportModeName(sb.toString());
        } else if (localName.equals("TrainNo")) {
            r.setTrainNbr(sb.toString());
        } else if (localName.equals("Towards")) {
            r.setTowardDirection(sb.toString());
        } else if (localName.equals("OperatorId")) {
            r.setOperatorId(sb.toString());
        } else if (localName.equals("OperatorName")) {
            r.setOperatorName(sb.toString());
        } else if (localName.equals("Text")) {
            r.setText(sb.toString());
        } else if (localName.equals("PublicNote")) {
            r.setPublicNote(sb.toString());
        } else if (localName.equals("Header")) {
            r.setHeader(sb.toString());
        } else if (localName.equals("Summary")) {
            r.setSummary(sb.toString());
        } else if (localName.equals("ShortText")) {
            r.setShortText(sb.toString());

        } else if (localName.equals("DepTimeDeviation")) {
            r.setDepTimeDeviation(sb.toString());
        } else if (localName.equals("ArrTimeDeviation")) {
            r.setArrTimeDeviation(sb.toString());
        } else if (localName.equals("Accessibility")) {
            r.setAccessibility(sb.toString());
        } else if (localName.equals("StopPoint")) {
            if (r.getStartPoint() == null) {
                r.setStartPoint(sb.toString());
            } else {
                r.setStopPoint(sb.toString());
            }
            //Done with RouteLink
        } else if (localName.equals("Distance")) {
            t.setDistance(sb.toString());
        } else if (localName.equals("CO2value")) {
            t.setCO2value(sb.toString());

        } else if (localName.equals("From")) {
            r.setFromStation(tempStation);
        } else if (localName.equals("To")) {
            r.setToStation(tempStation);
        } else if (localName.equals("Line")) {
            isOnLineElement = false;
        } else if (localName.equals("PriceZones")) {
            isOnPriceZoneElement = true;
        } else if (localName.equals("RouteLink")) {
            routeLinks.add(r);
        } else if (localName.equals("Journey")) {
            t.setRouteLinks(routeLinks);
            journeys.add(t);
        } else if (localName.equals("RouteLinks")) {
            isOnRouteLinksElement = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //StringBuilder because the parser splits elements over multiple character arrays.
        if (elementOn) {
            sb.append(ch, start, length);
        }
    }

    public ArrayList<Journey> getJourneys() {
        return journeys;
    }

}
