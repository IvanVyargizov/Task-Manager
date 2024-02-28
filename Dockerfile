FROM openjdk:17
WORKDIR /app
COPY . .
RUN ./gradlew clean build
EXPOSE 8080

CMD ["java", "-jar", "/app/build/libs/app-1.0-SNAPSHOT.jar"]