# Sử dụng OpenJDK 21
FROM openjdk:21-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép mã nguồn vào container
COPY . .

# Biên dịch ứng dụng
RUN javac Server/*.java Client/*.java

# Chạy server
CMD ["java", "Server.Server"]
