import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/orders", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String response;

                if ("GET".equalsIgnoreCase(method)) {
                    // Friendly message when opened in a browser
                    response = "{\n" +
                            "  \"message\": \"Welcome to the Dummy E-commerce API!\",\n" +
                            "  \"endpoints\": {\n" +
                            "    \"Place Order\": \"POST /api/orders\",\n" +
                            "    \"Cancel Order\": \"DELETE /api/orders/{orderId}\"\n" +
                            "  }\n" +
                            "}";
                    sendResponse(exchange, 200, response);

                } else if ("POST".equalsIgnoreCase(method)) {
                    // 1. Place Order Method
                    response = placeOrder();
                    sendResponse(exchange, 200, response);

                } else if ("DELETE".equalsIgnoreCase(method)) {
                    // 2. Cancel Order Method
                    String[] segments = path.split("/");
                    String orderId = (segments.length > 3) ? segments[3] : "UNKNOWN_ID";
                    response = cancelOrder(orderId);
                    sendResponse(exchange, 200, response);

                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("🚀 E-commerce API running at: http://localhost:8080/api/orders");
    }

    public static String placeOrder() {
        String generatedId = "ORD-" + System.currentTimeMillis();
        return "{\n" +
                "  \"status\": \"CREATED\",\n" +
                "  \"message\": \"Order placed successfully!\",\n" +
                "  \"orderId\": \"" + generatedId + "\"\n" +
                "}";
    }

    public static String cancelOrder(String orderId) {
        // Endpoint requires order ID and changes status to cancelled
        return "{\n" +
                "  \"status\": \"CANCELLED\",\n" +
                "  \"message\": \"Order " + orderId + " has been successfully cancelled.\",\n" +
                "  \"orderId\": \"" + orderId + "\"\n" +
                "}";
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}