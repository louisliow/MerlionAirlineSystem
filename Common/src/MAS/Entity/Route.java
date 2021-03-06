package MAS.Entity;

import javax.persistence.*;

@Entity
public class Route implements Comparable<Route>{
    private long id;

    @GeneratedValue
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private Airport origin;

    @ManyToOne
    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    private Airport destination;

    @ManyToOne
    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    private double distance;

    @Basic
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isSame(Route route) {
        if (route.getOrigin() == origin) {
            if (route.getDestination() == destination)
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(Route another) {
        if (this.getDistance() < another.getDistance())
            return -1;
        else if (this.getDistance() > another.getDistance())
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Route && this.origin == ((Route)obj).getOrigin() && this.destination == ((Route)obj).getDestination();
    }

    public boolean equalsReversed(Object obj) {
        return obj instanceof Route && this.origin == ((Route)obj).getDestination() && this.destination == ((Route)obj).getOrigin();
    }
}
