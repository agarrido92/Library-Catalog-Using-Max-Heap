FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY . .
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target /usr/src/app/target
COPY --from=build /usr/src/app/pom.xml /usr/src/app/pom.xml
CMD ["bash", "-c", "mvn -q -f /usr/src/app/pom.xml compile exec:java -Dexec.mainClass=com.example.library.Main"]
