FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/xpenso-0.0.1-SNAPSHOT.jar xpenso.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "xpenso.jar"]
