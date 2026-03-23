FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :services:api:installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/api/build/install/api .
EXPOSE 8080
CMD ["./bin/api"]
