#  CS6310 A5 Grocery Express Project

## Demo Link
https://youtu.be/bhnNWD6GuAY

## Overview
As discussed in our proposal, we have made the following functional and non-functional modifications to our grocery express application:
1) **Archivability**: Use of relational database to ensure data persistence along with import and export functionalities to help manage archived data.
2) **Performance**: Implement a cache layer in our system along with database indexing and query optimization to increase performance.
3) **Solar Powered Drone**: Introduce a Clock Class and Location Class to keep track of time and distance. Added speed, refuelRate, and fuelConsumptionRate attributes to the Drone Class.

## Technical Specification
* PostgreSQL 9.6.12
* Redis 7.2.3
* Spring Boot 3.1.5
* Spring Data JPA
* Java 17
* Maven 3.9.5
* Docker

## Key Improvements to the System
### Data Persistance
We have added a PostgresSQL database for data storage instead of using in-memory object to ensure data persistance. We have utilized the Spring Data JPA package to implement the data access layer of our application where it will help handle queries to the database and all standard CRUD operations.

### Caching
We have utilized the redis in-memory data store to cache frequently accessed data in-memory which would help minimize the number of database calls and improve system response time.

### Performance Optimization
For performance optimization, we have implemented the followings in our code with the help of Spring Data JPA:<br/>

**Database indexing:**<br/>
We have added indexes on orders, drones, stores and items entity. This can improve the performance of queries that involve searching, sorting, and joining by the indexed columns.

**Query optimization:**<br/>
We have reviewed all the database queries to ensure we are only fetching the necessary data that is needed for the corresponding operation

**Caching:**<br/>
We have enabled caching to store frequently accessed data (Customers and DronePilots) in memory which would help reducing database hits and resulting in shorter system response time.

**Using lazy loading fetch strategy when possible**<br/>
We have used the lazy load fetch strategy where appropriate to avoid loading excessive amount of data. For example, in our store entity, we have used the lazy load fetch strategy to ensure the store items are loaded lazily. This means that the items collection will only be loaded from the database when it is accessed, improving performance by reducing the amount of data fetched from the database.


**Avoid N+1 query problem**<br/>
In our system, the drone class has a one-to-many relationship with order. When we run the **display_drones** command, we would need to retrieve information with their associated orders.
If lazy loading is used, the Spring Data JPA framework will first fetch all drones with a single query. However, when you iterate over these drones and access their orders, a separate query is executed for each drone to fetch its associated orders. This means that if you have N drones, you will end up executing N+1 queries: one query to fetch the drones and an additional query for each drone's orders.
This N+1 query problem can have a negative impact on performance, especially when dealing with a large number of drones and orders. It results in increased database round-trips and unnecessary overhead.
To address the N+1 problem, we have used the eager loading strategy to load the associated orders upfront along with the drones, eliminating the need for additional queries.


## How to get started

### To build and run all containers:

```
docker-compose -p grocery-express up -d --build
```


### To test a specific scenario against the initial jar 
(Make sure to build and run all the containers first)
#### Mac / Linux
```
./scripts/test.sh <scenario>
```
#### Windows
```
.\scripts\test.sh <scenario>
```

### To batch run the test scenarios
#### Mac / Linux
```
./scripts/batch.sh
```
#### Windows
```
.\scripts\batch.sh
```

### To run in interactive mode
#### Step 1 from the host
```
docker-compose -p grocery-express up -d
```
#### Step 2 from the host
```
docker exec -it grocery-express-app-1 sh -c "java -jar grocery-express-project-0.0.1-SNAPSHOT.jar"
```
#### Step 3 from the jar
* From there you can run any of the commands listed below:
```
make_store,<Store>,<InitialRevenue>,<x-coordinate>,<y-coordinate>
display_stores
sell_item,<Store>,<Item>,<Weight>
display_items,<Store>
make_pilot,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<TaxId>,<LicenseId>,<ExperienceLevel>
display_pilots
make_drone,<Store>,<DroneId>,<WeightCapacity>,<FuelCapacity>,<RefuelRate>,<FuelRate>,<Speed>
display_drones,<Store>
fly_drone,<Store>,<DroneId>,<PilotAccount>
make_customer,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<CustomerRating>,<Credits>,<x-coordinate>,<y-coordinate>
display_customers
start_order,<Store>,<OrderId>,<DroneId>,<CustomerAccount>,<ExpectedDeliveryTime>
display_orders,<Store>
request_item,<Store>,<OrderId>,<Item>,<Quantity>,<UnitPrice>
purchase_order,<Store>,<OrderId>
cancel_order,<Store>,<OrderId>
transfer_order,<Store>,<OrderId>,<DroneId>
display_efficiency
display_distance,<Store>,<CustomerAccountID>
display_time
set_time,<NewTime>
import_customers,<FileName>
export_customers
purge_customers
display_archive
clean_archive
test_cache,<NumberOfCustomers>
stop
```

### To run & test in interactive mode

```
java -jar grocery-express-project-0.0.1-SNAPSHOT.jar < commands_00.txt > drone_delivery_00_results.txt
diff -s drone_delivery_00_results.txt drone_delivery_initial_00_results.txt > results_00.txt
cat results_00.txt
```

### To run a specific scenario with your jar and output to localhost
The "mkdir docker_results ; " would not be needed after the 1st run, but just in case you have not made the directory yet with another command.
```
mkdir docker_results ; docker-compose -p grocery-express up -d;  docker exec -it grocery-express-app-1 sh -c 'java -jar grocery-express-project-0.0.1-SNAPSHOT.jar < commands_00.txt'  > docker_results/drone_delivery_00_results.txt
```

### If you get stuck in an infinite loop
Simply stop and remove the running container
```
docker ps
docker rm -f <container_id>
```

### To stop and remove all containers:

```
docker-compose -p grocery-express down
```

### To test with a clean image & container
After running the below command you will need to run the build command again
#### Windows
```
docker ps -aq | % { docker stop $_ } | % { docker rm -f $_ } | docker images -f "dangling=true" -aq | % { docker rmi -f $_ } | docker images grocery-express-app -aq | % { docker rmi -f $_ }
```
#### Mac
```
docker ps -aq | xargs docker stop | xargs docker rm -f && docker images -f "dangling=true" -aq | xargs docker rmi -f && docker images "grocery-express-app" -aq | xargs docker rmi -f
```

# Note please run all the scripts below from the project root directory
### To export customer data from the database 
#### Mac / Linux
```
./scripts/export-customers.sh
```
#### Windows
```
.\scripts\export-customers.sh
```

### To import customer data from the database 
(please make sure the foreign key constraints are not violated when importing data into the tables)
#### Mac / Linux
```
./scripts/import-customers.sh <file_path>
```
#### Windows
```
.\scripts\import-customers.sh <file_path>
```

### To purge customer data from the database 
#### Mac / Linux
```
./scripts/purge-customers.sh
```
#### Windows
```
.\scripts\purge-customers.sh
```

### To retrieve archived customer data from customer_archive folder inside the docker container 
#### Mac / Linux
```
./scripts/get-archived-customers.sh
```
#### Windows
```
.\scripts\get-archived-customers.sh
```

## Test Cases for Solar Powered Drone
To make sure our system can satisfy all the requirements for the solar powered drone, we have prepared the following test scenarios:
1) **[Test Scenario 66]** -	Track Distance between stores and customers 
2) **[Test Scenario 67]** -	Implement a simulated clock to track system time 
3) **[Test Scenario 68]** -	Fuel is consumed when delivering orders 
4) **[Test Scenario 69]** -	If Drone does not have enough fuel, it must stay in the same location and wait for certain amount of time until it is fully recharged with solar power (recharge time [min] = (drone max fuel capacity - remaining fuel) [C] / refuel rate [C/min]). 
5) **[Test Scenario 70]** - Implement day/night like clock cycle such that refuel rate is faster during the day 
6) **[Test Scenario 71]** -	Track the order delivery time and implement penalties for order that was not delivered in a timely manner. 

For more details on each of the test scenarios, please refer to the **Solar_Powered_Drone_Test_Cases.pdf** file.

## Test Cases for Archivability
To make sure our system can satisfy the archivability requirement, we have prepared the following test scenarios:
1) **[Test Scenario 72]** -	Export customers data and verify if customers data has been archived
2) **[Test Scenario 73]** -	Purge customers data and check if customers data has been removed from the system
3) **[Test Scenario 74]** -	Import customers data and check if customers data has been imported successfully
4) **[Test Scenario 75]** -	Auto purge and archive customers data after 7 days

## Remarks / Important Notes
To ensure all tests can run independently (without affecting each other) and pass all the test scenarios, we have implemented our application with the following configurations to make it convenient for development and testing purposes:
1) In the **application.properties** file, we have the **spring.jpa.hibernate.ddl-auto** property set to **create** to ensure the database schema will be dropped and recreated during every application startup. In production environment, we would normally set this property to **none** which means no changes will be performed on the database during application startup or shutdown.
2) In the **application.properties** file, we have set the **logging level** property to **error** to ensure only the error messages will be printed into the console.
3) In the command loop (i.e. in DeliveryService Class), we have implemented logic to clear the redis cache during program startup and shutdown to ensure all the data stored in cache during each test run are cleared. This is to avoid the possibility of tests failed due to the data cached during different test run. In production environment, we would normally let the system to clear the cache based on the specified Time-To-Live (TTL) value and the Cache Eviction Policies.

All instructions provided in this document assume the application will be tested and run with docker containers. If you would like to run it locally (without docker), you would need to install all the dependencies as listed in the technical specification section and you may need to adjust some of the properties defined in the **application.properties** file according to your local environment setup.
