#!/bin/bash

# This script imports data from a CSV file into a PostgreSQL database running in a Docker container.

CONTAINER_NAME="grocery-express-db-1"
TABLE_NAME="$1"
FILE_PATH="$2"
CSV_FILE=$(basename "$FILE_PATH")

# Copy the CSV file to the Docker container
docker cp "$FILE_PATH" "$CONTAINER_NAME:/home/$CSV_FILE"

# Execute the psql command inside the Docker container to import the data
docker exec -it -u postgres "$CONTAINER_NAME" \
psql -d postgres -c "COPY $TABLE_NAME FROM '/home/$CSV_FILE' DELIMITER ',' CSV HEADER;"

# Clear redis cache
docker exec grocery-express-redis-1 redis-cli flushall