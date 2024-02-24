package edu.gatech.cs6310.groceryexpressproject.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import edu.gatech.cs6310.groceryexpressproject.model.Clock;
import edu.gatech.cs6310.groceryexpressproject.model.Customer;
import edu.gatech.cs6310.groceryexpressproject.model.Location;
import edu.gatech.cs6310.groceryexpressproject.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RedisCacheManager cacheManager;
    @Autowired
    private LocationService locationService;

    @Cacheable(value="Customer", key="#accountID", condition = "#accountID != null")
    public Customer findByAccountID(String accountID) {
        return customerRepository.findByAccountID(accountID);
    }
    @CacheEvict(value = {"Customers"}, allEntries = true)
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    @Cacheable("Customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll(Sort.by(Sort.Direction.ASC, "accountID"));
    }

    public boolean isCustomerExists(String accountID) {
        return customerRepository.findByAccountID(accountID) != null;
    }

    public boolean canAffordItem(Customer customer, int unitPrice, int quantity) {
        int itemCost = unitPrice * quantity;
        int customerCredit = customer.getCredit();
        int pendingCost = customer.getPendingCost();
        return customerCredit >= itemCost + pendingCost;
    }

    @CacheEvict(value = "Customer", key = "#customer.accountID", condition = "#customer.accountID != null")
    public void substractCredit(int orderCost, Customer customer) {
        Objects.requireNonNull(cacheManager.getCache("Customers")).clear();
        customer.setCredit(customer.getCredit() - orderCost);
        customerRepository.save(customer);
    }

    /**
     * Export customers to CSV file.
     *
     * @throws IOException if there is an error while writing to the file
     */
    public void exportCustomersToCSV(int time) throws IOException {
        List<Customer> customers = customerRepository.findAll(Sort.by(Sort.Direction.ASC, "accountID"));
        String directoryPath = "./customers_archive/";
        File directory = new File(directoryPath);
        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String timeStamp = Clock.getTimestamp(time);
        String filePath = directoryPath + "customers_" + timeStamp + ".csv";
        Writer writer = new FileWriter(filePath);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        try {
            beanToCsv.write(customers);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        }
        writer.close();
    }
    /**
     * Import customers from a CSV file.
     *
     * @param  fileName  the name of the CSV file to import
     */
    @CacheEvict(value = {"Customers", "Customer"}, allEntries = true)
    public void importCustomersFromCSV(String fileName) {
        try { 
            FileReader FileReader = new FileReader("./customers_archive/" + fileName);
            CSVReader csvReader = new CSVReaderBuilder(FileReader) 
                                    .withSkipLines(1) 
                                    .build(); 
            List<String[]> allData = csvReader.readAll(); 
            for (String[] row : allData) { 
                Location location = new Location(Integer.parseInt(row[8]), Integer.parseInt(row[9]));
                location = locationService.createLocation(location);
                Customer customer = new Customer(row[0], row[2], row[3], row[6], Integer.parseInt(row[7]), Integer.parseInt(row[1]), location);
                createCustomer(customer);
            }
        } 
        catch (Exception e) { 
            e.printStackTrace();
            System.out.println("ERROR:import_failed");
        } 
    }
    /**
     * Displays the names of files and directories in the "customers_archive" directory.
     */
    public void displayArchivedCustomerFiles() {
        // Creates an array in which we will store the names of files and directories
        String[] pathNames;

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File("./customers_archive/");

        // Populates the array with names of files and directories
        pathNames = f.list();

        if (pathNames != null) {
            // Sort the pathNames array in alphabetical order
            Arrays.sort(pathNames);
            // For each pathname in the pathNames array
            for (String pathname : pathNames) {
                // Print the names of files and directories
                System.out.println(pathname);
            }
        }
    }

    /**
     * Deletes all files in the customers_archive directory.
     *
     */
    public void cleanArchive() {
        File file = new File("./customers_archive/");
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @CacheEvict(value = {"Customers", "Customer"}, allEntries = true)
    public void purgeCustomers() {
        customerRepository.deleteAll();
        System.out.println("OK:Customers_Data_Has_Been_Purged");
    }
}
