FROM eclipse-temurin:8-jdk-alpine
COPY "./target/SpringBootMVCPortafolio-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]