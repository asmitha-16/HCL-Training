package com.campus.lostandfound.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class EmbeddedWebServer {

    private final int port;
    private final ApiRouter apiRouter;
    private final Path staticResourceDir;
    private HttpServer server;

    public EmbeddedWebServer(int port, ApiRouter apiRouter, Path staticResourceDir) {
        this.port = port;
        this.apiRouter = apiRouter;
        this.staticResourceDir = staticResourceDir;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(16));

        server.createContext("/", new UnifiedHandler());
        server.start();
        System.out.println("=====================================================================");
        System.out.println(">>> Campus Lost & Found Platform is RUNNING on http://localhost:" + port);
        System.out.println("=====================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class UnifiedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();
            String method = exchange.getRequestMethod();

            if (path.startsWith("/api/")) {
                // Read request body
                String body = "";
                try (InputStream is = exchange.getRequestBody()) {
                    body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }

                String clientIp = exchange.getRemoteAddress().getAddress().getHostAddress();
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

                ApiRouter.HttpResponse resp = apiRouter.route(method, path, query, body, clientIp, authHeader);

                byte[] respBytes = resp.body().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", resp.contentType());
                exchange.sendResponseHeaders(resp.statusCode(), respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } else {
                // Static resource
                serveStaticFile(exchange, path);
            }
        }

        private void serveStaticFile(HttpExchange exchange, String path) throws IOException {
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            Path filePath = staticResourceDir.resolve(path.startsWith("/") ? path.substring(1) : path);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                filePath = staticResourceDir.resolve("index.html");
            }

            if (!Files.exists(filePath)) {
                byte[] notFound = "<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound);
                }
                return;
            }

            String contentType = determineContentType(filePath.toString());
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String determineContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=utf-8";
            if (filename.endsWith(".css")) return "text/css; charset=utf-8";
            if (filename.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (filename.endsWith(".json")) return "application/json; charset=utf-8";
            if (filename.endsWith(".svg")) return "image/svg+xml";
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain; charset=utf-8";
        }
    }
}
