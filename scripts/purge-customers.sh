#!/bin/bash

# Clear the customers data inside the Docker container

CONTAINER_NAME="grocery-express-db-1"

# Connect to the database container and execute SQL statements
docker exec -i -u postgres "$CONTAINER_NAME" psql -d postgres << EOF
-- Disable foreign key checks
SET session_replication_role = 'replica';

-- Delete the data from the locations table
DELETE FROM locations;

-- Delete the data from the customers table
DELETE FROM customers;

-- Enable foreign key checks
SET session_replication_role = 'origin';
EOF

# Clear redis cache
docker exec grocery-express-redis-1 redis-cli flushall