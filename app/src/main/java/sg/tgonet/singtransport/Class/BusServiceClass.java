package sg.tgonet.singtransport.Class;

public class BusServiceClass extends Buses implements Comparable<BusServiceClass> {

    private String ServiceNo;
    private String FirstStop;
    private String LastStop;
    private String Direction;
    private String StartBusStop;
    private String EndBusStop;

    public BusServiceClass(String serviceNo, String firstStop, String lastStop, String direction) {
        ServiceNo = serviceNo;
        FirstStop = firstStop;
        LastStop = lastStop;
        Direction = direction;
    }

    public BusServiceClass(String serviceNo, String firstStop, String lastStop, String direction, String startBusStop, String endBusStop) {
        ServiceNo = serviceNo;
        FirstStop = firstStop;
        LastStop = lastStop;
        Direction = direction;
        StartBusStop = startBusStop;
        EndBusStop = endBusStop;
    }

    public String getServiceNo() {
        return ServiceNo;
    }

    public void setServiceNo(String serviceNo) {
        ServiceNo = serviceNo;
    }

    public String getFirstStop() {
        return FirstStop;
    }

    public void setFirstStop(String firstStop) {
        FirstStop = firstStop;
    }

    public String getLastStop() {
        return LastStop;
    }

    public void setLastStop(String lastStop) {
        LastStop = lastStop;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getStartBusStop() {
        return StartBusStop;
    }

    public void setStartBusStop(String startBusStop) {
        StartBusStop = startBusStop;
    }

    public String getEndBusStop() {
        return EndBusStop;
    }

    public void setEndBusStop(String endBusStop) {
        EndBusStop = endBusStop;
    }

    @Override
    public int compareTo(BusServiceClass o) {
        return this.getServiceNo().compareTo(o.getServiceNo());
    }
}
