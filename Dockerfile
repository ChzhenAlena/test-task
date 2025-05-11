FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

RUN ./mvnw clean verify -DskipIntegrationTests=false

COPY src ./src

#CMD ["java", "-jar", "app.jar"]
CMD ["./mvnw", "spring-boot:run"]
