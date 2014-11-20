package se.jakobkrantz.transit.app.database;/*
 * Created by krantz on 14-11-19.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseTransitSQLite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "transitDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_RECENT = "recentSearchesTable";
    public static final String COLUMN_STATION_ID = "id";
    public static final String COLUMN_STATION_NAME = "stationName";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_STATION_TYPE = "stationType";
    public static final String COLUMN_TIME_SEARCHED = "timeSearchedStation";

    public static final String COLUMN_STATION_ID1 = "id1";
    public static final String COLUMN_STATION_NAME1 = "stationName1";
    public static final String COLUMN_LATITUDE1 = "latitude1";
    public static final String COLUMN_LONG1 = "longitude1";
    public static final String COLUMN_STATION_TYPE1 = "stationType1";
    public static final String COLUMN_TIME_SEARCHED1 = "timeSearchedStation1";

    public static final String TABLE_NAME_FAVOURITES = "favTable";


    private static final String[] COLUMNS_RECENT = {COLUMN_STATION_ID, COLUMN_STATION_NAME, COLUMN_LATITUDE, COLUMN_LONG, COLUMN_STATION_TYPE, COLUMN_TIME_SEARCHED};
    private static final String[] COLUMNS_FAVOURITES = {COLUMN_STATION_ID, COLUMN_STATION_ID1, COLUMN_STATION_NAME, COLUMN_STATION_NAME1, COLUMN_LATITUDE, COLUMN_LATITUDE1,
            COLUMN_LONG, COLUMN_LONG1, COLUMN_STATION_TYPE, COLUMN_STATION_TYPE1, COLUMN_TIME_SEARCHED};


    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_RECENT = "create table "
            + TABLE_NAME_RECENT + "(" + COLUMN_STATION_ID
            + " integer primary key, " + COLUMN_STATION_NAME
            + " text, " + COLUMN_LATITUDE
            + " text, " + COLUMN_LONG
            + " text, " + COLUMN_STATION_TYPE
            + " text, " + COLUMN_TIME_SEARCHED
            + " datetime DEFAULT CURRENT_TIMESTAMP);";


    private static final String DATABASE_CREATE_TABLE_FAVOURITES = "create table "
            + TABLE_NAME_FAVOURITES + "(" + COLUMN_STATION_ID
            + " integer, " + COLUMN_STATION_ID1
            + " integer, " + COLUMN_STATION_NAME
            + " text, " + COLUMN_STATION_NAME1
            + " text, " + COLUMN_LONG
            + " text, " + COLUMN_LONG1
            + " text, " + COLUMN_LATITUDE
            + " text, " + COLUMN_LATITUDE1
            + " text, " + COLUMN_STATION_TYPE
            + " text, " + COLUMN_STATION_TYPE1
            + " text, " + COLUMN_TIME_SEARCHED
            + " datetime DEFAULT CURRENT_TIMESTAMP);";


    public DatabaseTransitSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addStationsToRecent(final ArrayList<Station> stations) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = getWritableDatabase();
                for (int i = 0; i < stations.size(); i++) {
                    db.insert(TABLE_NAME_RECENT, null, stationToContentValues(stations.get(i)));
                }
                db.close();
                return null;
            }
        }.execute();
    }


    public void addStationPair(Station s1, Station s2) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = stationToContentValues(s1);
        values.put(COLUMN_STATION_NAME1, s2.getStationName());
        values.put(COLUMN_STATION_ID1, s2.getStationId());
        values.put(COLUMN_LONG1, s2.getLongitude());
        values.put(COLUMN_LATITUDE1, s2.getLatitude());
        values.put(COLUMN_STATION_TYPE1, s2.getType());
        db.insert(TABLE_NAME_FAVOURITES, null, values);
    }


    public void deleteRecentStation(Station s) {
        deleteRecentStation(s.getStationId());
    }

    public void deleteRecentStation(int stationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_RECENT, COLUMN_STATION_ID + "= ?", new String[]{Integer.toString(stationId)});
        db.close();
    }

    public void deleteFavouriteJourney(SimpleJourney j) {
        Station s1 = j.getFromStation();
        Station s2 = j.getToStation();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_FAVOURITES, COLUMN_STATION_ID + "= ? AND " + COLUMN_STATION_ID1 + "= ?", new String[]{Integer.toString(s1.getStationId()), Integer.toString(s2.getStationId())});
        db.close();
    }

    /**
     * @param howMany Amount of Stations returned, passing null returns all.
     *                Sorted after last time searched.
     * @return
     */
    public List<Station> getRecentStations(final int howMany) {
        final ArrayList<Station> stations = new ArrayList<Station>(howMany);


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = getReadableDatabase();

                Cursor cursor = db.query(TABLE_NAME_RECENT, COLUMNS_RECENT, null, null, null, null, COLUMN_TIME_SEARCHED + " DESC", Integer.toString(howMany));
                db.close();
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                while (!cursor.isAfterLast()) {

                    Station s = new Station();
                    s.setStationId(cursor.getInt(0));
                    s.setStationName(cursor.getString(1));
                    s.setLatitude(Double.parseDouble(cursor.getString(2)));
                    s.setLongitude(Double.parseDouble(cursor.getString(3)));
                    s.setType(cursor.getString(4));
                    s.setTimeSearched(cursor.getString(5));
                    stations.add(s);
                    cursor.moveToNext();
                }
                return null;
            }


        }.execute();

        return stations;
    }

    /**
     * @param howMany amount os favourite journeys returned as SimpleJourney objects. Passing null return all.
     *                Sorted after time added/searched
     * @return
     */
    public List<SimpleJourney> getAllFavouriteJourneys(int howMany) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_RECENT, COLUMNS_FAVOURITES, null, null, null, null, COLUMN_TIME_SEARCHED + " DESC", Integer.toString(howMany));

        if (cursor != null) {
            cursor.moveToFirst();
        }
        ArrayList<SimpleJourney> journeys = new ArrayList<SimpleJourney>(howMany);
        while (!cursor.isAfterLast()) {
            Station s1 = new Station();
            Station s2 = new Station();

            s1.setStationId(cursor.getInt(0));
            s2.setStationId(cursor.getInt(1));

            s1.setStationName(cursor.getString(2));
            s1.setStationName(cursor.getString(3));

            s1.setLatitude(Double.parseDouble(cursor.getString(4)));
            s2.setLatitude(Double.parseDouble(cursor.getString(5)));

            s1.setLongitude(Double.parseDouble(cursor.getString(6)));
            s2.setLongitude(Double.parseDouble(cursor.getString(7)));

            s1.setType(cursor.getString(8));
            s2.setType(cursor.getString(9));

            s1.setTimeSearched(cursor.getString(10));
            s2.setTimeSearched(cursor.getString(10));

            cursor.moveToNext();
            journeys.add(new SimpleJourney(s1,s2));
        }

        db.close();
        return journeys;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_RECENT);
        db.execSQL(DATABASE_CREATE_TABLE_FAVOURITES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private ContentValues stationToContentValues(Station s) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATION_NAME, s.getStationName());
        values.put(COLUMN_STATION_ID, s.getStationId());
        values.put(COLUMN_LONG, s.getLongitude());
        values.put(COLUMN_LATITUDE, s.getLatitude());
        values.put(COLUMN_STATION_TYPE, s.getType());
        values.put(COLUMN_TIME_SEARCHED, getDateTime());
        return values;
    }

}
