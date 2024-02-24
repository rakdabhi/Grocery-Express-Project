#!/bin/bash

# Clear the table data inside the Docker container

CONTAINER_NAME="grocery-express-db-1"

TABLE_NAME="$1"  # Take the table name as the first command-line argument

# Connect to the database container and execute SQL statements
docker exec -i -u postgres "$CONTAINER_NAME" psql -d postgres << EOF
-- Disable foreign key checks
SET session_replication_role = 'replica';

-- Delete the data from the specified table
DELETE FROM "$TABLE_NAME" CASCADE;

-- Enable foreign key checks
SET session_replication_role = 'origin';
EOF
# Clear redis cache
docker exec grocery-express-redis-1 redis-cli flushall