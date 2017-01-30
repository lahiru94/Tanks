package tank.game;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Gobinath
 */
public class Controller {

    private static Controller instance;

    private Controller() {

    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void sendMessage(String msg) {
        try (Socket serverSocket = new Socket(Manifest.SERVER_IP, Manifest.SERVER_PORT);) {
            if (serverSocket.isConnected()) {
                try (BufferedWriter output = new BufferedWriter(
                        new OutputStreamWriter(serverSocket.getOutputStream()));) {
                    output.write(msg);
                }
            }

        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public String receiveMessage() {
        String readLine = "#";
        try (ServerSocket ServerSocketForClient = new ServerSocket(Manifest.CLIENT_PORT);
                Socket clientSocket = ServerSocketForClient.accept();) {
            if (ServerSocketForClient.isBound()) {
                try (BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));) {
                    while (!input.ready()) {
                        Thread.sleep(500);
                    }
                    readLine = input.readLine();
                    return readLine.split("#")[0];
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "";
    }
}
