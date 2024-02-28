FROM openjdk:17
WORKDIR /
COPY . .
RUN ./gradlew build
EXPOSE 5000

CMD ["java", "-jar", "/build/libs/app-1.0-SNAPSHOT.jar"]