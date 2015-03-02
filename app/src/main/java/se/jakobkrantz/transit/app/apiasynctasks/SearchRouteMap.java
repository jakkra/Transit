package se.jakobkrantz.transit.app.apiasynctasks;
/*
 * Created by jakkra on 2015-03-02.
 */

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import se.jakobkrantz.transit.app.skanetrafikenAPI.XMLRouteMapHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchRouteMap extends AsyncTask<String, Void, ArrayList<LatLng>> {
    private XMLRouteMapHandler xmlRouteMapHandler;
    private XMLReader xmlR;
    
    public SearchRouteMap() {
        SAXParserFactory saxPF = SAXParserFactory.newInstance();
        SAXParser saxP;

        try {
            saxP = saxPF.newSAXParser();
            xmlR = saxP.getXMLReader();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        xmlRouteMapHandler = new XMLRouteMapHandler();
        xmlR.setContentHandler(xmlRouteMapHandler);
    }


    @Override
    protected ArrayList<LatLng> doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            xmlR.parse(new InputSource(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return xmlRouteMapHandler.getLatLngs();
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> stations) {
        if (stations != null) {
            //adapter.setSearchResults(stations);
        }
    }
}
