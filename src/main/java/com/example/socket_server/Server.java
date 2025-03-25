package com.example.socket_server;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server đang lắng nghe trên cổng " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client đã kết nối: " + clientSocket.getInetAddress());

                    int choice = Integer.parseInt(in.readLine());
                    if (choice == 1) {
                        out.println("Hello từ Server!");
                    } else {
                        out.println("Sai lựa chọn!");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
