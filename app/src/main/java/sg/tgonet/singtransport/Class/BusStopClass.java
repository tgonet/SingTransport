package sg.tgonet.singtransport.Class;

import java.io.Serializable;

public class BusStopClass extends Buses implements Comparable<BusStopClass>, Serializable {

    private String BusStopCode;
    private String Description;
    private String RoadName;
    private Double Latitude;
    private Double Longitude;
    private Double Distance;

    public BusStopClass(String busStopCode, String description, String roadName, Double latitude, Double longitude) {
        BusStopCode = busStopCode;
        Description = description;
        RoadName = roadName;
        Latitude = latitude;
        Longitude = longitude;
    }

    public BusStopClass(String busStopCode, String description, String roadName, Double latitude, Double longitude, Double distance) {
        BusStopCode = busStopCode;
        Description = description;
        RoadName = roadName;
        Latitude = latitude;
        Longitude = longitude;
        Distance = distance;
    }

    public String getBusStopCode() {
        return BusStopCode;
    }

    public void setBusStopCode(String busStopCode) {
        BusStopCode = busStopCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getRoadName() {
        return RoadName;
    }

    public void setRoadName(String roadName) {
        RoadName = roadName;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    @Override
    public int compareTo(BusStopClass o) {
        return Integer.parseInt(this.getBusStopCode()) - Integer.parseInt(o.getBusStopCode());
    }


}
