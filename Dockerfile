# Start with a base image containing Java runtime
FROM openjdk:17-jdk

# Add Maintainer Info
LABEL maintainer="work@rommler.de"

# Make port 8080 available to the world outside this container
EXPOSE 7080

# The application's jar file
ARG JAR_FILE=build/libs/federation_connector-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

COPY src/main/resources/${ASSET_FOLDER_PATH} ${ASSET_FOLDER_PATH}
COPY src/main/resources/Contracts /Contracts
COPY src/main/resources/Contract_Config.json /Contract_Config.json

# Set environment variables
ENV SERVER_PORT=7080
ENV CONTROLLER_PORT=12080
ENV SOCKET_PORT=10080
ENV CONTROLLER_IP=127.0.0.1
ENV ASSET_FOLDER_PATH=/Assets1
ENV CONTRACT_FOLDER_PATH=/Contracts
ENV CONTRACT_FILE_PATH=/Contract_Config.json

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]