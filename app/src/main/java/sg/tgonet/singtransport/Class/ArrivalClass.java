package sg.tgonet.singtransport.Class;

import java.io.Serializable;

public class ArrivalClass implements Comparable<ArrivalClass>, Serializable {

    private String ServiceNo;
    private String Timing;
    private String Timing2;
    private String Timing3;
    private String Load;
    private String Load2;
    private String Load3;
    public Double Lat1;
    public Double Lag1;
    public Double Lat2;
    public Double Lag2;
    public Double Lat3;
    public Double Lag3;
    public Boolean Favourite;

    public ArrivalClass(String serviceNo, String timing, String timing2, String timing3, String load, String load2, String load3, Double lat1, Double lag1, Double lat2, Double lag2, Double lat3, Double lag3, Boolean favourite) {
        ServiceNo = serviceNo;
        Timing = timing;
        Timing2 = timing2;
        Timing3 = timing3;
        Load = load;
        Load2 = load2;
        Load3 = load3;
        Lat1 = lat1;
        Lag1 = lag1;
        Lat2 = lat2;
        Lag2 = lag2;
        Lat3 = lat3;
        Lag3 = lag3;
        Favourite = favourite;
    }

    public ArrivalClass(String serviceNo, Boolean favourite) {
        ServiceNo = serviceNo;
        Favourite = favourite;
    }

    public ArrivalClass(String serviceNo, String timing, String timing2, String timing3, String load, String load2, String load3, Double lat1, Double lag1, Double lat2, Double lag2, Double lat3, Double lag3) {
        ServiceNo = serviceNo;
        Timing = timing;
        Timing2 = timing2;
        Timing3 = timing3;
        Load = load;
        Load2 = load2;
        Load3 = load3;
        Lat1 = lat1;
        Lag1 = lag1;
        Lat2 = lat2;
        Lag2 = lag2;
        Lat3 = lat3;
        Lag3 = lag3;
    }

    public ArrivalClass(String serviceNo, String timing, String timing2, String timing3, String load, String load2, String load3, Boolean favourite) {
        ServiceNo = serviceNo;
        Timing = timing;
        Timing2 = timing2;
        Timing3 = timing3;
        Load = load;
        Load2 = load2;
        Load3 = load3;
        Favourite = favourite;
    }

    public String getServiceNo() {
        return ServiceNo;
    }

    public void setServiceNo(String serviceNo) {
        ServiceNo = serviceNo;
    }

    public String getTiming() {
        return Timing;
    }

    public void setTiming(String timing) {
        Timing = timing;
    }

    public String getTiming2() {
        return Timing2;
    }

    public void setTiming2(String timing2) {
        Timing2 = timing2;
    }

    public String getTiming3() {
        return Timing3;
    }

    public void setTiming3(String timing3) {
        Timing3 = timing3;
    }

    public String getLoad() {
        return Load;
    }

    public void setLoad(String load) {
        Load = load;
    }

    public String getLoad2() {
        return Load2;
    }

    public void setLoad2(String load2) {
        Load2 = load2;
    }

    public String getLoad3() {
        return Load3;
    }

    public void setLoad3(String load3) {
        Load3 = load3;
    }

    public Double getLat1() {
        return Lat1;
    }

    public void setLat1(Double lat1) {
        Lat1 = lat1;
    }

    public Double getLag1() {
        return Lag1;
    }

    public void setLag1(Double lag1) {
        Lag1 = lag1;
    }

    public Double getLat2() {
        return Lat2;
    }

    public void setLat2(Double lat2) {
        Lat2 = lat2;
    }

    public Double getLag2() {
        return Lag2;
    }

    public void setLag2(Double lag2) {
        Lag2 = lag2;
    }

    public Double getLat3() {
        return Lat3;
    }

    public void setLat3(Double lat3) {
        Lat3 = lat3;
    }

    public Double getLag3() {
        return Lag3;
    }

    public void setLag3(Double lag3) {
        Lag3 = lag3;
    }

    public Boolean getFavourite() {
        return Favourite;
    }

    public void setFavourite(Boolean favourite) {
        Favourite = favourite;
    }

    public int compareTo(ArrivalClass o) {
        return this.getServiceNo().compareTo(o.getServiceNo());
    }
}

