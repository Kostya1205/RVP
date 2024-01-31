package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaApplication {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)){
            List<Book> books = new ArrayList<>();
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new Thread(() -> handleClient(socket,books)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleClient(Socket clientSocket,List<Book> books) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            // Чтение HTTP-запроса
            StringBuilder request = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                request.append(line).append("\r\n");
            }

            // Поиск соответствия
            if (request.toString().startsWith("GET")) {

                // Регулярное выражение для поиска строки после "GET /" и перед " HTTP/1.1"
                Pattern pattern = Pattern.compile("GET /(.+?) HTTP/1\\.1");
                Matcher matcher = pattern.matcher(request.toString());
                String extractedString = new String("");
                if (matcher.find()) {
                    // Извлечение найденной строки
                    extractedString = matcher.group(1);
                }
                String htmlContent = "";
                String contentType = "";
                    if(Objects.equals(extractedString, "exercise")){
                    htmlContent = loadFile("src/main/resources/exercise.html");
                    contentType="text/html";
                    }else if(Objects.equals(extractedString,"table")){
                        htmlContent = loadFile("src/main/resources/table.html");
                        contentType="text/html";
                    }else if(Objects.equals(extractedString,"table/info")){
                        htmlContent = convertListToJson(books);
                        contentType="application/json";
                    } else if (Objects.equals(extractedString, "js/exercise.js")) {
                                // Загрузка и отправка содержимого JavaScript файла
                        htmlContent = loadFile("src/main/resources/js/exercise.js");
                        contentType="application/javascript";
                    } else if (Objects.equals(extractedString, "js/table.js")) {
                        htmlContent = loadFile("src/main/resources/js/table.js");
                        contentType="application/javascript";
                    } else if (Objects.equals(extractedString, "css/main.css")) {
                                // Загрузка и отправка содержимого JavaScript файла
                        htmlContent = loadFile("src/main/resources/css/main.css");
                        contentType="text/css";
                    } else if (Objects.equals(extractedString, "css/exercise.js")) {
                        htmlContent = loadFile("src/main/resources/css/exercise.css");
                        contentType="text/css";
                    } else if (Objects.equals(extractedString, "css/table.css")) {
                        htmlContent = loadFile("src/main/resources/css/table.css");
                        contentType="text/css";
                    }else {
                        htmlContent = loadFile("src/main/resources/about.html");
                        contentType="text/html";
                    }
                sendResponseToClient(htmlContent,outputStream,contentType);

            } else if (request.toString().startsWith("POST")) {
                StringBuilder stringBuilder = new StringBuilder();
                while (reader.ready()) {
                    stringBuilder.append((char) reader.read());
                }
                String body = stringBuilder.toString();
                // Регулярное выражение для поиска строки после "GET /" и перед " HTTP/1.1"
                Pattern pattern = Pattern.compile("POST /(.+?) HTTP/1\\.1");
                Matcher matcher = pattern.matcher(request.toString());
                String extractedString = new String("");
                if (matcher.find()) {
                    // Извлечение найденной строки
                    extractedString = matcher.group(1);
                }

                String responseData = "";
                if(Objects.equals(extractedString, "exercise")){
                    Result result = new Result(body.replace("\"", ""));
                    responseData="\"" +result.getResult()+ "\"";
                }else if(Objects.equals(extractedString,"table/add")){
                    Book book = new ObjectMapper().readValue(body,Book.class);
                    book.setId(UUID.randomUUID().toString());
                    books.add(book);
                    responseData = convertListToJson(books);
                }else if(Objects.equals(extractedString,"table/delete")){
                    Book book = new ObjectMapper().readValue(body,Book.class);
                    for (Book book1:books){
                        if(Objects.equals(book1.getId(), book.getId()))
                        {
                            books.remove(book1);
                            break;
                        }
                    }
                    responseData = convertListToJson(books);
                }else if(Objects.equals(extractedString,"table/change")){
                    Book book = new ObjectMapper().readValue(body,Book.class);
                    for (Book book1:books){
                        if(Objects.equals(book1.getId(), book.getId()))
                        {
                            book1.setName(book.getName());
                            book1.setPrice(book.getPrice());
                            book1.setVolume(book.getVolume());
                            break;
                        }
                    }
                    responseData = convertListToJson(books);
                }

                sendResponseToClient(responseData,outputStream,"application/json");

            } else  {
                sendResponseToClient("",outputStream,"application/json");
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponseToClient(String htmlContent,OutputStream outputStream,String contentType) {
        try {
            int contentLength = 0;
            contentLength = htmlContent.getBytes("UTF-8").length;

            String response = "HTTP/1.1 200 OK\r\nContent-Type: "+contentType+"; charset=UTF-8\r\nContent-Length: " +
                    contentLength + "\r\n\r\n" +
                    htmlContent;
        // Отправка ответа клиенту
        outputStream.write(response.getBytes());
        outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String loadFile(String filePath) {
        try {
            // Чтение содержимого HTML-файла
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "<html><body><h1>Error loading HTML file</h1></body></html>";
        }
    }
    private static String convertListToJson(List<Book> books) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(books);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}