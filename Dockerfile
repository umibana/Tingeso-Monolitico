# First we build the app using Gradle

FROM openjdk:17-alpine as build
WORKDIR /app
COPY . .
RUN ./gradlew build

# Then we run the app

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar monolithicWebapp.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "monolithicWebapp.jar"]
