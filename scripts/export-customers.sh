# Export data from PostgreSQL table to CSV file
CONTAINER_NAME="grocery-express-db-1"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")  # Get the current timestamp

# Build the export file name with table name and timestamp
EXPORT_FILE="customers_${TIMESTAMP}.csv"

# Execute the following command in the Docker container with username: postgres
docker exec -it -u postgres $CONTAINER_NAME \
psql -d postgres -c "COPY (SELECT account_id, credit, first_name, last_name, location_id, pending_cost, phone_number, rating, x, y FROM customers c join locations l on c.location_id = l.id) TO STDOUT WITH (FORMAT CSV, HEADER)" > $EXPORT_FILE