package MAS.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Aircraft {

    @Column(unique = true)
    private String tailNumber;

    @Basic
    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    private AircraftSeatConfig seatConfig;

    @ManyToOne
    public AircraftSeatConfig getSeatConfig() {
        return seatConfig;
    }

    public void setSeatConfig(AircraftSeatConfig seatConfig) {
        this.seatConfig = seatConfig;
    }

    @Basic
    @Temporal(TemporalType.DATE)
    private Date manufacturedDate;

    public Date getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(Date manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }

    @GeneratedValue
    @Id
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    private double flyingCost;

    public double getFlyingCost() {
        return flyingCost;
    }

    public void setFlyingCost(double flyingCost) {
        this.flyingCost = flyingCost;
    }

    @Basic
    private double maxRange;

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double range) {
        this.maxRange = range;
    }

    @Basic
    private int milesSinceLastMaint;

    public int getMilesSinceLastMaint() {
        return milesSinceLastMaint;
    }

    public void setMilesSinceLastMaint(int milesSinceLastMaint) {
        this.milesSinceLastMaint = milesSinceLastMaint;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Aircraft && this.id == ((Aircraft) obj).id;
    }

    @ManyToOne
    private Airport currentLocation;

    public Airport getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Airport currentLocation) {
        this.currentLocation = currentLocation;
    }
}
