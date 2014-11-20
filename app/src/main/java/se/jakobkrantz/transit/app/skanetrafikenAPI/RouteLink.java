package se.jakobkrantz.transit.app.skanetrafikenAPI;/*
 * Created by krantz on 14-11-20.
 */


public class RouteLink {

    private String lineNbr; //Line's number
    private String runNbr; //Line's run number
    private String lineTypeId; //Reference to one line type in line types collection defined by transport authority. All available line types and ids can be retreved from GetMeansOfTransport function
    private String lineTypeName; //Line type name
    private String transportMode; //Reference to one TransportMode in modes collection defined by transport authority. All available TransportModes and ids can be retreved from GetMeansOfTransport function
    private String transportModeName; //Transport mode name
    private String trainNbr; //Additional info about train number if route link's line type is train
    private String towardDirection; //Destination text
    private String operatorId; //Vehicle operators unique id
    private String operatorName; //Vehicle operators name
    private String depTimeDeviation; //Deviation from timetable time in min. (on departure side). Delays are positive integer values and earlier times are negative.
    private String arrTimeDeviation; //Deviation from timetable time in min. (on arrival side). Delays are positive integer values and earlier times are negative.
    private String accessibility; //Accessibility, sum of accesibility features for line where 1=R (Adapted for wheelchair), 2=S (Visually impaired), 4=H (Hearing impaired)
    private String routeLinkKey; //Used by the Elmer seach engine to identify an object uniquely in the scope of a traffic data.
    private String depDateTime; //Departure date and time
    private String depIsTimingPoint; //Denotes if Departure node is a timing point. False means that DepDateTime is aproximated time
    private String arrDateTime; //Arrival date and time
    private String arrIsTimingPoint; //Arrival date and time
    private String callTrip; //Denotes if Arrival node is timing point. False means that ArrDateTime is approximated time
    private String lineName; //Line's name
    private Station fromStation; // from station, can be an middle point
    private Station toStation; // to station, can be an middle point
    private String depDeviationAffect; //Describes how departure time deviation affects the journey.
    private String arrDeviationAffect; //Describes how arrival time deviation affects the journey.
    private String text; //FootNote's text

    //Notes
    private String publicNote;
    private String header;
    private String summary;
    private String shortText;


    public void setLineNbr(String lineNbr) {
        this.lineNbr = lineNbr;
    }

    public String getLineNbr() {
        return lineNbr;
    }

    public void setRunNbr(String runNbr) {
        this.runNbr = runNbr;
    }

    public String getRunNbr() {
        return runNbr;
    }

    public void setLineTypeId(String lineTypeId) {
        this.lineTypeId = lineTypeId;
    }

    public String getLineTypeId() {
        return lineTypeId;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportModeName(String transportModeName) {
        this.transportModeName = transportModeName;
    }

    public String getTransportModeName() {
        return transportModeName;
    }

    public void setTrainNbr(String trainNbr) {
        this.trainNbr = trainNbr;
    }

    public String getTrainNbr() {
        return trainNbr;
    }

    public void setTowardDirection(String towardDirection) {
        this.towardDirection = towardDirection;
    }

    public String getTowardDirection() {
        return towardDirection;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorName() {
        return operatorName;
    }


    public void setDepTimeDeviation(String depTimeDeviation) {
        this.depTimeDeviation = depTimeDeviation;
    }

    public String getDepTimeDeviation() {
        return depTimeDeviation;
    }

    public void setArrTimeDeviation(String arrTimeDeviation) {
        this.arrTimeDeviation = arrTimeDeviation;
    }

    public String getArrTimeDeviation() {
        return arrTimeDeviation;
    }



    /**
     * @param accessibility  sum of accesibility features for line where
     *                       1=R (Adapted for wheelchair), 2=S (Visually impaired),
     *                       4=H (Hearing impaired)
     */
    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setRouteLinkKey(String routeLinkKey) {
        this.routeLinkKey = routeLinkKey;
    }

    public String getRouteLinkKey() {
        return routeLinkKey;
    }

    public void setDepDateTime(String depDateTime) {
        this.depDateTime = depDateTime;
    }

    public String getDepDateTime() {
        return depDateTime;
    }

    public void setDepIsTimingPoint(String depIsTimingPoint) {
        this.depIsTimingPoint = depIsTimingPoint;
    }

    public String getDepIsTimingPoint() {
        return depIsTimingPoint;
    }

    public void setArrDateTime(String arrDateTime) {
        this.arrDateTime = arrDateTime;
    }

    public String getArrDateTime() {
        return arrDateTime;
    }

    public void setArrIsTimingPoint(String arrIsTimingPoint) {
        this.arrIsTimingPoint = arrIsTimingPoint;
    }

    public String getArrIsTimingPoint() {
        return arrIsTimingPoint;
    }

    public void setCallTrip(String callTrip) {
        this.callTrip = callTrip;
    }

    public String getCallTrip() {
        return callTrip;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setFromStation(Station fromStation) {
        this.fromStation = fromStation;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public void setToStation(Station toStation) {
        this.toStation = toStation;
    }

    public Station getToStation() {
        return toStation;
    }

    public void setDepDeviationAffect(String depDeviationAffect) {
        this.depDeviationAffect = depDeviationAffect;
    }

    public String getDepDeviationAffect() {
        return depDeviationAffect;
    }

    public void setArrDeviationAffect(String arrDeviationAffect) {
        this.arrDeviationAffect = arrDeviationAffect;
    }

    public String getArrDeviationAffect() {
        return arrDeviationAffect;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setPublicNote(String publicNote) {
        this.publicNote = publicNote;
    }

    public String getPublicNote() {
        return publicNote;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getShortText() {
        return shortText;
    }
}
