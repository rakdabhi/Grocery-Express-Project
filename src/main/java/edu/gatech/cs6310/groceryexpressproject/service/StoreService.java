package edu.gatech.cs6310.groceryexpressproject.service;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cs6310.groceryexpressproject.model.Drone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import edu.gatech.cs6310.groceryexpressproject.model.Item;
import edu.gatech.cs6310.groceryexpressproject.model.Store;
import edu.gatech.cs6310.groceryexpressproject.repository.ItemRepository;
import edu.gatech.cs6310.groceryexpressproject.repository.StoreRepository;

@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ItemRepository itemRepository;

    public List<Store> getAllStores() {
        List<Store> stores = storeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        if (!stores.isEmpty()) {
            return stores;
        } else {
            return new ArrayList<Store>();
        }
    }

    public Store getStoreByName(String name) {
        return storeRepository.findByName(name);
    }

    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    public boolean isStoreExists(String name) {
        return storeRepository.findByName(name) != null;
    }

    public List<Item> getItemsByStore(Store store) {
        return itemRepository.findItemsByStore(store, Sort.by(Sort.Direction.ASC, "name"));
    }

    public Item getItem(String itemName, Store store) {
        return itemRepository.findItemByNameAndStore(itemName, store);
    }

    public boolean createStoreItem(Item item, Store store) {
        if (getItem(item.getName(), store) != null) {
            System.out.println("ERROR:item_identifier_already_exists");
            return false;
        }
        itemRepository.save(item);
        System.out.println("OK:change_completed");
        return true;
    }

    public void displayItems(Store store) {
        List<Item> items = itemRepository.findItemsByStore(store, Sort.by(Sort.Direction.ASC, "name"));
        for (Item item : items) {
            System.out.println(item.getName() + "," + item.getWeight());
        }
        System.out.println("OK:display_completed");
    }

    public boolean isItemExists(String itemName, Store store) {
        return itemRepository.findItemByNameAndStore(itemName, store) != null;
    }

    public void updateStoreKeyMetrics(int orderCost, Store store, Drone drone, int latePenalty) {
        store.setRevenue(store.getRevenue() + orderCost);
        store.setPurchases(store.getPurchases() + 1);
        int droneOverloads = drone.getOrders().size() - 1;
        store.setOverloads(store.getOverloads() + droneOverloads);
        store.setPenalties(store.getPenalties() + latePenalty);
        storeRepository.save(store);
    }

    public void incrementTransfers(Store store) {
        store.setTransfers(store.getTransfers() + 1);
        storeRepository.save(store);
    }

    public void displayEfficiency() {
        List<Store> stores = getAllStores();
        for (Store store : stores) {
            System.out.println("name:" + store.getName() +
                    ",purchases:" + store.getPurchases() +
                    ",overloads:" + store.getOverloads() +
                    ",transfers:" + store.getTransfers() +
                    ",penalties:" + store.getPenalties()
            );
        }
        System.out.println("OK:display_completed");
    }
}
