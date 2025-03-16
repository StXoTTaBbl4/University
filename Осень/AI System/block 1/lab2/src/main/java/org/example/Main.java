package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        InputHandler handler = new InputHandler();
        while (true){
            input = reader.readLine();
            if (input.equals("exit")){
                System.exit(0);
            }
            handler.handle(input);
            System.out.println(handler);
        }
    }
}