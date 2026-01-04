FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre AS extract
WORKDIR /layers
COPY --from=build /workspace/target/*.jar app.jar
# Use -Djarmode=layertools for extraction
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -u 10001 -m appuser
USER appuser

# Copy the extracted layers
COPY --from=extract /layers/dependencies/ ./
COPY --from=extract /layers/spring-boot-loader/ ./
COPY --from=extract /layers/snapshot-dependencies/ ./
COPY --from=extract /layers/application/ ./

EXPOSE 8080
# Use the updated 'launch' package path
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]