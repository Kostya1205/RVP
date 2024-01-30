package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaApplication {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)){
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            String inputLine;
            while ((inputLine = input.readLine()) != null) {
//                System.out.println("Message from client: " + inputLine);
//                // Отправляем обратно клиенту полученную строку
//                output.println(inputLine);
                Result result = new Result(inputLine);
                output.println(result.getResult());
            }
            input.close();
            output.close();
            socket.close();
            System.out.println("Connection is closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}