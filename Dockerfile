# First we build the app using Gradle
# Disabled since we do this in the pipeline
# FROM openjdk:17-alpine as build
# WORKDIR /app
# COPY . .
# RUN ./gradlew build

# Then we run the app

FROM openjdk:17-alpine
WORKDIR /app
COPY ./build/libs/*.jar monolithicWebapp.jar
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} monolithicWebapp.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "monolithicWebapp.jar"]
