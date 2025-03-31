package com.example.socket_server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import com.example.socket_server.exercises.NumberChecking;
import com.example.socket_server.exercises.DigitOperations;
import com.example.socket_server.exercises.GcdLcm;
import com.example.socket_server.exercises.StringManipulation;

import java.nio.file.Paths;

/**
 * WebSocket Server để xử lý các yêu cầu từ client
 * - Hỗ trợ 6 chức năng xử lý số và chuỗi
 * - Giao tiếp với client thông qua WebSocket
 * - Phục vụ giao diện web từ thư mục public
 */
@WebSocket  // Đánh dấu đây là một WebSocket endpoint
public class WebSocketServer {
    // Biến lưu trạng thái: đang ở chức năng nào (0 = menu chính)
    private static int currentFunction = 0;

    /**
     * Hàm main để khởi động server
     */
    public static void main(String[] args) throws Exception {
        // Lấy port từ biến môi trường hoặc dùng port mặc định 8080
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;
        
        // Tạo server với port đã chọn
        Server server = new Server(port);

        // [1] Cấu hình WebSocket
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.getPolicy().setIdleTimeout(10000); // Timeout sau 10 giây không hoạt động
                factory.register(WebSocketServer.class);
            }
        };

        // [2] Cấu hình phục vụ file tĩnh (HTML, CSS, JS)
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        
        // Thư mục chứa file tĩnh (public)
        String publicPath = System.getenv("PUBLIC_PATH");
        if (publicPath == null || publicPath.isEmpty()) {
            publicPath = Paths.get("src/main/public").toAbsolutePath().toString();
        }
        resourceHandler.setResourceBase(publicPath);

        // [3] Kết hợp các handler
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new org.eclipse.jetty.server.Handler[]{resourceHandler, wsHandler});
        server.setHandler(handlers);

        // [4] Khởi động server
        server.start();
        System.out.println("Server đang chạy tại ws://localhost:" + port + "/chat");
        System.out.println("Giao diện web tại http://localhost:" + port);
        System.out.println("Thư mục public: " + publicPath);
        server.join();
    }

    /**
     * Xử lý sự kiện client kết nối
     */
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("Client kết nối: " + session.getRemoteAddress());
        sendMenu(session);  // Gửi menu cho client
    }

    /**
     * Xử lý sự kiện client ngắt kết nối
     */
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Client ngắt kết nối: " + session.getRemoteAddress());
    }

    /**
     * Xử lý tin nhắn từ client
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            if (currentFunction == 0) {
                // [1] Đang ở menu chính -> Xử lý lựa chọn chức năng
                handleMenuChoice(session, message);
            } else {
                // [2] Đang trong một chức năng -> Xử lý dữ liệu đầu vào
                handleFunctionInput(session, message);
            }
        } catch (Exception e) {
            // [3] Xử lý lỗi -> Thông báo và hiển thị lại menu
            session.getRemote().sendString("Lỗi: " + e.getMessage() + "\n\n");
            currentFunction = 0;
            sendMenu(session);
        }
    }

    /**
     * Xử lý khi người dùng chọn chức năng từ menu
     */
    private void handleMenuChoice(Session session, String message) throws Exception {
        int choice = Integer.parseInt(message.trim());
        if (choice >= 1 && choice <= 6) {
            currentFunction = choice;
            session.getRemote().sendString(getPromptForFunction(choice));
        } else {
            session.getRemote().sendString("Lựa chọn không hợp lệ!\n\n");
            sendMenu(session);
        }
    }

    /**
     * Xử lý dữ liệu đầu vào cho chức năng đã chọn
     */
    private void handleFunctionInput(Session session, String message) throws Exception {
        String result = processFunction(currentFunction, message.trim());
        session.getRemote().sendString(result + "\n\n");
        currentFunction = 0;  // Trở về menu chính
        sendMenu(session);
    }

    /**
     * Gửi menu chức năng cho client
     */
    private void sendMenu(Session session) throws Exception {
        StringBuilder menu = new StringBuilder();
        menu.append("=== MENU CHỨC NĂNG ===\n\n");
        menu.append("1. Kiểm tra số:\n");
        menu.append("   - Số nguyên tố\n");
        menu.append("   - Số chính phương\n");
        menu.append("   - Số hoàn hảo\n");
        menu.append("   - Số Armstrong\n\n");
        menu.append("2. Tính toán chữ số:\n");
        menu.append("   - Tổng các chữ số\n");
        menu.append("   - Tích các chữ số\n\n");
        menu.append("3. Tìm ƯCLN và BCNN của hai số\n\n");
        menu.append("4. Xử lý chuỗi cơ bản:\n");
        menu.append("   - Đảo ngược chuỗi\n\n");
        menu.append("5. Xử lý chuỗi nâng cao:\n");
        menu.append("   - Chuyển đổi HOA/thường\n");
        menu.append("   - Đảo ngược HOA/thường\n\n");
        menu.append("6. Phân tích chuỗi:\n");
        menu.append("   - Đếm số từ\n");
        menu.append("   - Tìm nguyên âm\n");
        menu.append("   - Thống kê tần suất\n\n");
        menu.append("Vui lòng chọn chức năng (1-6): ");
        session.getRemote().sendString(menu.toString());
    }

    /**
     * Lấy thông báo hướng dẫn cho từng chức năng
     */
    private String getPromptForFunction(int function) {
        switch (function) {
            case 1: return "Vui lòng nhập một số nguyên để kiểm tra các tính chất:\n";
            case 2: return "Vui lòng nhập một số nguyên để tính tổng và tích các chữ số:\n";
            case 3: return "Vui lòng nhập hai số nguyên (cách nhau bởi dấu cách) để tìm ƯCLN và BCNN:\n";
            case 4: return "Vui lòng nhập chuỗi cần đảo ngược:\n";
            case 5: return "Vui lòng nhập chuỗi cần xử lý nâng cao:\n";
            case 6: return "Vui lòng nhập chuỗi cần phân tích:\n";
            default: return "Lựa chọn không hợp lệ!\n";
        }
    }

    /**
     * Xử lý dữ liệu theo chức năng đã chọn
     */
    private String processFunction(int function, String data) {
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
                        throw new IllegalArgumentException("Vui lòng nhập đúng hai số nguyên, cách nhau bởi dấu cách!");
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
                    return "Lựa chọn không hợp lệ!\n";
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vui lòng nhập số nguyên hợp lệ!");
        }
    }
} 