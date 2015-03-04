package se.jakobkrantz.transit.app.apiasynctasks;
/*
 * Created by jakkra on 2015-03-02.
 */

import android.os.AsyncTask;
import android.util.Log;
import org.unbescape.html.HtmlEscape;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import se.jakobkrantz.transit.app.skanetrafikenAPI.CoordinateRoute;
import se.jakobkrantz.transit.app.skanetrafikenAPI.XMLRouteMapHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchRouteMapTask extends AsyncTask<String, Void, ArrayList<CoordinateRoute>> {
    private XMLRouteMapHandler xmlRouteMapHandler;
    private XMLReader xmlR;
    private DataDownloadListener downloadListener;

    public SearchRouteMapTask() {
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

    public void setDataDownloadListener(DataDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }


    @Override
    protected ArrayList<CoordinateRoute> doInBackground(String... params) {
        Log.e("url in async", params[0]);

        try {
            String xml = new Scanner(new URL(params[0]).openStream()).useDelimiter("\\A").next();
            xml = HtmlEscape.unescapeHtml(xml);
            InputSource stream = new InputSource(new StringReader(xml));
            xmlR.parse(stream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return xmlRouteMapHandler.getCoordinateRoutes();

    }

    @Override
    protected void onPostExecute(ArrayList<CoordinateRoute> journey) {
        if (journey != null) {
            downloadListener.dataDownloadedSuccessfully(journey);
        } else {
            downloadListener.dataDownloadFailed();
        }
    }
}
