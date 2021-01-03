package sg.tgonet.singtransport.Class;

import java.util.ArrayList;

public class ListOfArrivalClass {

    public BusStopClass busStopClass;
    public ArrayList<ArrivalClass> arrivalClasses;

    public ListOfArrivalClass(BusStopClass busStopClass, ArrayList<ArrivalClass> arrivalClasses) {
        this.busStopClass = busStopClass;
        this.arrivalClasses = arrivalClasses;
    }
}
