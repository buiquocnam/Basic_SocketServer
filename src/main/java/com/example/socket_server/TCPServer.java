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
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String welcomeMessage = "Chào mừng! Hãy nhập số 1 để nhận lời chào, hoặc 'exit' để thoát.";
            out.println(welcomeMessage);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Nhận từ client: " + inputLine);

                if ("exit".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Tạm biệt!");
                    break;
                }

                try {
                    int choice = Integer.parseInt(inputLine.trim());
                    if (choice == 1) {
                        out.println("Xin chào từ Server!");
                    } else {
                        out.println("Lựa chọn không hợp lệ! Vui lòng chọn số 1.");
                    }
                } catch (NumberFormatException e) {
                    out.println("Vui lòng nhập một số nguyên hợp lệ!");
                }
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

    // Phương thức để dừng server
    public static void stop() {
        isRunning = false;
        executorService.shutdown();
    }
} 