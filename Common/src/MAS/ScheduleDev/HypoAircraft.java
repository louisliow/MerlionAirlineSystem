package MAS.ScheduleDev;

import MAS.Entity.Aircraft;
import MAS.Entity.Airport;

public class HypoAircraft extends ScheduleDevelopmentClass implements Comparable<HypoAircraft> {
    public Aircraft aircraft;
    public Airport startingAp;
    public Airport location;
    public Airport prevLocation;
    public boolean reqMaint = false;
    public boolean routeHome = false;

    @Override
    public int compareTo(HypoAircraft another) {
        if (this.aircraft.getFlyingCost() < another.aircraft.getFlyingCost())
            return -1;
        else if (this.aircraft.getFlyingCost() > another.aircraft.getFlyingCost())
            return 1;
        return 0;
    }
}
