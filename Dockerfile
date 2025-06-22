# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add wait-for-it script to wait for MySQL
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Environment variables for database configuration
ENV DB_HOST=mysql \
    DB_PORT=3307 \
    DB_NAME=golanguage_db \
    DB_USER=root \
    DB_PASSWORD=12345678

# Expose the application port
EXPOSE ${PORT:-8080}

# Entry point with wait-for-it
ENTRYPOINT ["/bin/sh", "-c", "/wait-for-it.sh $DB_HOST:$DB_PORT -t 60 -- java -jar app.jar"]

# Run the application
CMD ["java", "-jar", "app.jar"] 