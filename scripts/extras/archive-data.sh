#!/bin/bash

# Export and Clear the table data inside the Docker container

CONTAINER_NAME="grocery-express-db-1"
TABLE_NAME="$1"  # Take the table name as the first command-line argument
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")  # Get the current timestamp

# Build the export file name with table name and timestamp
EXPORT_FILE="${TABLE_NAME}_${TIMESTAMP}.csv"

# Export data from PostgreSQL table to CSV file
docker exec -it -u postgres $CONTAINER_NAME \
psql -d postgres -c "COPY (SELECT * FROM $TABLE_NAME) TO STDOUT WITH (FORMAT CSV, HEADER)" > $EXPORT_FILE

# Clear the table data inside the Docker container
docker exec -i -u postgres "$CONTAINER_NAME" psql -d postgres << EOF
-- Disable foreign key checks
SET session_replication_role = 'replica';

-- Delete the data from the specified table
DELETE FROM "$TABLE_NAME";

-- Enable foreign key checks
SET session_replication_role = 'origin';
EOF

# Clear redis cache
docker exec grocery-express-redis-1 redis-cli flushall