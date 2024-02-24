# Create the local directory if it doesn't exist
mkdir -p ./customers_archive

# Copy customers archive from the Docker container to the local directory
docker cp grocery-express-app-1:/cs6310/customers_archive/. ./customers_archive