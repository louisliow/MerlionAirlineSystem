package MAS.ManagedBean.ManagementReporting;

import MAS.Bean.AircraftMaintenanceSlotBean;
import MAS.Bean.FleetBean;
import MAS.Bean.FlightScheduleBean;
import MAS.Entity.Aircraft;
import MAS.Exception.NotFoundException;
import MAS.ManagedBean.Auth.AuthManagedBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
public class AircraftReportManagedBean {
    @EJB
    FleetBean fleetBean;
    @EJB
    FlightScheduleBean flightScheduleBean;
    @EJB
    AircraftMaintenanceSlotBean aircraftMaintenanceSlotBean;
    @ManagedProperty(value="#{authManagedBean}")
    private AuthManagedBean authManagedBean;

    private List<Aircraft> aircrafts;
    private List<Aircraft> selectedAircrafts;
    private List<String> aircraftIdInputs;
    private String aircraftIds;
    private Aircraft aircraft;

    @PostConstruct
    private void init() {
        aircrafts = fleetBean.getAllAircraft();
        selectedAircrafts = new ArrayList<>();
    }

    public void saveAircraft() {
        aircraftIds = "";
        selectedAircrafts = new ArrayList<>();
        for (int i = 0; i < aircraftIdInputs.size(); i++) {
            aircraftIds = aircraftIds.concat(aircraftIdInputs.get(i)).concat("-");
            try {
                selectedAircrafts.add(fleetBean.getAircraft(Long.parseLong(aircraftIdInputs.get(i))));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String getAircraftIds() {
        return aircraftIds;
    }

    public void setAircraftIds(String aircraftIds) {
        this.aircraftIds = aircraftIds;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public List<String> getAircraftIdInputs() {
        return aircraftIdInputs;
    }

    public void setAircraftIdInputs(List<String> aircraftIdInputs) {
        this.aircraftIdInputs = aircraftIdInputs;
    }

    public void setAuthManagedBean(AuthManagedBean authManagedBean) {
        this.authManagedBean = authManagedBean;
    }

    public List<Aircraft> getAircrafts() {
        return aircrafts;
    }

    public void setAircrafts(List<Aircraft> aircrafts) {
        this.aircrafts = aircrafts;
    }

    public List<Aircraft> getSelectedAircrafts() {
        return selectedAircrafts;
    }

    public void setSelectedAircrafts(List<Aircraft> selectedAircrafts) {
        this.selectedAircrafts = selectedAircrafts;
    }
}
