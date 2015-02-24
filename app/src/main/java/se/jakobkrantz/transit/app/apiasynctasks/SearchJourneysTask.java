package se.jakobkrantz.transit.app.apiasynctasks;
/*
 * Created by krantz on 14-11-21.
 */

import android.os.AsyncTask;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.XMLQueryJourneyHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class SearchJourneysTask extends AsyncTask<String, Void, ArrayList<Journey>> {
    private XMLQueryJourneyHandler xmlQueryJourneyHandler;
    private XMLReader xmlR;
    private DataDownloadListener dataDownloadListener;

    public SearchJourneysTask() {
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
        xmlQueryJourneyHandler = new XMLQueryJourneyHandler();
        xmlR.setContentHandler(xmlQueryJourneyHandler);
    }

    public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
        this.dataDownloadListener = dataDownloadListener;
    }
    @Override
    protected ArrayList<Journey> doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            xmlR.parse(new InputSource(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return xmlQueryJourneyHandler.getJourneys();
    }

    @Override
    protected void onPostExecute(ArrayList<Journey> journeys) {
        if (journeys != null) {

            dataDownloadListener.dataDownloadedSuccessfully(journeys);
        } else {
            dataDownloadListener.dataDownloadFailed();
        }

    }

    public static interface DataDownloadListener {
        void dataDownloadedSuccessfully(Object data);
        void dataDownloadFailed();
    }
}


