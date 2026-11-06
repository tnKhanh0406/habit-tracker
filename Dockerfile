FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace

# Cache dependencies separately from application sources.
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /workspace/target/habit-tracker-0.0.1-SNAPSHOT.jar app.jar

USER spring:spring

EXPOSE 8081

ENV JAVA_TOOL_OPTIONS=""

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
