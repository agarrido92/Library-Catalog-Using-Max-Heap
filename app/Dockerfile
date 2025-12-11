FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /usr/src/app

# Copy pom and preload dependencies (faster rebuilds)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# Copy source code
COPY . .

# Run the app using Maven exec plugin
CMD ["mvn", "-q", "-B", "clean", "compile", "exec:java", "-Dexec.mainClass=com.example.library.Main"]
