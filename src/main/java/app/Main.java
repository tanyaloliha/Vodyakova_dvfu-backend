package app;

import app.model.Client;
import app.model.Workout;
import app.service.FitnessService;
import tools.jackson.databind.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final FitnessService service = new FitnessService();
    private static final JsonMapper mapper = JsonMapper.builder().build();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/workouts", new WorkoutHandler());
        server.createContext("/clients", new ClientHandler());
        server.createContext("/enroll", new EnrollHandler());
        
        server.setExecutor(null); 
        server.start();
        System.out.println("Сервер Фитнес-клуба запущен на порту 8080");
    }

    static class WorkoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                if (method.equals("GET")) {
                    if (parts.length == 2) { 
                        String query = exchange.getRequestURI().getQuery();
                        String type = null;
                        Boolean hasFreeSpots = null;
                        if (query != null) {
                            for (String param : query.split("&")) {
                                String[] kv = param.split("=");
                                if (kv[0].equals("type")) type = kv[1];
                                if (kv[0].equals("hasFreeSpots")) hasFreeSpots = Boolean.parseBoolean(kv[1]);
                            }
                        }
                        sendJson(exchange, 200, service.getWorkouts(type, hasFreeSpots));
                    } else if (parts.length == 3) { 
                        Workout w = service.getWorkout(parts[2]);
                        if (w != null) sendJson(exchange, 200, w);
                        else sendError(exchange, 404, "Тренировка не найдена");
                    }
                } else if (method.equals("POST")) { 
                    Workout newWorkout = mapper.readValue(exchange.getRequestBody(), Workout.class);
                    service.createWorkout(newWorkout);
                    sendJson(exchange, 201, newWorkout);
                } else if (method.equals("PUT")) { 
                    Workout updated = mapper.readValue(exchange.getRequestBody(), Workout.class);
                    Workout result = service.updateWorkout(parts[2], updated);
                    if (result != null) sendJson(exchange, 200, result);
                    else sendError(exchange, 404, "Тренировка не найдена");
                } else if (method.equals("DELETE")) { 
                    boolean deleted = service.deleteWorkout(parts[2]);
                    if (deleted) sendResponse(exchange, 204, "");
                    else sendError(exchange, 404, "Тренировка не найдена");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                sendError(exchange, 400, e.getMessage());
            } catch (Exception e) {
                sendError(exchange, 500, "Внутренняя ошибка сервера");
            }
        }
    }

    static class ClientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                if (method.equals("POST")) {
                    Client c = mapper.readValue(exchange.getRequestBody(), Client.class);
                    service.createClient(c);
                    sendJson(exchange, 201, c);
                } else if (method.equals("GET") && parts.length == 3) {
                    Client c = service.getClient(parts[2]);
                    if (c != null) sendJson(exchange, 200, c);
                    else sendError(exchange, 404, "Клиент не найден");
                }
            } catch (IllegalArgumentException e) {
                sendError(exchange, 400, e.getMessage());
            } catch (Exception e) {
                sendError(exchange, 500, "Внутренняя ошибка сервера");
            }
        }
    }

    static class EnrollHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String path = exchange.getRequestURI().getPath(); 
                String[] parts = path.split("/");
                if (parts.length != 4) {
                    sendError(exchange, 400, "Неверный путь. Используйте /enroll/{clientId}/{workoutId}");
                    return;
                }
                String clientId = parts[2];
                String workoutId = parts[3];

                if (exchange.getRequestMethod().equals("POST")) {
                    service.enrollClient(clientId, workoutId);
                    sendResponse(exchange, 200, "{\"message\":\"Клиент успешно записан на тренировку\"}");
                } else if (exchange.getRequestMethod().equals("DELETE")) {
                    service.cancelEnrollment(clientId, workoutId);
                    sendResponse(exchange, 200, "{\"message\":\"Запись успешно отменена\"}");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                sendError(exchange, 400, e.getMessage());
            } catch (Exception e) {
                sendError(exchange, 500, "Внутренняя ошибка сервера");
            }
        }
    }

    private static void sendJson(HttpExchange exchange, int status, Object obj) throws IOException {
        String response = mapper.writeValueAsString(obj);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        sendResponse(exchange, status, response);
    }

    private static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        String safeMessage = message.replace("\"", "\\\"");
        String json = "{\"error\":\"" + safeMessage + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        sendResponse(exchange, status, json);
    }

    private static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}