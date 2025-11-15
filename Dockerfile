FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY avito-test-domain/pom.xml ./avito-test-domain/
COPY avito-test-db/pom.xml ./avito-test-db/
COPY avito-test-impl/pom.xml ./avito-test-impl/

COPY avito-test-domain/src ./avito-test-domain/src
COPY avito-test-db/src ./avito-test-db/src
COPY avito-test-impl/src ./avito-test-impl/src

WORKDIR /app/avito-test-domain
RUN mvn clean install -DskipTests

WORKDIR /app/avito-test-db
RUN mvn clean install -DskipTests

WORKDIR /app/avito-test-impl
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/avito-test-impl/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

