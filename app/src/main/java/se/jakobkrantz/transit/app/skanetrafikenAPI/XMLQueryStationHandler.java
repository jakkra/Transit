package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-18.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XMLQueryStationHandler extends DefaultHandler {
    String elementValue = null;
    boolean elementOn = false;
    private ArrayList<Station> stations;
    private Station tempStation;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribute) {
        elementOn = true;
        if (localName.equals("StartPoints")) {
            stations = new ArrayList<Station>();
        } else if (localName.equals("Point")) {
            tempStation = new Station();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        elementOn = false;

        if (localName.equals("Id")) {
            tempStation.setStationId(Integer.parseInt(elementValue));
        } else if (localName.equals("Name")) {
            tempStation.setStationName(elementValue);
        } else if (localName.equals("Type")) {
            tempStation.setType(elementValue);
        } else if (localName.equals("X")) {
            tempStation.setLatitude(Double.parseDouble(elementValue));
        } else if (localName.equals("Y")) {
            tempStation.setLongitude(Double.parseDouble(elementValue));
        } else if (localName.equals("Point")) { // End tag point, done reading station.
            if (stations != null) {
                stations.add(tempStation);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementOn) {
            elementValue = new String(ch, start, length);
            elementOn = false;
        }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

}
