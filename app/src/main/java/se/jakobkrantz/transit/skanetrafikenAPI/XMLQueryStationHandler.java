package se.jakobkrantz.transit.skanetrafikenAPI;/*
 * Created by krantz on 14-11-18.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XMLQueryStationHandler extends DefaultHandler {
    boolean elementOn = false;
    private ArrayList<Station> stations;
    private Station tempStation;
    private StringBuilder sb;


    public XMLQueryStationHandler() {
        sb = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribute) {
        elementOn = true;
        sb.delete(0, sb.length());
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
            tempStation.setStationId(Integer.parseInt(sb.toString()));
        } else if (localName.equals("Name")) {
            tempStation.setStationName(sb.toString());
        } else if (localName.equals("Type")) {
            tempStation.setType(sb.toString());
        } else if (localName.equals("X")) {
            tempStation.setX(Double.parseDouble(sb.toString()));
        } else if (localName.equals("Y")) {
            tempStation.setY(Double.parseDouble(sb.toString()));
        } else if (localName.equals("Point")) { // End tag point, done reading station.
            if (stations != null) {
                stations.add(tempStation);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //StringBuilder because the parser splits elements over multiple character arrays.
        if (elementOn) {
            sb.append(ch, start, length);

        }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

}
