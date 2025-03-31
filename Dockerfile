FROM maven:3.8.4-openjdk-17-slim AS build

# Sao chép mã nguồn vào container
WORKDIR /app
COPY . .

# Build ứng dụng với Maven
RUN mvn clean package

# Tạo image chạy cuối cùng
FROM openjdk:17-slim
WORKDIR /app

# Sao chép file JAR từ stage build
COPY --from=build /app/target/socket-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar

# Expose cổng TCP
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-cp", "app.jar", "com.example.socket_server.TCPServer"]
