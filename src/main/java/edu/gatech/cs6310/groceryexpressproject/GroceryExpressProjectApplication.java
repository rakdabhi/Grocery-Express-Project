package edu.gatech.cs6310.groceryexpressproject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import edu.gatech.cs6310.groceryexpressproject.service.DeliveryService;

@SpringBootApplication
@EnableCaching
public class GroceryExpressProjectApplication {

	public static void main(String[] args) {
		System.out.println("Welcome to the Grocery Express Delivery Service!");
		ConfigurableApplicationContext context = SpringApplication.run(GroceryExpressProjectApplication.class, args);
		DeliveryService deliveryService = context.getBean(DeliveryService.class);
		deliveryService.commandLoop();	
	}
}
