package se.jakobkrantz.transit.app.apiasynctasks;/*
 * Created by krantz on 14-11-19.
 */

import android.os.AsyncTask;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import se.jakobkrantz.transit.app.adapters.SearchFragmentListAdapter;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;
import se.jakobkrantz.transit.app.skanetrafikenAPI.XMLQueryStationHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchStationsTask extends AsyncTask<String, Void, ArrayList<Station>> {
    private XMLQueryStationHandler xmlStationHandler;
    private XMLReader xmlR;
    private SearchFragmentListAdapter adapter;

    public SearchStationsTask(SearchFragmentListAdapter adapter) {
        this.adapter = adapter;
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
        xmlStationHandler = new XMLQueryStationHandler();
        xmlR.setContentHandler(xmlStationHandler);
    }


    @Override
    protected ArrayList<Station> doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            xmlR.parse(new InputSource(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return xmlStationHandler.getStations();
    }

    @Override
    protected void onPostExecute(ArrayList<Station> stations) {
        if (stations != null) {
            adapter.setSearchResults(stations);
        }

        //Fill listView with the stations
        //listViewAdapter = new CustomListViewAdapter(context, stations);
        //listView.setAdapter(listViewAdapter);
    }

}
