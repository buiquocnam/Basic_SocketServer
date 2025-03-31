package com.example.socket_server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.nio.file.Paths;

@WebServlet(name = "WebSocket Chat", urlPatterns = { "/chat" })
public class WebSocketServer extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(ChatWebSocket.class);
    }

    public static class ChatWebSocket extends WebSocketAdapter {
        @Override
        public void onWebSocketConnect(Session session) {
            super.onWebSocketConnect(session);
            System.out.println("Client đã kết nối: " + session.getRemoteAddress());
            try {
                String message = "Chào mừng! Hãy nhập số 1 để nhận lời chào, hoặc 'exit' để thoát.";
                session.getRemote().sendString(new String(message.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onWebSocketText(String message) {
            super.onWebSocketText(message);
            System.out.println("Nhận từ client: " + message);

            try {
                if ("exit".equalsIgnoreCase(message.trim())) {
                    String response = "Tạm biệt!";
                    getSession().getRemote().sendString(new String(response.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                    getSession().close();
                    return;
                }

                try {
                    int choice = Integer.parseInt(message.trim());
                    String response;
                    if (choice == 1) {
                        response = "Xin chào từ Server!";
                    } else {
                        response = "Lựa chọn không hợp lệ! Vui lòng chọn số 1.";
                    }
                    getSession().getRemote().sendString(new String(response.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                } catch (NumberFormatException e) {
                    String response = "Vui lòng nhập một số nguyên hợp lệ!";
                    getSession().getRemote().sendString(new String(response.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            super.onWebSocketClose(statusCode, reason);
            System.out.println("Client đã ngắt kết nối: " + getSession().getRemoteAddress());
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            super.onWebSocketError(cause);
            System.err.println("Lỗi WebSocket: " + cause.getMessage());
        }
    }

    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Cấu hình CORS
        FilterHolder corsFilter = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Sec-WebSocket-Extensions,Sec-WebSocket-Key,Sec-WebSocket-Version");
        corsFilter.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "false");

        // Đặt encoding cho context
        context.setInitParameter("org.eclipse.jetty.servlet.Default.charEncoding", "UTF-8");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        
        // Thêm WebSocket servlet
        context.addServlet(new ServletHolder(new WebSocketServer()), "/chat");

        // Thêm servlet cho static files
        ServletHolder staticHolder = new ServletHolder("default", new org.eclipse.jetty.servlet.DefaultServlet());
        String publicPath = System.getenv("PUBLIC_PATH");
        if (publicPath == null || publicPath.isEmpty()) {
            // Đường dẫn local development
            publicPath = Paths.get("src/main/public").toAbsolutePath().toString();
        }
        staticHolder.setInitParameter("resourceBase", publicPath);
        staticHolder.setInitParameter("dirAllowed", "true");
        staticHolder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(staticHolder, "/");

        // Thêm health check endpoint
        ServletHolder healthHolder = new ServletHolder("health", new org.eclipse.jetty.servlet.DefaultServlet() {
            @Override
            protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("Máy chủ đang hoạt động!");
            }
        });
        context.addServlet(healthHolder, "/health");

        try {
            server.start();
            System.out.println("WebSocket Server đang chạy tại ws://localhost:" + port + "/chat");
            System.out.println("Truy cập giao diện web tại http://localhost:" + port);
            System.out.println("Public path: " + publicPath);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 