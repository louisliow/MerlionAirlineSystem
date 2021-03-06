package MAS.Bean;

import MAS.Common.Constants;
import MAS.Common.SeatConfigObject;
import MAS.Entity.*;
import MAS.Exception.BookingException;
import MAS.Exception.NotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless(name = "BookFlightEJB")
public class BookFlightBean {
    @PersistenceContext
    private EntityManager em;
    @EJB
    PNRBean pnrBean;
    @EJB
    BookingClassBean bookingClassBean;
    @EJB
    CostsBean costsBean;

    public BookFlightBean() {
    }

    public long seatsLeft(Flight flight, int travelClass) {
        SeatConfigObject seatConfigObject = new SeatConfigObject();
        seatConfigObject.parse(flight.getAircraftAssignment().getAircraft().getSeatConfig().getSeatConfig());
        int totalSeatsInClass = seatConfigObject.getSeatsInClass(travelClass);
        long seatsBookedInClass = (long) em.createQuery("SELECT COUNT(e) FROM ETicket e WHERE e.flight = :flight AND e.travelClass = :travelClass").setParameter("flight", flight).setParameter("travelClass", travelClass).getSingleResult();
        return totalSeatsInClass - seatsBookedInClass;
    }

    public PNR bookFlights(List<BookingClass> bookingClasses, List<String> passengerNames) throws BookingException {
        for (int i = 0; i < bookingClasses.size(); i++) {
            try {
                bookingClasses.set(i, bookingClassBean.getBookingClass(bookingClasses.get(i).getId()));
            } catch (NotFoundException e) {
                throw new BookingException();
            }
        }

        // Ensure enough seats available on all booking class and flights before proceeding
        for (BookingClass bookingClass : bookingClasses) {
            if (bookingClass.getAllocation() - bookingClass.getOccupied() < passengerNames.size()) {
                throw new BookingException("Not enough seats on booking class.");
            }
            if (!bookingClass.isOpen()) {
                throw new BookingException("Booking class not open!");
            }
            if (seatsLeft(bookingClass.getFlight(), bookingClass.getTravelClass()) < passengerNames.size()) {
                throw new BookingException("Not enough seats in travel class.");
            }
        }

        // Subtract seats from booking class
        for (BookingClass bookingClass : bookingClasses) {
            bookingClass.setOccupied(bookingClass.getOccupied() + passengerNames.size());
            em.merge(bookingClass);
        }

        PNR pnr = new PNR();
        List<Itinerary> itineraries = new ArrayList<>();
        List<SpecialServiceRequest> SSRs = new ArrayList<>();

        // Populate itinerary
        for (BookingClass bookingClass : bookingClasses) {
            Flight flight = bookingClass.getFlight();
            Itinerary itinerary = new Itinerary();
            itinerary.setOrigin(flight.getAircraftAssignment().getRoute().getOrigin().getId());
            itinerary.setDestination(flight.getAircraftAssignment().getRoute().getDestination().getId());
            itinerary.setDepartureDate(flight.getDepartureTime());
            itinerary.setArrivalDate(flight.getArrivalTime());
            itinerary.setBookingClass(bookingClass.getName());
            itinerary.setFlightCode(flight.getCode());
            itineraries.add(itinerary);
        }
        pnr.setItineraries(itineraries);

        // Set passengers
        pnr.setPassengers(passengerNames);

        ArrayList<ETicket> eTickets = new ArrayList<>();

        // Issue eTicket for each flight
        for (BookingClass bookingClass : bookingClasses) {
            for (String passengerName : passengerNames) {
                ETicket eTicket = new ETicket();
                eTicket.setCreated(new Date());
                eTicket.setBookingClass(bookingClass);
                eTicket.setFlight(bookingClass.getFlight());
                eTicket.setPassengerName(passengerName);
                eTicket.setTravelClass(bookingClass.getTravelClass());
                eTicket.setSeatNumber(-1);
                em.persist(eTicket);
                em.flush();
                try {
                    SpecialServiceRequest ssr = new SpecialServiceRequest();
                    ssr.setPassengerNumber(pnrBean.getPassengerNumber(pnr, passengerName));
                    ssr.setItineraryNumber(pnrBean.getItineraryNumber(pnr, bookingClass.getFlight().getCode()));
                    ssr.setActionCode(Constants.SSR_ACTION_CODE_TICKET_NUMBER);
                    ssr.setValue(eTicket.getId().toString());
                    SSRs.add(ssr);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
                eTickets.add(eTicket);
            }
        }
        pnr.setCreated(new Date());
        pnr.setSpecialServiceRequests(SSRs);
        em.persist(pnr);
        em.flush();
        for (ETicket eTicket : eTickets) {
            eTicket.setPnr(pnr);
            em.persist(eTicket);
        }
        return pnr;
    }

    public void addPartnerCost(double price, String partnerCode) {
        try {
            if (Arrays.asList(Constants.PARTNERS).indexOf(partnerCode) != -1) {
                costsBean.createCost(Constants.COST_ANNUAL, Constants.PARTNER_COMMISSION[Arrays.asList(Constants.PARTNERS).indexOf(partnerCode)] * price, "Commission for " + partnerCode, -1);
            }
        } catch (Exception e) {
            //
        }
    }

}
