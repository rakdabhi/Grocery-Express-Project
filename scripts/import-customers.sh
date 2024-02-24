#!/bin/bash

# This script imports data from a CSV file into a PostgreSQL database running in a Docker container.

CONTAINER_NAME="grocery-express-db-1"
CUSTOMERS_TABLE_NAME="customers"
LOCATION_TABLE_NAME="locations"
TEMP_TABLE_NAME="temp_table"
FILE_PATH="$1"
CSV_FILE=$(basename "$FILE_PATH")

# Copy the CSV file to the Docker container
docker cp "$FILE_PATH" "$CONTAINER_NAME:/home/$CSV_FILE"

# Execute the psql command inside the Docker container to create the temp table and import data
  # Step 1: Create a temporary table to store the imported data
  # Step 2: Copy the data from the CSV file into the temporary table
  # Step 3: Insert the location data from the temporary table into the location table
  # Step 4: Insert the customer data from the temporary table into the customers table
  # Step 5: Drop the temporary table
docker exec -i -u postgres "$CONTAINER_NAME" psql -d postgres << EOF

         CREATE TEMP TABLE $TEMP_TABLE_NAME (account_id varchar(255), credit int, first_name varchar(255), last_name varchar(255), location_id int, pending_cost int, phone_number varchar(255), rating int, x int, y int);

         \COPY $TEMP_TABLE_NAME (account_id, credit, first_name, last_name, location_id, pending_cost, phone_number, rating, x, y) FROM '/home/$CSV_FILE' DELIMITER ',' CSV HEADER;

         INSERT INTO $LOCATION_TABLE_NAME (x, y) SELECT x, y FROM $TEMP_TABLE_NAME;

         INSERT INTO $CUSTOMERS_TABLE_NAME (account_id, credit, first_name, last_name, location_id, pending_cost, phone_number, rating)
         SELECT account_id, credit, first_name, last_name, l.id as location_id, pending_cost, phone_number, rating
         FROM $TEMP_TABLE_NAME t
         JOIN $LOCATION_TABLE_NAME l ON t.x = l.x AND t.y = l.y;

         DROP TABLE $TEMP_TABLE_NAME;
EOF

# Clear redis cache
docker exec grocery-express-redis-1 redis-cli flushall