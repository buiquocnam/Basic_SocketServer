package com.example.socket_server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TCPServer {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        // Lấy port từ biến môi trường hoặc sử dụng port mặc định
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Máy chủ TCP đang chạy tại cổng " + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client mới đã kết nối: " + clientSocket.getInetAddress());
                    
                    executorService.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Lỗi khi chấp nhận kết nối: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi động máy chủ: " + e.getMessage());
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Đọc dòng đầu tiên để kiểm tra loại request
            String firstLine = in.readLine();
            if (firstLine == null) {
                return;
            }

            // Kiểm tra xem có phải là HTTP request không
            if (firstLine.contains("HTTP/1.1") || firstLine.contains("HTTP/1.0")) {
                handleHttpRequest(firstLine, in, out);
            } else {
                handleTcpClient(firstLine, in, out);
            }

        } catch (IOException e) {
            System.err.println("Lỗi xử lý client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client đã ngắt kết nối: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }

    private static void handleHttpRequest(String firstLine, BufferedReader in, PrintWriter out) {
        // Đọc hết các header HTTP
        while (true) {
            try {
                String line = in.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
            } catch (IOException e) {
                break;
            }
        }

        // Trả về HTTP response đơn giản
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println("Connection: close");
        out.println();
        out.println("TCP Socket Server is running!");
        out.flush();
    }

    private static void handleTcpClient(String firstLine, BufferedReader in, PrintWriter out) {
        // Gửi thông báo chào mừng
        String welcomeMessage = "Chào mừng! Hãy nhập số 1 để nhận lời chào, hoặc 'exit' để thoát.";
        out.println(welcomeMessage);

        // Xử lý tin nhắn đầu tiên nếu có
        processMessage(firstLine, out);

        // Xử lý các tin nhắn tiếp theo
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Nhận từ client: " + inputLine);
                
                if ("exit".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Tạm biệt!");
                    break;
                }

                processMessage(inputLine, out);
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc từ client: " + e.getMessage());
        }
    }

    private static void processMessage(String message, PrintWriter out) {
        try {
            int choice = Integer.parseInt(message.trim());
            if (choice == 1) {
                out.println("Xin chào từ Server!");
            } else {
                out.println("Lựa chọn không hợp lệ! Vui lòng chọn số 1.");
            }
        } catch (NumberFormatException e) {
            if (!message.contains("HTTP")) { // Không gửi thông báo lỗi cho HTTP requests
                out.println("Vui lòng nhập một số nguyên hợp lệ!");
            }
        }
    }

    // Phương thức để dừng server
    public static void stop() {
        isRunning = false;
        executorService.shutdown();
    }
} 