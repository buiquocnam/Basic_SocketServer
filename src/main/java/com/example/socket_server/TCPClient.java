package com.example.socket_server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) {
        // Lấy địa chỉ server từ tham số dòng lệnh hoặc sử dụng localhost
        String serverAddress = args.length > 0 ? args[0] : "localhost";
        // Lấy port từ tham số dòng lệnh hoặc sử dụng port mặc định
        int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        try (
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Đã kết nối đến server: " + serverAddress + ":" + serverPort);

            // Tạo thread riêng để đọc phản hồi từ server
            Thread serverListener = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Lỗi kết nối đến server: " + e.getMessage());
                    }
                }
            });
            serverListener.start();

            // Vòng lặp chính để đọc input từ người dùng
            System.out.println("Nhập tin nhắn (gõ 'exit' để thoát):");
            String userInput;
            while (!(userInput = scanner.nextLine()).equalsIgnoreCase("exit")) {
                out.println(userInput);
            }
            out.println("exit");

        } catch (UnknownHostException e) {
            System.err.println("Không thể tìm thấy server: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Lỗi kết nối đến server: " + e.getMessage());
        }
    }
} 