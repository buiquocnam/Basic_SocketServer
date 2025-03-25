# Sử dụng image Java 21 mới nhất
FROM eclipse-temurin:21-jdk

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép toàn bộ project vào container
COPY . .

# Build project với Maven
RUN ./mvnw clean package

# Chạy ứng dụng khi container khởi động
CMD ["java", "-jar", "target/socket-server-1.0-SNAPSHOT.jar"]
