package com.example.socket_server;

import com.example.socket_server.exercises.NumberChecking;
import com.example.socket_server.exercises.DigitOperations;
import com.example.socket_server.exercises.GcdLcm;
import com.example.socket_server.exercises.StringManipulation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server đang chạy tại port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client kết nối từ: " + clientSocket.getInetAddress());
                
                // Tạo thread mới cho mỗi client
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            int currentFunction = 0;
            sendMenu(out);

            String line;
            while ((line = in.readLine()) != null) {
                try {
                    if (currentFunction == 0) {
                        // Người dùng đang chọn chức năng
                        currentFunction = Integer.parseInt(line.trim());
                        if (currentFunction >= 1 && currentFunction <= 6) {
                            out.println(getPromptForFunction(currentFunction));
                        } else {
                            currentFunction = 0;
                            out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại:\n");
                            sendMenu(out);
                        }
                    } else {
                        // Người dùng đang nhập dữ liệu cho chức năng đã chọn
                        String result = processFunction(currentFunction, line.trim());
                        out.println(result + "\n");
                        currentFunction = 0;
                        sendMenu(out);
                    }
                } catch (Exception e) {
                    out.println("Có lỗi xảy ra: " + e.getMessage() + "\nVui lòng thử lại:\n");
                    currentFunction = 0;
                    sendMenu(out);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi xử lý client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }

    private static void sendMenu(PrintWriter out) {
        StringBuilder menu = new StringBuilder();
        menu.append("MENU CHỨC NĂNG:\n\n");
        menu.append("1. Kiểm tra số (số nguyên tố, chính phương, hoàn hảo, Armstrong)\n");
        menu.append("2. Tính tổng và tích các chữ số\n");
        menu.append("3. Tìm UCLN và BCNN\n");
        menu.append("4. Đảo ngược chuỗi\n");
        menu.append("5. Xử lý chuỗi nâng cao\n");
        menu.append("6. Phân tích chuỗi\n\n");
        menu.append("Vui lòng chọn chức năng (1-6):\n");
        out.println(menu.toString());
    }

    private static String getPromptForFunction(int function) {
        switch (function) {
            case 1:
                return "Nhập một số nguyên để kiểm tra:\n";
            case 2:
                return "Nhập một số nguyên để tính tổng và tích các chữ số:\n";
            case 3:
                return "Nhập hai số nguyên cách nhau bởi dấu cách để tìm UCLN và BCNN:\n";
            case 4:
            case 5:
            case 6:
                return "Nhập chuỗi cần xử lý:\n";
            default:
                return "Lựa chọn không hợp lệ\n";
        }
    }

    private static String processFunction(int function, String data) {
        try {
            switch (function) {
                case 1: {
                    int number = Integer.parseInt(data);
                    return NumberChecking.checkNumber(number);
                }
                case 2: {
                    int number = Integer.parseInt(data);
                    return DigitOperations.calculateDigits(number);
                }
                case 3: {
                    String[] parts = data.split("\\s+");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Vui lòng nhập đúng hai số nguyên cách nhau bởi dấu cách");
                    }
                    int a = Integer.parseInt(parts[0]);
                    int b = Integer.parseInt(parts[1]);
                    return GcdLcm.calculate(a, b);
                }
                case 4:
                    return StringManipulation.reverseString(data);
                case 5:
                    return StringManipulation.AdvancedOperations.processString(data);
                case 6:
                    return StringManipulation.Analysis.analyzeString(data);
                default:
                    return "Lựa chọn không hợp lệ\n";
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vui lòng nhập số nguyên hợp lệ");
        }
    }
} 