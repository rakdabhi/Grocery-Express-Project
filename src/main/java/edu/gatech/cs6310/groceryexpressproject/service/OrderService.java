package edu.gatech.cs6310.groceryexpressproject.service;

import edu.gatech.cs6310.groceryexpressproject.model.*;
import edu.gatech.cs6310.groceryexpressproject.repository.CustomerRepository;
import edu.gatech.cs6310.groceryexpressproject.repository.LineRepository;
import edu.gatech.cs6310.groceryexpressproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreService storeService;
    @Autowired
    private DroneService droneService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private DronePilotService dronePilotService;
    @Autowired
    private ClockService clockService;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    public List<Order> getAllOrders(Store store) {
        return orderRepository.findOrdersByStore(store, Sort.by(Sort.Direction.ASC, "orderID"));
    }
    public Order getOrder(String orderID, Store store) {
        return orderRepository.findOrderByOrderIDAndStore(orderID, store);
    }
    public boolean isOrderExists(String orderID, Store store) {
        return orderRepository.findOrderByOrderIDAndStore(orderID, store) != null;
    }

    public void displayOrders(Store store) {
        List<Order> orders = getAllOrders(store);
        for (Order order : orders) {
            System.out.println(order.toString());
            displayItems(order);
        }
        System.out.println("OK:display_completed");
    }

    public void displayItems(Order order) {
        List<Line> lines = lineRepository.findLinesByOrder(order);
        for (Line line : lines) {
            System.out.println(line.toString());
        }
    }

    public boolean isItemOrdered(String orderID, String itemName, Store store) {
        Order order = orderRepository.findOrderByOrderIDAndStore(orderID, store);
        return lineRepository.findLineByOrderAndItemName(order, itemName) != null;
    }

    public void addItemToOrder(Order order, Item item, int quantity, int price) {
        Line line = new Line(order, item, quantity, price);
        lineRepository.save(line);
        order.setOrderCost(order.getOrderCost() + line.getLineCost());
        order.setOrderWeight(order.getOrderWeight() + line.getLineWeight());
        orderRepository.save(order);
        Customer customer = order.getCustomer();
        customer.setPendingCost(customer.getPendingCost() + line.getLineCost());
        customerRepository.save(customer);
    }

    public boolean purchaseOrder(String orderID, Store store) {
        Order order = getOrder(orderID, store);
        return this.deliverOrder(order);
    }

    public boolean deliverOrder(Order order) {
        int deliveryTime = 0;
        Drone drone = order.getDrone();
        if (drone.getPilot() == null) {
            System.out.println("ERROR:drone_needs_pilot");
            return false;
        }
        int fuelRequired = order.getOrderDistance() * drone.getFuelConsumptionRate();
        if (drone.getRemainingFuel() < fuelRequired) {
            int rechargeTime = (int) ((drone.getFuelCapacity() - drone.getRemainingFuel()) / drone.getRefuelRate());
            // drone refuel rate is doubled during daytime, hence the recharge time is halved
            if (clockService.isDayTime()) {
                // divide recharge time by 2 if it is day time
                rechargeTime /= 2;
            }
            deliveryTime += rechargeTime; // add recharge time to delivery time
            drone.setRemainingFuel(drone.getFuelCapacity()); // restore fuel capacity
        }
        boolean isDelivered = droneService.decrementFuel(drone, fuelRequired);
        DronePilot pilot = drone.getPilot();
        if (isDelivered) {
            dronePilotService.incrementPilotExperience(pilot);
            droneService.increaseDroneRemainingWeight(drone, order);
            this.removeOrder(order);
            deliveryTime += order.getOrderDistance() / drone.getSpeed(); // add drone travel time to delivery time
            clockService.incrementTime(deliveryTime);
            int latePenalty = 0;
            if(deliveryTime > order.getExpectedDeliveryTime()) {
                latePenalty = 1;
                System.out.println("Expected_Delivery Time: " + order.getExpectedDeliveryTime() + " minutes");
                System.out.println("Actual_Delivery Time: " + deliveryTime + " minutes");
                System.out.println("OK:late_delivery_penalty_applied");
            } else {
                System.out.println("Delivery Time: " + deliveryTime + " minutes");
                System.out.println("OK:change_completed");
            }
            customerService.substractCredit(order.getOrderCost(), order.getCustomer());
            storeService.updateStoreKeyMetrics(order.getOrderCost(), order.getStore(), drone, latePenalty);
        }
        return isDelivered;
    }

    public void removeOrder(Order order) {
        orderRepository.deleteById(order.getId());
    }

    public boolean cancelOrder(Order order) {
        Customer customer = order.getCustomer();
        customer.setPendingCost(customer.getPendingCost() - order.getOrderCost());
        customerRepository.save(customer);
        Drone drone = order.getDrone();
        droneService.increaseDroneRemainingWeight(drone, order);
        this.removeOrder(order);
        return true;
    }

    public boolean transferOrder(Order order, Drone newDrone) {
        if (order.getOrderWeight() > newDrone.getRemainingWeight()) {
            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
            return false;
        }
        Drone oldDrone = order.getDrone();
        if (oldDrone.getId() == newDrone.getId()) {
            System.out.println("OK:new_drone_is_current_drone_no_change");
            return false;
        }
        droneService.increaseDroneRemainingWeight(oldDrone, order);
        droneService.decreaseDroneRemainingWeight(newDrone, order);
        order.setDrone(newDrone);
        orderRepository.save(order);
        storeService.incrementTransfers(order.getStore());
        return true;
    }
}
