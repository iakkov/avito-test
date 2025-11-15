FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY avito-test-db/pom.xml avito-test-db/
COPY avito-test-domain/pom.xml avito-test-domain/
COPY avito-test-impl/pom.xml avito-test-impl/

RUN mvn -B -ntp dependency:go-offline

COPY avito-test-db/src avito-test-db/src
COPY avito-test-domain/src avito-test-domain/src
COPY avito-test-impl/src avito-test-impl/src

RUN mvn -B -ntp clean install -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/avito-test-impl/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


