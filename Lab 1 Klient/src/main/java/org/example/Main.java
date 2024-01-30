package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 12345;
        try (
            Socket socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ){
            System.out.println("Connecting  to server...");

            // Отправляем сообщение серверу
            System.out.println("Enter message for server:");
            String message = input.readLine();
            output.println(message);

            // Ждем ответа от сервера и выводим его на экран
            System.out.println("Server response: " + serverInput.readLine());
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}