FROM openjdk:17
WORKDIR /app
COPY . .
RUN ./gradlew clean build
EXPOSE 5000

CMD ["java", "-jar", "/app/build/libs/app-1.0-SNAPSHOT.jar"]