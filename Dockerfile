# --- Etapa de build ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia primeiro só o necessário para resolver dependências (cache de camada)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Agora copia o código-fonte e builda
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# --- Etapa de execução ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring

EXPOSE 8080

# --enable-preview é exigido pelo compilador configurado no pom.xml
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]