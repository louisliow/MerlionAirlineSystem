package MAS.Structure;

import MAS.Common.Permissions;

public class Pages {
    public static final Page DASHBOARD = new Page("/App/index");

	// Common Infrastructure: System Admin
    public static final Page LIST_USERS = new Page("/App/SystemAdmin/users", Permissions.MANAGE_USERS);
    public static final Page CREATE_USER = new Page("/App/SystemAdmin/createUser", Permissions.MANAGE_USERS);
    public static final Page UPDATE_USER = new Page("/App/SystemAdmin/updateUser", Permissions.MANAGE_USERS);
    public static final Page SYSTEM_MAINTENANCE = new Page("/App/SystemAdmin/maintenance", Permissions.SYSTEM_MAINTENANCE);

    public static final Page LIST_ROLES = new Page("/App/SystemAdmin/roles", Permissions.MANAGE_ROLES);
    public static final Page CREATE_ROLE = new Page("/App/SystemAdmin/createRole", Permissions.MANAGE_ROLES);
    public static final Page UPDATE_ROLE = new Page("/App/SystemAdmin/updateRole", Permissions.MANAGE_ROLES);

    public static final Page LIST_WORKGROUPS = new Page("/App/SystemAdmin/workgroups", Permissions.MANAGE_WORKGROUPS);
    public static final Page CREATE_WORKGROUP = new Page("/App/SystemAdmin/createWorkgroup", Permissions.MANAGE_WORKGROUPS);
    public static final Page UPDATE_WORKGROUP = new Page("/App/SystemAdmin/updateWorkgroup", Permissions.MANAGE_WORKGROUPS);

    public static final Page VIEW_AUDIT_LOG = new Page("/App/SystemAdmin/auditLog", Permissions.ACCESS_AUDIT_LOGS);

    // Airline Planning System: Fleet Planning Pages
    public static final Page LIST_TYPE = new Page("/App/FleetPlanning/aircraftTypes.xhtml", Permissions.MANAGE_FLEET);
    public static final Page CREATE_TYPE = new Page("/App/FleetPlanning/createAircraftType", Permissions.MANAGE_FLEET);
    public static final Page UPDATE_TYPE = new Page("/App/FleetPlanning/updateAircraftType", Permissions.MANAGE_FLEET);

    public static final Page LIST_AIRCRAFT = new Page("/App/FleetPlanning/aircrafts", Permissions.MANAGE_FLEET);
    public static final Page CREATE_AIRCRAFT = new Page("/App/FleetPlanning/createAircraft", Permissions.MANAGE_FLEET);
    public static final Page UPDATE_AIRCRAFT = new Page("/App/FleetPlanning/updateAircraft", Permissions.MANAGE_FLEET);

    public static final Page CREATE_SEATCONFIG = new Page("/App/FleetPlanning/createSeatConfig", Permissions.MANAGE_FLEET);

    // Airline Planning System: Route Planning Pages
    public static final Page LIST_AIRPORTS = new Page("/App/RoutePlanning/airports", Permissions.MANAGE_ROUTES);
    public static final Page CREATE_AIRPORTS = new Page("/App/RoutePlanning/createAirport", Permissions.MANAGE_ROUTES);
    public static final Page UPDATE_AIRPORTS = new Page("/App/RoutePlanning/updateAirport", Permissions.MANAGE_ROUTES);

    public static final Page LIST_CITIES = new Page("/App/RoutePlanning/cities", Permissions.MANAGE_ROUTES);
    public static final Page CREATE_CITY = new Page("/App/RoutePlanning/createCity", Permissions.MANAGE_ROUTES);
    public static final Page LIST_COUNTRIES = new Page("/App/RoutePlanning/countries", Permissions.MANAGE_ROUTES);
    public static final Page CREATE_COUNTRY = new Page("/App/RoutePlanning/createCountry", Permissions.MANAGE_ROUTES);

    public static final Page LIST_ROUTES = new Page("/App/RoutePlanning/routes", Permissions.MANAGE_ROUTES);
    public static final Page VISUALIZE_ROUTES = new Page("/App/RoutePlanning/routesVisualization", Permissions.MANAGE_ROUTES);
    public static final Page CREATE_ROUTES = new Page("/App/RoutePlanning/createRoute", Permissions.MANAGE_ROUTES);
    public static final Page UPDATE_ROUTES = new Page("/App/RoutePlanning/updateRoute", Permissions.MANAGE_ROUTES);

    public static final Page LIST_AIRCRAFT_ASSIGNMENT = new Page("/App/RoutePlanning/aircraftAssignment", Permissions.MANAGE_ROUTES);
    public static final Page CREATE_AIRCRAFT_ASSIGNMENT = new Page("/App/RoutePlanning/createAircraftAssignment", Permissions.MANAGE_ROUTES);
    public static final Page UPDATE_AIRCRAFT_ASSIGNMENT = new Page("/App/RoutePlanning/updateAircraftAssignment", Permissions.MANAGE_ROUTES);

    // Airline Planning System: Schedule Planning Pages
    public static final Page LIST_FLIGHTS = new Page("/App/FlightPlanning/flights", Permissions.MANAGE_FLIGHT);
    public static final Page CREATE_FLIGHT = new Page("/App/FlightPlanning/createFlight", Permissions.MANAGE_FLIGHT);
    public static final Page CREATE_FLIGHT_SINGLE = new Page("/App/FlightPlanning/createFlightSingle", Permissions.MANAGE_FLIGHT);
    public static final Page CREATE_FLIGHT_RECURRING = new Page("/App/FlightPlanning/createFlightRecurring", Permissions.MANAGE_FLIGHT);
    public static final Page UPDATE_FLIGHT = new Page("/App/FlightPlanning/updateFlight", Permissions.MANAGE_FLIGHT);

    public static final Page LIST_MAINTENANCE_SLOTS = new Page("/App/FlightPlanning/maintenanceSlots", Permissions.MANAGE_FLIGHT);
    public static final Page CREATE_MAINTENANCE_SLOT = new Page("/App/FlightPlanning/createMaintenanceSlot", Permissions.MANAGE_FLIGHT);
    public static final Page UPDATE_MAINTENANCE_SLOT = new Page("/App/FlightPlanning/updateMaintenanceSlot", Permissions.MANAGE_FLIGHT);

    // Airline Planning System: Schedule Development Pages
    public static final Page SCHEDULE_DEVELOPMENT = new Page("/App/ScheduleDev/scheduleDev", Permissions.MANAGE_AIRCRAFT_SCHEDULE);

    // Airline Inventory Subsystem: Price Management Module
    public static final Page LIST_FARE_RULES = new Page("/App/PriceManagement/fareRules", Permissions.MANAGE_FARE_RULES);
    public static final Page CREATE_FARE_RULE = new Page("/App/PriceManagement/createFareRule", Permissions.MANAGE_FARE_RULES);

    public static final Page LIST_BOOKING_CLASSES = new Page("/App/PriceManagement/bookingClasses", Permissions.MANAGE_BOOKING_CLASSES);
    public static final Page CREATE_BOOKING_CLASS = new Page("/App/PriceManagement/createBookingClass", Permissions.MANAGE_BOOKING_CLASSES);

    // Airline Administrative Subsystem: Costs Management
    public static final Page LIST_COSTS = new Page("/App/CostManagement/costs", Permissions.MANAGE_COSTS);
    public static final Page CREATE_COST = new Page("/App/CostManagement/createCost", Permissions.MANAGE_COSTS);

    // AFOS
    public static final Page CREW_CERTIFICATION = new Page("/App/CrewOperations/certification", Permissions.CREW_CERTIFICATION, Permissions.MANAGE_CREW_CERTIFICATION);
    public static final Page CREATE_CREW_CERTIFICATION = new Page("/App/CrewOperations/createCertification", Permissions.CREW_CERTIFICATION);
    public static final Page VIEW_CREW_CERTIFICATION = new Page("/App/CrewOperations/viewCertification", Permissions.CREW_CERTIFICATION, Permissions.MANAGE_CREW_CERTIFICATION);
    public static final Page FLIGHT_BIDDING = new Page("/App/CrewOperations/flightBidding", Permissions.FLIGHT_BID, Permissions.MANAGE_FLIGHT_BID);
    public static final Page FLIGHT_ROSTER = new Page("/App/CrewOperations/flightRoster", Permissions.FLIGHT_BID, Permissions.MANAGE_FLIGHT_BID);
    public static final Page FLIGHT_DEFERMENT = new Page("/App/CrewOperations/flightDeferment", Permissions.FLIGHT_BID, Permissions.MANAGE_FLIGHT_BID);
    public static final Page LIST_FLIGHT_REPORTS = new Page("/App/CrewOperations/flightReports", Permissions.FLIGHT_REPORTING, Permissions.MANAGE_OPERATIONS_REPORTING);
    public static final Page CREATE_FLIGHT_REPORT = new Page("/App/CrewOperations/createFlightReport", Permissions.FLIGHT_REPORTING);
    public static final Page LIST_MAINTENANCE_REPORTS = new Page("/App/CrewOperations/maintenanceReports", Permissions.MAINTENANCE_REPORTING, Permissions.MANAGE_OPERATIONS_REPORTING);
    public static final Page CREATE_MAINTENANCE_REPORT = new Page("/App/CrewOperations/createMaintenanceReport", Permissions.MAINTENANCE_REPORTING);
    public static final Page FLIGHT_SIGNINOUT = new Page("/App/CrewOperations/signInOut", Permissions.FLIGHT_BID);
    public static final Page MAINTENANCE_SHIFTS = new Page("/App/CrewOperations/maintenanceShifts", Permissions.MANAGE_MAINTENANCE_SHIFT);
    public static final Page CREATE_MAINTENANCE_SHIFT = new Page("/App/CrewOperations/createMaintenanceShift", Permissions.MANAGE_MAINTENANCE_SHIFT);

    // DCS: Check In
    public static final Page CHECK_IN = new Page("/App/DepartureControl/checkIn", Permissions.CHECK_IN);
    public static final Page CHECK_IN_2 = new Page("/App/DepartureControl/checkIn2", Permissions.CHECK_IN);

    // DCS: Gate Control
    public static final Page GATE_CHECK = new Page("/App/DepartureControl/gateCheck", Permissions.GATE_CONTROL);
    public static final Page GATE_CHECK_2 = new Page("/App/DepartureControl/gateCheck2", Permissions.GATE_CONTROL);
    public static final Page PASSENGER_SERVICE = new Page("/App/DepartureControl/passengerService", Permissions.PASSENGER_SERVICE);

    // AAS: MANAGEMENT REPORTING
    public static final Page PROFITABILITY_REPORT = new Page("/App/ManagementReporting/profitabilityReport", Permissions.MANAGE_REPORTING);
    public static final Page CREW_REPORT = new Page("/App/ManagementReporting/crewReport", Permissions.MANAGE_REPORTING);
    public static final Page AIRCRAFT_REPORT = new Page("/App/ManagementReporting/aircraftReport", Permissions.MANAGE_REPORTING);
    public static final Page SEASONALITY_REPORT = new Page("/App/ManagementReporting/seasonalityReport", Permissions.MANAGE_REPORTING);
    public static final Page CAMPAIGN_REPORT = new Page("/App/ManagementReporting/campaignReport", Permissions.MANAGE_REPORTING);

    // CRM
    public static final Page LIST_CUSTOMERS = new Page("/App/CustomerRelations/customers", Permissions.MANAGE_CUSTOMERS);
    public static final Page VIEW_CUSTOMER = new Page("/App/CustomerRelations/customer", Permissions.MANAGE_CUSTOMERS);
    public static final Page HELPDESK = new Page("/App/CustomerRelations/Helpdesk/index", Permissions.MANAGE_CUSTOMERS);
    public static final Page HELPDESK_CUSTOMER = new Page("/App/CustomerRelations/Helpdesk/customer", Permissions.MANAGE_CUSTOMERS);
    public static final Page HELPDESK_UPDATE_CUSTOMER_PROFILE = new Page("/App/CustomerRelations/Helpdesk/updateCustomerProfile", Permissions.MANAGE_CUSTOMERS);
    public static final Page HELPDESK_UPDATE_MEMBERSHIP = new Page("/App/CustomerRelations/Helpdesk/updateMembership", Permissions.MANAGE_CUSTOMERS);
    public static final Page HELPDESK_ISSUE_CARD = new Page("/App/CustomerRelations/Helpdesk/issueCard", Permissions.MANAGE_CUSTOMERS);
    public static final Page CUSTOMER_FEEDBACK = new Page("/App/CustomerRelations/customerFeedback", Permissions.MANAGE_CUSTOMERS);
    public static final Page VIEW_CUSTOMER_FEEDBACK = new Page("/App/CustomerRelations/viewCustomerFeedback", Permissions.MANAGE_CUSTOMERS);
    public static final Page CUSTOMER_FLIGHT_BOOKINGS = new Page("/App/CustomerRelations/Helpdesk/flightBookings", Permissions.MANAGE_CUSTOMERS);
    public static final Page SPECIAL_REQUEST_FORM = new Page("/App/CustomerRelations/manageSpecialRequests", Permissions.MANAGE_CUSTOMERS);

    public static final Page VIEW_CAMPAIGNS = new Page("/App/CustomerRelations/campaigns", Permissions.MANAGE_CAMPAIGNS);
    public static final Page CREATE_CAMPAIGN = new Page("/App/CustomerRelations/createCampaign", Permissions.MANAGE_CAMPAIGNS);
    public static final Page UPDATE_CAMPAIGN = new Page("/App/CustomerRelations/viewCampaign", Permissions.MANAGE_CAMPAIGNS);
    public static final Page VIEW_CUSTOMERGROUPS = new Page("/App/CustomerRelations/customerGroups", Permissions.MANAGE_CAMPAIGNS);
    public static final Page CREATE_CUSTOMERGROUP = new Page("/App/CustomerRelations/createCustomerGroup", Permissions.MANAGE_CAMPAIGNS);
    public static final Page UPDATE_CUSTOMERGROUP = new Page("/App/CustomerRelations/viewCustomerGroup", Permissions.MANAGE_CAMPAIGNS);
    public static final Page CUSTOMER_SEGMENTATION = new Page("/App/CustomerRelations/customerSegmentation", Permissions.MANAGE_CAMPAIGNS);
    public static final Page SEND_TARGETED_MAIL = new Page("/App/CustomerRelations/sendMail", Permissions.MANAGE_CAMPAIGNS);
}
