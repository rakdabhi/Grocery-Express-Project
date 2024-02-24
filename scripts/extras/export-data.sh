# Export data from PostgreSQL table to CSV file
CONTAINER_NAME="grocery-express-db-1"
TABLE_NAME="$1"  # Take the table name as the first command-line argument
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")  # Get the current timestamp

# Build the export file name with table name and timestamp
EXPORT_FILE="${TABLE_NAME}_${TIMESTAMP}.csv"

# Execute the following command in the Docker container with username: postgres
docker exec -it -u postgres $CONTAINER_NAME \
psql -d postgres -c "COPY (SELECT * FROM $TABLE_NAME) TO STDOUT WITH (FORMAT CSV, HEADER)" > $EXPORT_FILE