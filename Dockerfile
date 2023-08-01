# Fetching latest version of Java
FROM openjdk:11
 
# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/red-surf-4563-1.0.jar /app

# Exposing port 5000
EXPOSE 5000

# Starting the application
CMD ["java", "-jar", "red-surf-4563-1.0.jar"]