#----------------------
# --- Maven build stage
FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#----------------------
# --- Runtime image
FROM eclipse-temurin:17-jre
VOLUME /tmp
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# JVM memory limit for small container
env JAVA_OPTS="-XX:+UseContainerSupport -Xmx256m -Xms64m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
