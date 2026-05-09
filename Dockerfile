FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "if [ -n \"$DATABASE_URL\" ] && [ -z \"$DB_URL\" ]; then clean_url=${DATABASE_URL#postgresql://}; host_path=${clean_url#*@}; host_path=${host_path%%\\?*}; export DB_URL=\"jdbc:postgresql://$host_path\"; fi; java -jar app.jar"]
