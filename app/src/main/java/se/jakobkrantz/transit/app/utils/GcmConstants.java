package se.jakobkrantz.transit.app.utils;/*
 * Created by krantz on 14-12-11.
 */

/**
 * Constants used in the communication between server and client
 */
public class GcmConstants {

    public static final String DISTURBANCE_TYPE = "distType";
    public static final String DISTURBANCE_REPORT_TIME = "distReportTime";
    public static final String DISTURBANCE_REPORT_TIME_MILLIS = "distRepInMillis";
    public static final String DISTURBANCE_LAT = "distLat";
    public static final String DISTURBANCE_LONG = "distLong";
    public static String ACTION = "action";

    public static String ACTION_REGISTER = "regUserIdAction";
    public static String ACTION_UNREGISTER = "unRegUserIdAction";
    public static String ACTION_SET_INTERESTING_LOCATIONS = "actionLocationsInteresting"; //TODO TO USE
    public static String ACTION_REPORT_DISTURBANCE = "reportDisturbance";
    public static String ACTION_REGISTER_SUCCESSFUL = "registrationSuccessful";
    public static final String ACTION_ACK = "actionAck";

    public static String DISTURBANCE_FROM_STATION_NBR = "distFrom";
    public static String DISTURBANCE_TO_STATION_NBR = "distTo";
    public static String DISTURBANCE_FROM_STATION_NAME = "distFromName";
    public static String DISTURBANCE_TO_STATION_NAME = "distToName";
    public static String DISTURBANCE_APPROX_MINS = "disturbMins";
    public static String DISTURBANCE_NOTE = "distNote";

    long GCM_DEFAULT_TTL = 60 * 60 * 1000; // 1 hour


}
