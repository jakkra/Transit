package se.jakobkrantz.transit.app.utils;/*
 * Created by krantz on 14-12-11.
 */

public class GcmConstants {

    public static String ACTION = "action";

    public static String ACTION_REGISTER = "regUserIdAction";
    public static String ACTION_UNREGISTER = "unRegUserIdAction";
    public static String ACTION_SET_INTERESTING_LOCATIONS = "actionLocationsInteresting";
    public static String ACTION_REPORT_DISTURBANCE = "reportDisturbance";
    public static String ACTION_REGISTER_SUCCESSFUL = "registrationSuccessful";

    public static String DISTURBANCE_FROM_STATION_NBR = "distFrom";
    public static String DISTURBANCE_TO_STATION_NBR = "distTo";
    public static String DISTURBANCE_FROM_STATION_NAME = "distFromName";
    public static String DISTURBANCE_TO_STATION_NAME = "distToName";
    public static String DISTURBANCE_APPROX_MINS = "disturbMins";
    public static String DISTURBANCE_NOTE = "distNote";



    long GCM_DEFAULT_TTL = 60 * 60 * 1000; // 1 hour


}
