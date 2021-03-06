package MAS.Bean;

import MAS.Common.Cabin;
import MAS.Common.Constants;
import MAS.Common.SeatConfigObject;
import MAS.Common.Utils;
import MAS.Entity.*;
import MAS.Exception.NoItemsCreatedException;
import MAS.Exception.NotFoundException;
import MAS.Exception.ScheduleClashException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless(name = "FlightScheduleEJB")
@LocalBean
public class FlightScheduleBean {
    @PersistenceContext
    EntityManager em;

    @EJB
    FareRuleBean fareRuleBean;
    @EJB
    BookingClassBean bookingClassBean;
    @EJB
    CostsBean costsBean;
    @EJB
    AttributesBean attributesBean;
    @EJB
    FleetBean fleetBean;

    @EJB
    FFPBean ffpBean;
    @EJB
    CustomerLogBean customerLogBean;
    @EJB
    CustomerBean customerBean;
    @EJB
    PartnerMilesBean partnerMilesBean;

    public FlightScheduleBean() {
    }

    //-----------------Flights---------------------------
    public long createFlight(String code, Date departureTime, Date arrivalTime, long aircraftAssignmentId) throws NotFoundException, ScheduleClashException {
        Flight flight = new Flight();
        flight.setCode(code);
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureTime(departureTime);
        AircraftAssignment aircraftAssignment = em.find(AircraftAssignment.class, aircraftAssignmentId);
        if (aircraftAssignment == null) throw new NotFoundException();
        flight.setAircraftAssignment(aircraftAssignment);
        if (checkScheduleClash(aircraftAssignment.getAircraft().getId(), departureTime, arrivalTime)) {
            throw new ScheduleClashException();
        }
        em.persist(flight);
        em.flush();
        return flight.getId();
    }

    public long createFlight(String code, Date departureTime, Date arrivalTime, long aircraftAssignmentId, boolean createBkingClass) throws NotFoundException, ScheduleClashException {
        long flightId = createFlight(code, departureTime, arrivalTime, aircraftAssignmentId);
        if (createBkingClass)
            createDefaultBookingClasses(flightId);

        return  flightId;
    }

    private void createDefaultBookingClasses(long flightId) throws  NotFoundException {
        Flight flight = em.find(Flight.class, flightId);
        FareRule fareN = fareRuleBean.getFareRuleByName(Constants.FARE_NORMAL);
        FareRule fareE = fareRuleBean.getFareRuleByName(Constants.FARE_EARLY);
        FareRule fareL = fareRuleBean.getFareRuleByName(Constants.FARE_LATE);
        FareRule fareD = fareRuleBean.getFareRuleByName(Constants.FARE_DOUBLE);
        FareRule fareEx = fareRuleBean.getFareRuleByName(Constants.FARE_EXPENSIVE);
        SeatConfigObject seatConfigObject = new SeatConfigObject();
        seatConfigObject.parse(flight.getAircraftAssignment().getAircraft().getSeatConfig().getSeatConfig());
        double costPerSeat = costsBean.calculateCostPerSeat(flightId);

        for (int i = 0; i < Cabin.TRAVEL_CLASSES.length; i++) {
            int seatsInClass = seatConfigObject.getSeatsInClass(i);
            int seatsLeft = seatsInClass;
            double cabinPrice = costPerSeat * Constants.TRAVEL_CLASS_PRICE_MULTIPLIER[i];

            bookingClassBean.createBookingClass(Constants.BOOKING_CLASS_NAMES[i * 5 + 0], seatsLeft, i, fareEx, flight, Utils.makeNiceMoney(cabinPrice * fareEx.getPriceMul()), false);
            seatsLeft -= costsBean.getSeatTraffic(seatsInClass, cabinPrice, Utils.makeNiceMoney(cabinPrice * 1.5));
            bookingClassBean.createBookingClass(Constants.BOOKING_CLASS_NAMES[i * 5 + 1], seatsLeft, i, fareL, flight, Utils.makeNiceMoney(cabinPrice * fareL.getPriceMul()), false);
            seatsLeft -= costsBean.getSeatTraffic(seatsInClass, cabinPrice, Utils.makeNiceMoney(cabinPrice * 1.35));
            bookingClassBean.createBookingClass(Constants.BOOKING_CLASS_NAMES[i * 5 + 2], seatsLeft, i, fareN, flight, Utils.makeNiceMoney(cabinPrice * fareN.getPriceMul()), false);
            bookingClassBean.createBookingClass(Constants.BOOKING_CLASS_NAMES[i * 5 + 3], seatsLeft, i, fareD, flight, Utils.makeNiceMoney(cabinPrice * fareD.getPriceMul()), false);
            seatsLeft -= costsBean.getSeatTraffic(seatsInClass, cabinPrice, Utils.makeNiceMoney(cabinPrice * 1));
            bookingClassBean.createBookingClass(Constants.BOOKING_CLASS_NAMES[i * 5 + 4], seatsLeft, i, fareE, flight, Utils.makeNiceMoney(cabinPrice * fareE.getPriceMul()), false);
        }
    }

    public void changeFlightCode(long id, String code) throws NotFoundException {
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new NotFoundException();
        flight.setCode(code);
        em.persist(flight);
    }

    public void changeFlightTimings(long id, Date departureTime, Date arrivalTime) throws NotFoundException {
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new NotFoundException();
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureTime(departureTime);
        em.persist(flight);
        em.flush();
    }

    public void removeFlight(long id) throws NotFoundException {
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new NotFoundException();
        em.remove(flight);
    }

    public Flight getFlight(long id) throws NotFoundException {
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new NotFoundException();
        return flight;
    }

    public List<Flight> getFlightOfAc(long acId) throws NotFoundException {
        Aircraft aircraft = em.find(Aircraft.class, acId);
        if (aircraft == null) throw new NotFoundException();
        return em.createQuery("SELECT f from Flight f WHERE f.aircraftAssignment.aircraft = :aircraft", Flight.class).setParameter("aircraft", aircraft).getResultList();
    }

    public List<Flight> getFlightWithinDate(Date start, Date end) {
        return em.createQuery("SELECT f from Flight f WHERE f.departureTime >= :start AND f.departureTime <= :end", Flight.class)
                .setParameter("start", start, TemporalType.TIMESTAMP)
                .setParameter("end", end, TemporalType.TIMESTAMP).getResultList();
    }

    public List<Flight> getAllFlights() {
        return em.createQuery("SELECT f from Flight f", Flight.class).getResultList();
    }

    public List<Flight> findFlightByAA(long aaId) throws NotFoundException {
        AircraftAssignment aa = em.find(AircraftAssignment.class, aaId);
        if (aa == null) throw new NotFoundException();
        return em.createQuery("SELECT f from Flight f WHERE f.aircraftAssignment = :aa", Flight.class).setParameter("aa", aa).getResultList();
    }

    public String findSeatConfigOfFlight(long flightId) throws NotFoundException {
        Flight flight = em.find(Flight.class, flightId);
        if (flight == null) throw new NotFoundException();
        return flight.getAircraftAssignment().getAircraft().getSeatConfig().getSeatConfig();
    }

    public long createRecurringFlight(String code, long aircraftAssignmentId, String departureTime, int flightDuration, Date recurringStartDate, Date recurringEndDate, int[] recurringDays) throws NotFoundException, NoItemsCreatedException, ScheduleClashException {
        AircraftAssignment aircraftAssignment = em.find(AircraftAssignment.class, aircraftAssignmentId);
        if (aircraftAssignment == null) throw new NotFoundException();

        long days = TimeUnit.DAYS.convert(recurringEndDate.getTime() - recurringStartDate.getTime(), TimeUnit.MILLISECONDS);

        if (days < 0) {
            throw new NoItemsCreatedException();
        }

        Date currDepartureDate = null;
        Date currArrivalDate = null;

        try {
            currDepartureDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(recurringStartDate) + " " + departureTime);
            currArrivalDate = Utils.minutesLater(currDepartureDate, flightDuration);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> recurringDaysList = new ArrayList<>();
        for (int i = 0; i < recurringDays.length; i++) {
            recurringDaysList.add(recurringDays[i]);
        }

        ArrayList<Flight> flights = new ArrayList<>();
        FlightGroup flightGroup = new FlightGroup();

        for (int i = 0; i <= days; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDepartureDate);
            if (recurringDaysList.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                if (checkScheduleClash(aircraftAssignment.getAircraft().getId(), (Date) currDepartureDate.clone(),(Date) currArrivalDate.clone()))
                    throw new ScheduleClashException();
                Flight flight = new Flight();
                flight.setCode(code);
                flight.setArrivalTime((Date) currArrivalDate.clone());
                flight.setDepartureTime((Date) currDepartureDate.clone());
                flight.setAircraftAssignment(aircraftAssignment);
                flight.setFlightGroup(flightGroup);
                flights.add(flight);

            }
            currDepartureDate.setTime(currDepartureDate.getTime() + 86400000);
            currArrivalDate.setTime(currArrivalDate.getTime() + 86400000);
        }

        if (flights.size() == 0) {
            throw new NoItemsCreatedException();
        }

        flightGroup.setFlights(flights);
        em.persist(flightGroup);
        em.flush();

        return flightGroup.getId();
    }

    public long createRecurringFlight(String code, long aircraftAssignmentId, String departureTime, int flightDuration, Date recurringStartDate, Date recurringEndDate, int[] recurringDays, boolean createBkingClass) throws NotFoundException, NoItemsCreatedException, ScheduleClashException {
        long flightGrpId = createRecurringFlight(code, aircraftAssignmentId, departureTime, flightDuration, recurringStartDate, recurringEndDate, recurringDays);

        if (createBkingClass) {
            FlightGroup flightGroup = em.find(FlightGroup.class, flightGrpId);
            for (int i = 0; i < flightGroup.getFlights().size(); i++) {
                createDefaultBookingClasses(flightGroup.getFlights().get(i).getId());
            }
        }

        return flightGrpId;
    }


    public FlightGroup getFlightGroup(long id) throws NotFoundException {
        FlightGroup flightGroup = em.find(FlightGroup.class, id);
        if (flightGroup == null) throw new NotFoundException();
        return flightGroup;
    }

    public void removeFlightFromFlightGroup(long id) throws NotFoundException {
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new NotFoundException();
        flight.setFlightGroup(null);
        em.persist(flight);
    }

    public void updateRecurringFlight(long flightGroupId, String code, String departureTime, int flightDuration) throws NotFoundException {
        FlightGroup flightGroup = getFlightGroup(flightGroupId);
        for (Flight flight : flightGroup.getFlights()) {
            flight.setCode(code);
            try {
                flight.setDepartureTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(flight.getDepartureTime()) + " " + departureTime));
                flight.setArrivalTime(Utils.minutesLater(flight.getDepartureTime(), flightDuration));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            em.persist(flight);
        }
    }

    public List<Flight> findDepartingFlightsByAirportForCheckIn(Airport baseAirport) {
        return em.createQuery("SELECT f FROM Flight f, AircraftAssignment aa, Route r " +
                "WHERE f.aircraftAssignment = aa AND aa.route = r AND r.origin = :baseAirport AND f.status < 4 " +
                "AND f.departureTime < :date AND f.departureTime > :limitDate", Flight.class)
                .setParameter("baseAirport", baseAirport)
                .setParameter("limitDate", Utils.minutesLater(new Date(), -2880), TemporalType.TIMESTAMP)
                .setParameter("date", Utils.hoursFromNow(48), TemporalType.TIMESTAMP)
                .getResultList();
    }

    public List<Flight> findDepartingFlightsByAirportForGateControl(Airport baseAirport) {
        return em.createQuery("SELECT f FROM Flight f, AircraftAssignment aa, Route r " +
                "WHERE f.aircraftAssignment = aa AND aa.route = r AND r.origin = :baseAirport AND f.status < 6 " +
                "AND f.departureTime < :date AND f.departureTime > :limitDate", Flight.class)
                .setParameter("baseAirport", baseAirport)
                .setParameter("limitDate", Utils.minutesLater(new Date(), -2880), TemporalType.TIMESTAMP)
                .setParameter("date", Utils.hoursFromNow(48), TemporalType.TIMESTAMP)
                .getResultList();
    }

    public List<Flight> findDepartingFlightsByAirportForPassengerService(Airport baseAirport) {
        return em.createQuery("SELECT f FROM Flight f, AircraftAssignment aa, Route r " +
                "WHERE f.aircraftAssignment = aa AND aa.route = r AND r.origin = :baseAirport AND f.status < 6 " +
                "AND f.departureTime < :date", Flight.class)
                .setParameter("baseAirport", baseAirport)
                .setParameter("date", Utils.hoursFromNow(96), TemporalType.TIMESTAMP)
                .getResultList();
    }

    public List<ETicket> getETicketsForFlight(Flight flight) {
        return em.createQuery("SELECT et FROM ETicket et WHERE et.flight = :flight", ETicket.class)
                .setParameter("flight", flight)
                .getResultList();
    }

    public List<Integer> getSeatsTakenForFlight(Flight flight) {
        ArrayList<Integer> seatsTaken = new ArrayList<>();
        List<ETicket> eTickets = getETicketsForFlight(flight);
        for (ETicket eTicket : eTickets) {
            if (eTicket.getSeatNumber() != -1) {
                seatsTaken.add(eTicket.getSeatNumber());
            }
        }
        return seatsTaken;
    }

    public ETicket getETicket(long id) throws NotFoundException {
        ETicket eTicket = em.find(ETicket.class, id);
        if (eTicket == null) throw new NotFoundException();
        return eTicket;
    }

    public List<ETicket> getRelatedETickets(ETicket eTicket) {
        return em.createQuery("SELECT et FROM ETicket et WHERE et.pnr = :pnr AND et.flight = :flight", ETicket.class)
                .setParameter("flight", eTicket.getFlight())
                .setParameter("pnr", eTicket.getPnr())
                .getResultList();
    }

    public void updateETicket(ETicket eTicket) throws NotFoundException {
        if (em.find(ETicket.class, eTicket.getId()) == null) throw new NotFoundException();
        em.merge(eTicket);
    }

    public long createBaggageItem(double weight) {
        Baggage baggage = new Baggage();
        baggage.setWeight(weight);
        em.persist(baggage);
        em.flush();
        return baggage.getId();
    }

    public void removeBaggageItem(long id) throws NotFoundException {
        Baggage baggage = em.find(Baggage.class, id);
        if (baggage == null) throw new NotFoundException();
        em.remove(baggage);
    }

    public Baggage getBaggageItem(long id) throws NotFoundException {
        Baggage baggage = em.find(Baggage.class, id);
        if (baggage == null) throw new NotFoundException();
        return baggage;
    }

    public void updateSingleFlight(Flight flight) throws NotFoundException {
        if (em.find(Flight.class, flight.getId()) == null) throw new NotFoundException();
        em.merge(flight);
    }

    public boolean isSeatAvailable(Flight flight, int seat) {
        return em.createQuery("SELECT COUNT(et) FROM ETicket et WHERE et.flight = :flight AND et.seatNumber = :seatNumber", Long.class)
                .setParameter("flight", flight)
                .setParameter("seatNumber", seat)
                .getSingleResult() == 0;
    }

    public List<ETicket> getCustomerEtickets(Long id) {
        return em.createQuery("SELECT et FROM ETicket et WHERE et.ffpNumber = :ffpNumber", ETicket.class)
                .setParameter("ffpNumber", "MA/" + id)
                .getResultList();
    }

    public int[] getNumFlightsByMonthForDestination(String airportId) throws NotFoundException {
        Airport airport = em.find(Airport.class, airportId);
        if (airport == null) throw new NotFoundException();
        int[] flightCount = new int[12];
        for (int i = 0; i < 12; i++) {
            Date startOfMonth = Utils.getStartOfMonth(i, 0);
            Date endOfMonth;
            if (i < 11)
                endOfMonth = Utils.getStartOfMonth(i + 1, 0);
            else
                endOfMonth = Utils.getStartOfMonth(0, 1);

            flightCount[i] = Integer.parseInt(em.createQuery("SELECT COUNT(f) FROM Flight f WHERE f.aircraftAssignment.route.destination = :destination AND f.departureTime >= :startOfMonth AND f.departureTime < :endOfMonth", Long.class)
                    .setParameter("destination", airport)
                    .setParameter("startOfMonth", startOfMonth)
                    .setParameter("endOfMonth", endOfMonth)
                    .getSingleResult().toString());
        }
        return flightCount;
    }

    public double[] getFlightUtilisationByMonthForDestination(String airportId) throws NotFoundException {
        Airport airport = em.find(Airport.class, airportId);
        if (airport == null) throw new NotFoundException();
        double[] flightUtil = new double[12];
        for (int i = 0; i < 12; i++) {
            double total = 0;
            Date startOfMonth = Utils.getStartOfMonth(i, 0);
            Date endOfMonth;
            if (i < 11)
                endOfMonth = Utils.getStartOfMonth(i + 1, 0);
            else
                endOfMonth = Utils.getStartOfMonth(0, 1);
            List<Flight> flightsThisMonth = em.createQuery("SELECT f FROM Flight f WHERE f.aircraftAssignment.route.destination = :destination AND f.departureTime >= :startOfMonth AND f.departureTime < :endOfMonth", Flight.class)
                    .setParameter("destination", airport)
                    .setParameter("startOfMonth", startOfMonth)
                    .setParameter("endOfMonth", endOfMonth)
                    .getResultList();
            for(int j = 0; j < flightsThisMonth.size(); j++) {
                total += getUtilisationOfFlight(flightsThisMonth.get(j).getId());
            }
            if (flightsThisMonth.size() == 0)
                flightUtil[i] = 0;
            else
                flightUtil[i] = total / flightsThisMonth.size();
        }
        return flightUtil;
    }

    public double getUtilisationOfFlight(long flightId) throws NotFoundException {
        Flight flight = em.find(Flight.class, flightId);
        if (flight == null) throw new NotFoundException();
        double usedSeats = Double.parseDouble(em.createQuery("SELECT COUNT(et) FROM ETicket et WHERE et.flight = :flight AND et.gateChecked = true", Long.class)
                .setParameter("flight", flight)
                .getSingleResult().toString());
        SeatConfigObject seatConfigObject = new SeatConfigObject();
        seatConfigObject.parse(flight.getAircraftAssignment().getAircraft().getSeatConfig().getSeatConfig());
        double maxSeats = seatConfigObject.getTotalSeats();
        return usedSeats/maxSeats;
    }

    public int getFlightSalesVolume(long flightId) throws NotFoundException {
        Flight flight = em.find(Flight.class, flightId);
        if (flight == null) throw new NotFoundException();
        return Integer.parseInt(em.createQuery("SELECT COUNT(et) FROM ETicket et WHERE et.flight = :flight AND et.gateChecked = true", Long.class)
                .setParameter("flight", flight)
                .getSingleResult().toString());
    }

    public int[] getFlightSalesVolumeByMonthForDestination(String airportId) throws NotFoundException {
        Airport airport = em.find(Airport.class, airportId);
        if (airport == null) throw new NotFoundException();
        int[] flightSales = new int[12];
        for (int i=0; i<12; i++) {
            int total = 0;
            Date startOfMonth = Utils.getStartOfMonth(i, 0);
            Date endOfMonth;
            if (i < 11)
                endOfMonth = Utils.getStartOfMonth(i + 1, 0);
            else
                endOfMonth = Utils.getStartOfMonth(0, 1);
            List<Flight> flightsThisMonth = em.createQuery("SELECT f FROM Flight f WHERE f.aircraftAssignment.route.destination = :destination AND f.departureTime >= :startOfMonth AND f.departureTime < :endOfMonth", Flight.class)
                    .setParameter("destination", airport)
                    .setParameter("startOfMonth", startOfMonth)
                    .setParameter("endOfMonth", endOfMonth)
                    .getResultList();
            for (int j = 0; j < flightsThisMonth.size(); j++) {
                total += getFlightSalesVolume(flightsThisMonth.get(j).getId());
            }
            if (flightsThisMonth.size() == 0)
                flightSales[i] = 0;
            else
                flightSales[i] = total / flightsThisMonth.size();
        }
        return flightSales;
    }

    public double[] getSalesVarianceOfFlightForDestination(String airportId) throws NotFoundException {
        int[] flightSalesVolume = getFlightSalesVolumeByMonthForDestination(airportId);
        double aggregate = getAggregateFlightSalesForDestination(airportId);
        double[] salesVariance = new double[12];
        for (int i = 0; i<12; i++ ) {
            if (aggregate == 0) {
                salesVariance[i] = 0;
                continue;
            }
            salesVariance[i] = (double) flightSalesVolume[i] / aggregate;
        }
        return salesVariance;
    }

    public double getAggregateFlightSalesForDestination(String airportId) throws NotFoundException {
        int[] flightSalesForMonth = getFlightSalesVolumeByMonthForDestination(airportId);
        int totalFlightSalesInYear = 0;
        for (int i = 0; i < 12; i++) {
            totalFlightSalesInYear += flightSalesForMonth[i];
        }
        return (double) totalFlightSalesInYear / 12;
    }

    public void departFlight(Flight flight, Date date) {
        flight.setActualDepartureTime(date);
        flight.setStatus(6);
        try {
            fleetBean.changeAircraftLocation(flight.getAircraftAssignment().getAircraft().getId(), flight.getAircraftAssignment().getRoute().getDestination().getId());
            fleetBean.updateAircraftMiles(flight.getAircraftAssignment().getAircraft().getId(), (int)flight.getAircraftAssignment().getRoute().getDistance());
        } catch (Exception e) {}
        int milesFlown = new Double(flight.getAircraftAssignment().getRoute().getDistance()).intValue();
        List<ETicket> etickets = getETicketsForFlight(flight);
        for (ETicket eticket : etickets) {
            String ffp = eticket.getFfpNumber();
            if (ffp == null) continue;
            String[] parts = ffp.split("/");

            if (parts.length != 2) continue;
            if (!Arrays.asList(Constants.FFP_ALLIANCE_LIST_CODE).contains(parts[0])) continue;
            if (!eticket.isGateChecked()) continue;

            try {
                FareRule fareRule = eticket.getBookingClass().getFareRule();

                int miles = (milesFlown * fareRule.getMilesAccrual()) / 100;
                miles = (miles * travelClassMultiplier(eticket)) / 100;

                int eliteMiles = milesFlown;
                eliteMiles = (eliteMiles * travelClassMultiplier(eticket)) / 100;

                if (parts[0].equals("MA")) {
                    Customer customer = customerBean.getCustomer(Long.parseLong(parts[1]));
                    ffpBean.creditEliteMiles(customer.getId(), eliteMiles);
                    ffpBean.creditMiles(customer.getId(), miles);

                    customerLogBean.createCustomerLog(customer.getId(),
                            miles + " Miles earned for flight " + flight.getCode() +
                                    " from " + flight.getAircraftAssignment().getRoute().getOrigin() +
                                    " to " + flight.getAircraftAssignment().getRoute().getDestination(), "miles");
                    customerLogBean.createCustomerLog(customer.getId(),
                            eliteMiles + " Elite Miles earned for flight " + flight.getCode() +
                                    " from " + flight.getAircraftAssignment().getRoute().getOrigin() +
                                    " to " + flight.getAircraftAssignment().getRoute().getDestination(), "elite_miles");
                } else {
                    partnerMilesBean.awardMiles(ffp, miles);
                }
            } catch (NotFoundException e) {
                continue;
            }
        }
    }

    private int travelClassMultiplier(ETicket eticket) {
        switch (eticket.getBookingClass().getTravelClass()) {
            case 0:
                return 150;
            case 1:
                return 125;
            default:
                return 100;
        }
    }

    public boolean checkScheduleClash(long aircraftId, Date startTime, Date endTime) throws NotFoundException {
        Aircraft aircraft = em.find(Aircraft.class, aircraftId);
        if (aircraft == null) throw new NotFoundException();
        List<Flight> flights = em.createQuery("SELECT f from Flight f WHERE f.aircraftAssignment.aircraft = :aircraft AND " +
                "((f.departureTime > :startTime AND f.departureTime < :endTime) " +
                "OR (f.departureTime < :startTime AND f.arrivalTime > :endTime) OR (f.arrivalTime > :startTime AND f.arrivalTime < :endTime))", Flight.class)
                .setParameter("startTime", startTime, TemporalType.TIMESTAMP)
                .setParameter("endTime", endTime, TemporalType.TIMESTAMP)
                .setParameter("aircraft", aircraft).getResultList();
        List<AircraftMaintenanceSlot> maintenanceSlots = em.createQuery("SELECT m from AircraftMaintenanceSlot m WHERE m.aircraft = :aircraft " +
                "AND ((m.startTime > :startTime AND m.startTime < :endTime) OR (m.startTime < :startTime AND FUNCTION('ADDTIME', m.startTime, m.duration) > :endTime) " +
                "OR (FUNCTION('ADDTIME', m.startTime, m.duration) > :startTime AND FUNCTION('ADDTIME', m.startTime, m.duration) < :endTime))", AircraftMaintenanceSlot.class)
                .setParameter("startTime", startTime, TemporalType.TIMESTAMP)
                .setParameter("endTime", endTime, TemporalType.TIMESTAMP)
                .setParameter("aircraft", aircraft).getResultList();
//        String debug = fleetBean.getAircraft(aircraftId).getId() + ": (" + startTime + " - " + endTime + ")\n";
//        for (Flight flight : flights) {
//            debug = debug.concat(flight.getDepartureTime() + " - " + flight.getArrivalTime() + "\n");
//        }
//        for (AircraftMaintenanceSlot slot : maintenanceSlots) {
//            debug = debug.concat(slot.getStartTime() + " / " + slot.getDuration() + "\n");
//        }
//        System.out.println(debug);
        return flights.size() != 0 || maintenanceSlots.size() != 0;
    }

    public Airport getLatestDestination(long aircraftId) throws NotFoundException {
        Aircraft aircraft = fleetBean.getAircraft(aircraftId);
        if (aircraft == null) throw new NotFoundException();
        List<Flight> flights = em.createQuery("SELECT f from Flight f WHERE f.aircraftAssignment.aircraft = :aircraft ORDER BY f.arrivalTime DESC", Flight.class)
                .setParameter("aircraft", aircraft).setMaxResults(1).getResultList();
        if (flights.size() > 0)
            return flights.get(0).getAircraftAssignment().getRoute().getDestination();
        else
            return aircraft.getCurrentLocation();
    }
}
