# Sử dụng Java 21 mới nhất (phiên bản slim để giảm dung lượng image)
FROM eclipse-temurin:21-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file cấu hình Maven trước để tận dụng cache Docker
COPY pom.xml .  
COPY src ./src  

# Đảm bảo quyền thực thi cho mvnw
RUN chmod +x ./mvnw

# Build project với Maven
RUN ./mvnw clean package

# Chạy ứng dụng khi container khởi động
CMD ["java", "-jar", "target/socket-server-1.0-SNAPSHOT.jar"]
