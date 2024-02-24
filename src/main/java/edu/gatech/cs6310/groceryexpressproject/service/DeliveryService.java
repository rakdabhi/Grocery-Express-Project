package edu.gatech.cs6310.groceryexpressproject.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.Scanner;

import edu.gatech.cs6310.groceryexpressproject.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    @Autowired
    private StoreService storeService;
    @Autowired
    private DronePilotService dronePilotService;
    @Autowired
    private DroneService droneService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisCacheManager cacheManager;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ClockService clockService;
    // A map of how much time it takes to execute the following commands (all set to 1 for simplicity)
    private Map<String, Integer> commandTimeMap = new HashMap<String, Integer>() {{
        put("make_store", 1);
        put("sell_item", 1);
        put("make_pilot", 1);
        put("make_drone", 1);
        put("fly_drone", 1);
        put("make_customer", 1);
        put("start_order", 1);
        put("request_item", 1);
        put("cancel_order", 1);
        put("transfer_order", 1);
    }};

    public void commandLoop() {
        evictAllCaches(); // clear all cache on startup
        clockService.initializeClock();
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";

        label: while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);

                switch (tokens[0]) {
                    case "make_store":
                        if (tokens.length >= 5) {
                            // System.out.println("store: " + tokens[1] + ", revenue: " + tokens[2] + ", x: " + tokens[3] + ", y: " + tokens[4]);
                            if (isValidInteger(tokens[2]) && isValidInteger(tokens[3]) && isValidInteger(tokens[4])) {
                                make_store(tokens[1], tokens[2], tokens[3], tokens[4]);
                            } else {
                                System.out.println("ERROR: Invalid input. Revenue, x-coordinate, and y-coordinate must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_stores":
                        // System.out.println("no parameters needed");
                        display_stores();
                        break;

                    case "sell_item":
                        // System.out.println("store: " + tokens[1] + ", item: " + tokens[2] + ",
                        // weight: " + tokens[3]);
                        if (tokens.length >= 4) {
                            if (isValidInteger(tokens[3])) {
                                sell_item(tokens[1], tokens[2], tokens[3]);
                            } else {
                                System.out.println("ERROR: Invalid input. Weight must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_items":
                        if(tokens.length >= 2) {
                            // System.out.println("store: " + tokens[1]);
                            display_items(tokens[1]);
                        }
                        break;

                    case "make_pilot":
                         if(tokens.length >= 8) {
                             // System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ",
                             // last_name: " + tokens[3]);
                             // System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ",
                             // license: " + tokens[6] + ", experience: " + tokens[7]);
                             if (isValidInteger(tokens[7])) {
                                 make_pilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                             } else {
                                 System.out.println("ERROR: Invalid input. Experience must be a valid integer.");
                             }
                         } else {
                             System.out.println("ERROR: Insufficient number of parameters");
                         }
                        break;

                    case "display_pilots":
                        // System.out.println("no parameters needed");
                         display_pilots();
                        break;

                    case "make_drone":
                        if (tokens.length >= 8) {
                            // System.out.println( "store: " + tokens[1] + ", drone: " + tokens[2] +
                            // ", capacity: " + tokens[3] + ", fuelCapacity: " + tokens[4] +
                            // ", refuelRate: " + tokens[5] + ", fuelConsumptionRate: " + tokens[6] +
                            // ", speed: " + tokens[7]);
                            if (isValidInteger(tokens[3]) && isValidInteger(tokens[4]) && isValidInteger(tokens[5]) && isValidInteger(tokens[6]) && isValidInteger(tokens[7])) {
                                make_drone(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                            } else {
                                System.out.println("ERROR: Invalid input. WeightCapacity, FuelCapacity, RefuelRate, FuelConsumptionRate, and Speed must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_drones":
                        if(tokens.length >= 2) {
                            // System.out.println("store: " + tokens[1]);
                            display_drones(tokens[1]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "fly_drone":
                        if(tokens.length >= 4) {
                            // System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ",
                            // pilot: " + tokens[3]);
                            fly_drone(tokens[1], tokens[2], tokens[3]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "make_customer":
                        // System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3] + ", phone: " + tokens[4] + 
                        // ", rating: " + tokens[5] + ", credit: " + tokens[6] + ", x_coordinate: " + tokens[7] + ", y_coordinate: " + tokens[8]);
                        if (tokens.length == 9) {
                            if (isValidInteger(tokens[5]) && isValidInteger(tokens[6]) && isValidInteger(tokens[7]) && isValidInteger(tokens[8])) {
                                make_customer(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7], tokens[8]);
                            } else {
                                System.out.println("ERROR: Invalid input. CustomerRating, Credit, x-coordinate, and y-coordinate must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Invalid number of parameters");
                        }
                        break;

                    case "display_customers":
                        // System.out.println("no parameters needed");
                         display_customers();
                        break;

                    case "start_order":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ",
                        // drone: " + tokens[3] + ", customer: " + tokens[4], + ", expectedDeliveryTime: " + tokens[5]);
                        if (tokens.length >= 6) {
                            if (isValidInteger(tokens[5])) {
                                start_order(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                            } else {
                                System.out.println("ERROR: Invalid input. ExpectedDeliveryTime must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_orders":
                        if(tokens.length >= 2) {
                            // System.out.println("store: " + tokens[1]);
                            display_orders(tokens[1]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "request_item":
                        if(tokens.length >= 6) {
                            // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ",
                            // item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
                            if (isValidInteger(tokens[4]) && isValidInteger(tokens[5])) {
                                request_item(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                            } else {
                                System.out.println("ERROR: Invalid input. Quantity and UnitPrice must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "purchase_order":
                        if(tokens.length >= 3) {
                            // System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                            purchase_order(tokens[1], tokens[2]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "cancel_order":
                        if(tokens.length >= 3){
                            // System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                            cancel_order(tokens[1], tokens[2]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "transfer_order":
                        if(tokens.length >= 4) {
                            // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ",
                            // new_drone: " + tokens[3]);
                            transfer_order(tokens[1], tokens[2], tokens[3]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_efficiency":
                        // System.out.println("no parameters needed");
                         display_efficiency();
                        break;

                    case "display_distance":
                        if(tokens.length >= 3) {
                            // System.out.println("store: " + tokens[1] + ", customer: " + tokens[2]);
                            display_distance(tokens[1], tokens[2]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;

                    case "display_time":
                        // System.out.println("no parameters needed");
                        display_time();
                        break;

                    case "set_time":
                        if (tokens.length >= 2) {
                            // System.out.println("time: " + tokens[1]);
                            if (isValidInteger(tokens[1])) {
                                set_time(tokens[1]);
                            }
                            else {
                                System.out.println("ERROR: Invalid input. New Time must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;
                    case "export_customers":
                        // System.out.println("no parameters needed");
                        export_customers();
                        break;
                    case "import_customers":
                        if (tokens.length >= 2) {
                            // System.out.println("fileName: " + tokens[1]);
                            import_customers(tokens[1]);
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;
                    case "purge_customers":
                        // System.out.println("no parameters needed");
                        purge_customers();
                        break;
                    case "display_archive":
                        // System.out.println("no parameters needed");
                        display_archive();
                        break;
                    case "clean_archive":
                        // System.out.println("no parameters needed");
                        clean_archive();
                        break;
                    case "test_cache":
                        if (tokens.length >= 2) {
                            if (isValidInteger(tokens[1])) {
                                testCache(tokens[1]);
                            } else {
                                System.out.println("ERROR: Invalid input. NumberOfCustomers must be a valid integer.");
                            }
                        } else {
                            System.out.println("ERROR: Insufficient number of parameters");
                        }
                        break;
                    case "stop":
                        System.out.println("stop acknowledged");
                        evictAllCaches(); // evict all caches when stopping
                        break label;

                    default:
                        if (!tokens[0].startsWith("//")) {
                            System.out.println("command " + tokens[0] + " NOT acknowledged");
                        }
                        break;
                }
                // Increments time on the clock based on the command executed
                if (commandTimeMap.containsKey(tokens[0])) {
                    clockService.incrementTime(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
            }
        }

        System.out.println("simulation terminated");
        commandLineInput.close();
    }

    private void import_customers(String fileName) {
        customerService.importCustomersFromCSV(fileName);
        System.out.println("OK:import_completed");
    }

    private void display_archive() {
        customerService.displayArchivedCustomerFiles();
        System.out.println("OK:display_completed");
    }

    private void clean_archive() {
        customerService.cleanArchive();
        System.out.println("OK:change_completed");
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    private void testCache(String numberOfObjectsToCreate) {
        System.out.println("testing cache...");
        final int objectsToCreate = Integer.parseInt(numberOfObjectsToCreate);

        //create customers
        for (int i =0; i< objectsToCreate; i++) {
            Location loc = locationService.createLocation(new Location(i,i));
            Customer c = new Customer("accountID" + i, "fName", "Lname", "909090909", 5, 1000, loc);
            customerService.createCustomer(c);
        }

        //on the first iteration we get the customers from the db and lazily add them to the cache
        //on the second iteration, we get the customers from the cache
        for (int getIteration = 0; getIteration < 2; getIteration++) {
            long before = System.currentTimeMillis();
            for (int i =0; i < objectsToCreate; i++) {
                Customer customer = customerService.findByAccountID("accountID" + i);
            }
            long after = System.currentTimeMillis();

            String isCached = getIteration == 1 ? "cached" : "database";
            System.out.println("Time to fetch "+ isCached +" Customers : " + (after - before) + "ms");
        }

    }

    private void export_customers() throws IOException {
        try {
            int time = clockService.getCurrentTime();
            customerService.exportCustomersToCSV(time);
            System.out.println("OK:export_completed");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR:export_failed");
        }
    }

    private void purge_customers() {
        customerService.purgeCustomers();
    }

    private void set_time(String time) {
        clockService.setCurrentTime(Integer.parseInt(time));
        System.out.println("OK:change_completed");
    }

    private void display_time() {
        Clock clock = clockService.getClock();
        System.out.println("Time: " + clock.toString());
        System.out.println("OK:display_completed");
    }

    private void display_distance(String storeID, String customerID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
        }
        if (!customerService.isCustomerExists(customerID)) {
            System.out.println("ERROR:customer_identifier_does_not_exist");
        }
        Location storeLocation = storeService.getStoreByName(storeID).getLocation();
        Location customerLocation = customerService.findByAccountID(customerID).getLocation();
        int distance = locationService.computeDistance(storeLocation, customerLocation);
        System.out.println("Distance: " + distance);
        System.out.println("OK:display_completed");
    }

    /**
     * Method to create a store if it doesn't exist
     * 
     * @param storeID - unique ID of store to create
     * @param revenue - revenue of store as an integer
     * @return true if store is created, else false
     */
    private boolean make_store(String storeID, String revenue, String x, String y) {
        if (storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_already_exists");
            return false;
        }
        Location location = new Location(Integer.parseInt(x), Integer.parseInt(y));
        if (locationService.isLocationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        locationService.createLocation(location);
        int revenueValue;
        try {
            revenueValue = Integer.parseInt(revenue);
        } catch (NumberFormatException e) {
            System.out.println("ERROR:invalid_revenue");
            return false;
        }

        Store store = new Store(storeID, revenueValue, location);
        storeService.createStore(store);
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Displays all stores, ordered on the stores' names
     */
    private void display_stores() {
        List<Store> stores = storeService.getAllStores();
        for (Store store : stores) {
            System.out.println("name:" + store.getName() + ",revenue:" + store.getRevenue());
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Adds an item to the catalog of items available to be requested and purchased
     * from a particular store
     * 
     * @param storeID    - unique ID of store to add item to
     * @param itemName   - name of item
     * @param itemWeight - weight of item
     * @return true if item is added to store's catalog, else false
     */
    private boolean sell_item(String storeID, String itemName, String itemWeight) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Item item = new Item(itemName, Integer.parseInt(itemWeight), storeService.getStoreByName(storeID));
        Store store = storeService.getStoreByName(storeID);
        return storeService.createStoreItem(item, store);
    }

    /**
     * Displays all items that are available for request/purchase at a specific
     * store, ordered on the items' names
     * 
     * @param storeID - unique ID of store
     */
    private void display_items(String storeID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        Store store = storeService.getStoreByName(storeID);
        storeService.displayItems(store);
    }

    /**
     * Creates a drone pilot who could fly a drone later to support grocery deliveries
     * @param pilotID - unique ID to represent pilot
     * @param firstName - first name of pilot
     * @param lastName - last name of pilot
     * @param phone - phone number of pilot
     * @param taxID - tax identifier of pilot
     * @param licenceID - unique licence ID of pilot
     * @param experience - number of deliveries completed successfully
     * @return true if drone pilot is created, else false
     */
    private boolean make_pilot(String pilotID, String firstName, String lastName,
                               String phone, String taxID, String licenceID, String experience) {
        DronePilot pilot = new DronePilot(pilotID, firstName, lastName, phone, taxID, licenceID, experience);
        if (dronePilotService.createDronePilot(pilot) != null) {
            System.out.println("OK:change_completed");
            return true;
        }
        return false;
    }

    /**
     * Displays all employees, ordered on the employees' account IDs
     */
    private void display_pilots() {
        List<DronePilot> pilots = dronePilotService.getAllDronePilots();
        for (DronePilot pilot : pilots) {
            System.out.println(pilot.toString());
        }
        System.out.println("OK:display_completed");
    }
    /**
     * Makes a new drone and adds it to the store.
     *
     * @param  storeID                 the ID of the store
     * @param  droneID                 the ID of the drone
     * @param  weightCapacity          the weight capacity of the drone
     * @param  fuelCapacity            the fuel capacity of the drone
     * @param  refuelRate              the refuel rate of the drone
     * @param  fuelConsumptionRate     the fuel consumption rate of the drone
     * @param  speed                   the speed of the drone
     * @return                         true if the drone is successfully created and added, false otherwise
     */
    private boolean make_drone(String storeID,
                               String droneID,
                               String weightCapacity,
                               String fuelCapacity,
                               String refuelRate,
                               String fuelConsumptionRate,
                               String speed)
    {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (droneService.isDroneExists(droneID, storeID)) {
            System.out.println("ERROR:drone_identifier_already_exists");
            return false;
        }
        Store store = storeService.getStoreByName(storeID);
        Drone drone = new Drone(droneID, weightCapacity, store, fuelCapacity, refuelRate, fuelConsumptionRate, speed);
        if (droneService.createDrone(drone) != null) {
            System.out.println("OK:change_completed");
            return true;
        }
        return false;
    }

    /**
     * Displays all drones that a particular store owns, ordered on the drones' unique IDs
     * @param storeID - unique ID of store that owns the drones
     */
    private void display_drones(String storeID) {
        if(!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        Store store = storeService.getStoreByName(storeID);
        List<Drone> drones = droneService.getAllDrones(store);
        for (Drone drone : drones) {
            System.out.println(drone.toString());
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Assigns a given pilot to take control of a given drone
     * @param storeID - unique ID of store where drone works
     * @param droneID - unique ID of the drone
     * @param pilotID - unique identifier for pilot
     */
    private void fly_drone(String storeID, String droneID, String pilotID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        if (!droneService.isDroneExists(droneID, storeID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return;
        }
        if (!dronePilotService.isPilotExists(pilotID)) {
            System.out.println("ERROR:pilot_identifier_does_not_exist");
            return;
        }
        Drone drone = droneService.getDrone(droneID, storeID);
        //retrieves the pilot (i.e. Pilot B) that is currently assigned to the drone
        DronePilot pilotB = drone.getPilot();
        if(pilotB != null) {
            //If pilotB is not null, it means there was a pilot already assigned to the drone
            //removes the pilot B from database (to avoid a foreign key constraint error)
            dronePilotService.deleteDronePilot(pilotB);
        }
        DronePilot pilotA = dronePilotService.getDronePilot(pilotID);
        //assigns the drone to pilot A
        pilotA.setDrone(drone);
        //updates the pilot A in the database
        dronePilotService.updateDronePilot(pilotA);
        if(pilotB != null) {
            //add back pilot B to the database
            DronePilot newPilotB = new DronePilot(pilotB.getAccountID(), pilotB.getFirstName(), pilotB.getLastName(), pilotB.getPhone(), pilotB.getTaxID(), pilotB.getLicenseID(), pilotB.getExperience());
            dronePilotService.createDronePilot(newPilotB);
        }
        System.out.println("OK:change_completed");
    }

    /**
     * Creates a customer if they don't exist
     * @param accountID - unique ID of customer
     * @param firstName - first name of customer
     * @param lastName - last name of customer
     * @param phone - phone number of customer
     * @param rating - rating of customer
     * @param credit - credit/money that customer has available to spend on groceries
     * @return true if customer is created, else false
     */
    private boolean make_customer(String accountID, String firstName, String lastName,
                                  String phone, String rating, String credit, String x, String y) {
        if (customerService.isCustomerExists(accountID)) {
            System.out.println("ERROR:customer_identifier_already_exists");
            return false;
        }
        Location location = new Location(Integer.parseInt(x), Integer.parseInt(y));
        if (locationService.isLocationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        locationService.createLocation(location);
        Customer customer = new Customer(accountID, firstName, lastName, phone, Integer.parseInt(rating), Integer.parseInt(credit), location);
        customerService.createCustomer(customer);
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Displays all customers
     */
    private void display_customers() {
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer customer : customers) {
            System.out.println(customer.toString());
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Creates an initial stub for an order if it doesn't exist
     * @param storeID - unique ID of store where order will be placed
     * @param orderID - unique ID of order
     * @param droneID - unique ID of drone that will carry order
     * @param customerID - unique ID of customer who is initiating the order
     * @return true is order is created, else false
     */
    private boolean start_order(String storeID, String orderID, String droneID, String customerID, String expectedDeliveryTime) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (orderService.isOrderExists(orderID, storeService.getStoreByName(storeID))) {
            System.out.println("ERROR:order_identifier_already_exists");
            return false;
        }
        if (!droneService.isDroneExists(droneID, storeID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        if (!customerService.isCustomerExists(customerID)) {
            System.out.println("ERROR:customer_identifier_does_not_exist");
            return false;
        }
        Drone drone = droneService.getDrone(droneID, storeID);
        Customer customer = customerService.findByAccountID(customerID);
        Order order = new Order(orderID, customer, storeService.getStoreByName(storeID), drone, Integer.parseInt(expectedDeliveryTime));
        if (order.getOrderDistance() > drone.getMaxFlightRange()) {
            System.out.println("ERROR:order_distance_exceeded_drone_max_flight_range");
            return false;
        }
        if (orderService.createOrder(order) != null) {
            System.out.println("OK:change_completed");
            return true;
        }
        return false;
    }
    /**
     * Displays all orders that a store has
     * @param storeID - unique ID of store that has orders
     */
    private void display_orders(String storeID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        Store store = storeService.getStoreByName(storeID);
        orderService.displayOrders(store);
    }

    /**
     * Add an item to the designated order
     * @param storeID - unique ID of store that holds item
     * @param orderID - unique ID of order that will add the item
     * @param itemID - unique ID of item
     * @param quantity - quantity of item
     * @param price - unit price of item
     * @return true if item is added to order, else false
     */
    private boolean request_item(String storeID, String orderID, String itemID, String quantity, String price) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Store store = storeService.getStoreByName(storeID);
        if (!orderService.isOrderExists(orderID, store)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (!storeService.isItemExists(itemID, store)) {
            System.out.println("ERROR:item_identifier_does_not_exist");
            return false;
        }
        if (orderService.isItemOrdered(orderID, itemID, store)) {
            System.out.println("ERROR:item_already_ordered");
            return false;
        }
        Order order = orderService.getOrder(orderID, store);
        Customer customer = order.getCustomer();
        if (customer == null || !customerService.canAffordItem(customer, Integer.parseInt(price), Integer.parseInt(quantity))) {
            System.out.println("ERROR:customer_cant_afford_new_item");
            return false;
        }
        Item item = storeService.getItem(itemID, store);
        Drone drone = order.getDrone();
        if (drone == null || !droneService.canCarryItem(drone, item, Integer.parseInt(quantity))) {
            System.out.println("ERROR:drone_cant_carry_new_item");
            return false;
        }
        orderService.addItemToOrder(order, item, Integer.parseInt(quantity), Integer.parseInt(price));
        droneService.updateDroneRemainingWeight(drone, item, Integer.parseInt(quantity));
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Customer purchases an order they placed at a store and system information is updated accordingly
     * @param storeID - unique ID of store where order is to be purchased from
     * @param orderID - unique ID of order to be purchased
     * @return true if order is purchased, else false
     */
    private boolean purchase_order(String storeID, String orderID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Store store = storeService.getStoreByName(storeID);
        if (!orderService.isOrderExists(orderID, store)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        boolean orderPurchased = orderService.purchaseOrder(orderID, store);
        return orderPurchased;
    }

    /**
     * Customer cancels an order they placed at a store
     * @param storeID - unique ID of store where order is to be cancelled from
     * @param orderID - unique ID of order to be cancelled
     * @return true if order is cancelled, else false
     */
    private boolean cancel_order(String storeID, String orderID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Store store = storeService.getStoreByName(storeID);
        if (!orderService.isOrderExists(orderID, store)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orderService.getOrder(orderID, store);
        boolean orderCanceled = orderService.cancelOrder(order);
        if (orderCanceled) System.out.println("OK:change_completed");
        return orderCanceled;
    }

    /**
     * Transfers an order from one drone to another drone within the particular store that the order is made from
     * @param storeID - unique ID of store
     * @param orderID - unique ID of order
     * @param droneID - unique ID of new drone that order will be transferred to
     * @return true if order is transferred, else false
     */
    private boolean transfer_order(String storeID, String orderID, String droneID) {
        if (!storeService.isStoreExists(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Store store = storeService.getStoreByName(storeID);
        if (!orderService.isOrderExists(orderID, store)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if(!droneService.isDroneExists(droneID, storeID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        Order order = orderService.getOrder(orderID, store);
        Drone newDrone = droneService.getDrone(droneID, storeID);
        boolean isTransferred = orderService.transferOrder(order, newDrone);
        if (isTransferred) System.out.println("OK:change_completed");
        return isTransferred;
    }

    /**
     * Displays information about three metrics for each store (purchases, overloads, and transfers)
     */
    private void display_efficiency() {
        storeService.displayEfficiency();
    }


    /**
     * Determines whether the given string is a valid integer.
     *
     * @param  string  the string to be checked
     * @return         true if the string is a non-negative integer, false otherwise
     */
    public static boolean isValidInteger(String string) {
        try {
            int parsedValue = Integer.parseInt(string);
            if (parsedValue < 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
