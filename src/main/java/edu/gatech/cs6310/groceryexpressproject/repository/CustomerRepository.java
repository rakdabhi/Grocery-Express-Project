package edu.gatech.cs6310.groceryexpressproject.repository;

import edu.gatech.cs6310.groceryexpressproject.model.Customer;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, String> {
    @Override
    List<Customer> findAll();
    List<Customer> findAll(Sort sort);
    Customer findByAccountID(String AccountID);
    void deleteAll();
}
