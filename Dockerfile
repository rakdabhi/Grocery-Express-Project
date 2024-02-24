# use maven as a builder image to separate the compile step from our deployment image
FROM maven:3.9.5 AS builder
WORKDIR cs6310
COPY ./ ./
RUN mvn clean install

FROM openjdk:17-slim AS backend
WORKDIR cs6310
# copy the test_scenarios and the test_results files to the final image
COPY test_scenarios ./
COPY test_results ./
# copy the jar from the builder image to the final image
COPY --from=builder /cs6310/target/grocery-express-project-0.0.1-SNAPSHOT.jar /cs6310/grocery-express-project-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "/cs6310/grocery-express-project-0.0.1-SNAPSHOT.jar"]