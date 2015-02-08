package se.jakobkrantz.transit.app.database;/*
 * Created by krantz on 14-11-19.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    public static final String TABLE_NAME_FAVOURITES = "favTable";
    public static final String COLUMN_STATION_ID1 = "id1";
    public static final String COLUMN_STATION_NAME1 = "stationName1";
    public static final String COLUMN_LATITUDE1 = "latitude1";
    public static final String COLUMN_LONG1 = "longitude1";
    public static final String COLUMN_STATION_TYPE1 = "stationType1";

    public static final String TABLE_RECENT_JOURNEY_SEARCH = "recentjourneysearch";





    private static final String[] COLUMNS_RECENT = {COLUMN_STATION_ID, COLUMN_STATION_NAME, COLUMN_LATITUDE, COLUMN_LONG, COLUMN_STATION_TYPE, COLUMN_TIME_SEARCHED};
    private static final String[] COLUMNS_JOURNEYS = {COLUMN_STATION_ID, COLUMN_STATION_ID1, COLUMN_STATION_NAME, COLUMN_STATION_NAME1, COLUMN_LATITUDE, COLUMN_LATITUDE1,
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

    private static final String DATABASE_CREATE_TABLE_RECENT_JOURNEY_SEARCH = "create table "
            + TABLE_RECENT_JOURNEY_SEARCH + "(" + COLUMN_STATION_ID
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

    public void clearRecentJourneySearches() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_RECENT_JOURNEY_SEARCH);
        db.close();
    }

    public void clearAllFavourites() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME_FAVOURITES);
        db.close();
    }

    public void addStationsToRecent(List<Station> stations) {
        if (stations != null) {
            SQLiteDatabase db = getWritableDatabase();
            for (int i = 0; i < stations.size(); i++) {
                try {
                    db.insertOrThrow(TABLE_NAME_RECENT, null, stationToContentValues(stations.get(i)));
                } catch (SQLException e) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_TIME_SEARCHED, getDateTime());
                    db.update(TABLE_NAME_RECENT, cv, COLUMN_STATION_ID + " = ?", new String[]{Integer.toString(stations.get(i).getStationId())});
                }
            }

            db.close();
        }
    }

    public void addRecentJourneySearch(SimpleJourney s) {
        addJourney(s, TABLE_RECENT_JOURNEY_SEARCH);
    }

    /**
     * @param s SimpleJourney to add as favourite
     * @return true if it was added, false if it was already a favourite. In this case last time used was updated to now.
     */
    public boolean addStationFavPair(SimpleJourney s) {
        return addJourney(s, TABLE_NAME_FAVOURITES);

    }

    private boolean addJourney(SimpleJourney s, String table) {
        boolean wasAdded = false;
        s.getFromStation().getStationName();
        s.getToStation().getStationName();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(table, null, COLUMN_STATION_NAME + " = ? AND " + COLUMN_STATION_NAME1 + " = ?", new String[]{s.getFromStation().getStationName(), s.getToStation().getStationName()}, null, null, null, "1");
        if (cursor.getCount() < 1) {
            Station s1 = s.getFromStation();
            Station s2 = s.getToStation();
            ContentValues values = stationToContentValues(s1);
            values.put(COLUMN_STATION_NAME1, s2.getStationName());
            values.put(COLUMN_STATION_ID1, s2.getStationId());
            values.put(COLUMN_LONG1, s2.getLongitude());
            values.put(COLUMN_LATITUDE1, s2.getLatitude());
            values.put(COLUMN_STATION_TYPE1, s2.getType());
            db.insert(table, null, values);
            wasAdded = true;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TIME_SEARCHED, getDateTime());
            db.update(table, cv, COLUMN_STATION_NAME + " = ? AND " + COLUMN_STATION_NAME1 + " = ?", new String[]{s.getFromStation().getStationName(), s.getToStation().getStationName()});
        }
        return wasAdded;

    }

    public void deleteFavouriteJourney(SimpleJourney j) {
        deleteFavouriteJourney(j, TABLE_NAME_FAVOURITES);
    }

    public void deleteRecentJourney(SimpleJourney j) {
        deleteFavouriteJourney(j, TABLE_RECENT_JOURNEY_SEARCH);
    }

    private void deleteFavouriteJourney(SimpleJourney j, String tableName) {
        Station s1 = j.getFromStation();
        Station s2 = j.getToStation();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, COLUMN_STATION_ID + "= ? AND " + COLUMN_STATION_ID1 + "= ?", new String[]{Integer.toString(s1.getStationId()), Integer.toString(s2.getStationId())});
        db.close();
    }

    public void deleteRecentStation(Station s) {
        deleteRecentStation(s.getStationId());
    }

    public void deleteRecentStation(int stationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_RECENT, COLUMN_STATION_ID + "= ?", new String[]{Integer.toString(stationId)});
        db.close();
    }


    public Station getRecentStation(String stationName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_RECENT, null, COLUMN_STATION_NAME + " = ? ", new String[]{stationName}, null, null, null, "1");

        if (cursor != null) {
            cursor.moveToFirst();
        }
        Station s = new Station();
        if (cursor.getColumnCount() > 1) {

            s.setStationId(cursor.getInt(0));
            s.setStationName(cursor.getString(1));
            s.setLatitude(Double.parseDouble(cursor.getString(2)));
            s.setLongitude(Double.parseDouble(cursor.getString(3)));
            s.setType(cursor.getString(4));
            s.setTimeSearched(cursor.getString(5));

            cursor.moveToNext();

            db.close();
        }
        return s;
    }

    /**
     * @param howMany Amount of Stations returned, passing null returns all. TODO: Can't enter null, FIX
     *                Sorted after last time searched.
     * @return
     */
    public List<Station> getRecentStations(int howMany) {
        List<Station> stations = new ArrayList<Station>(howMany);
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_RECENT, COLUMNS_RECENT, null, null, null, null, COLUMN_TIME_SEARCHED + " DESC", Integer.toString(howMany));
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
        db.close();
        return stations;
    }

    /**
     * @param howMany amount favourite journeys returned as SimpleJourney objects. Passing null return all.
     *                Sorted after time added/searched
     * @return
     */
    public List<SimpleJourney> getFavouriteJourneys(int howMany) {
        return getFavouriteJourneys(howMany, TABLE_NAME_FAVOURITES);
    }

    /**
     * @param howMany amount recent journeys returned as SimpleJourney objects. Passing null return all.
     *                Sorted after time added/searched
     * @return
     */
    public List<SimpleJourney> getRecentJourneys(int howMany) {
        return getFavouriteJourneys(howMany, TABLE_RECENT_JOURNEY_SEARCH);
    }

    private Station searchTableForStation(SQLiteDatabase db, String table, String station) {
        Cursor cursor = db.query(table, null, COLUMN_STATION_NAME + " = ?", new String[]{station}, null, null, null, "1");
        Station s1;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            s1 = new Station();
            s1.setStationId(cursor.getInt(0));
            s1.setStationName(cursor.getString(2));
            s1.setLatitude(Double.parseDouble(cursor.getString(4)));
            s1.setLongitude(Double.parseDouble(cursor.getString(6)));
            s1.setType(cursor.getString(8));
            s1.setTimeSearched(cursor.getString(10));
            return s1;
        } else {
            cursor = db.query(table, null, COLUMN_STATION_NAME1 + " = ?", new String[]{station}, null, null, null, "1");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                s1 = new Station();
                s1.setStationId(cursor.getInt(1));
                s1.setStationName(cursor.getString(3));
                s1.setLatitude(Double.parseDouble(cursor.getString(5)));
                s1.setLongitude(Double.parseDouble(cursor.getString(7)));
                s1.setType(cursor.getString(9));
                s1.setTimeSearched(cursor.getString(10));
                return s1;
            }

        }
        return null;
    }


    public SimpleJourney getSimpleJourneyFromRecentOrFavs(String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_FAVOURITES, null, COLUMN_STATION_NAME + " = ? AND " + COLUMN_STATION_NAME1 + " = ?", new String[]{from, to}, null, null, null, "1");
        if (cursor.getCount() < 1) {
            cursor = db.query(TABLE_RECENT_JOURNEY_SEARCH, null, COLUMN_STATION_NAME + " = ? AND " + COLUMN_STATION_NAME1 + " = ?", new String[]{from, to}, null, null, null, "1");
        }
        if (cursor.getCount() > 0) {

            if (cursor != null) {
                cursor.moveToFirst();
            }
            Station s1 = new Station();
            Station s2 = new Station();

            s1.setStationId(cursor.getInt(0));
            s2.setStationId(cursor.getInt(1));

            s1.setStationName(cursor.getString(2));
            s2.setStationName(cursor.getString(3));

            s1.setLatitude(Double.parseDouble(cursor.getString(4)));
            s2.setLatitude(Double.parseDouble(cursor.getString(5)));

            s1.setLongitude(Double.parseDouble(cursor.getString(6)));
            s2.setLongitude(Double.parseDouble(cursor.getString(7)));

            s1.setType(cursor.getString(8));
            s2.setType(cursor.getString(9));

            s1.setTimeSearched(cursor.getString(10));
            s2.setTimeSearched(cursor.getString(10));


            db.close();
            return new SimpleJourney(s1, s2);
        } else {
            Station s1 = searchTableForStation(db, TABLE_NAME_FAVOURITES, from);
            Station s2 = searchTableForStation(db, TABLE_NAME_FAVOURITES, to);
            if (s1 == null) {
                s1 = searchTableForStation(db, TABLE_RECENT_JOURNEY_SEARCH, from);
            }
            if (s2 == null) {
                s2 = searchTableForStation(db, TABLE_RECENT_JOURNEY_SEARCH, to);
            }
            return new SimpleJourney(s1, s2);
        }
    }


    private List<SimpleJourney> getFavouriteJourneys(int howMany, String table) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(table, COLUMNS_JOURNEYS, null, null, null, null, COLUMN_TIME_SEARCHED + " DESC", Integer.toString(howMany));

        if (cursor != null) {
            cursor.moveToFirst();
        }
        List<SimpleJourney> journeys = new ArrayList<SimpleJourney>(howMany);
        while (!cursor.isAfterLast()) {
            Station s1 = new Station();
            Station s2 = new Station();

            s1.setStationId(cursor.getInt(0));
            s2.setStationId(cursor.getInt(1));

            s1.setStationName(cursor.getString(2));
            s2.setStationName(cursor.getString(3));

            s1.setLatitude(Double.parseDouble(cursor.getString(4)));
            s2.setLatitude(Double.parseDouble(cursor.getString(5)));

            s1.setLongitude(Double.parseDouble(cursor.getString(6)));
            s2.setLongitude(Double.parseDouble(cursor.getString(7)));

            s1.setType(cursor.getString(8));
            s2.setType(cursor.getString(9));

            s1.setTimeSearched(cursor.getString(10));
            s2.setTimeSearched(cursor.getString(10));

            cursor.moveToNext();
            journeys.add(new SimpleJourney(s1, s2));
        }

        db.close();
        return journeys;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_RECENT);
        db.execSQL(DATABASE_CREATE_TABLE_FAVOURITES);
        db.execSQL(DATABASE_CREATE_TABLE_RECENT_JOURNEY_SEARCH);
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
