package com.muc;

import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList(){
        return workerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            // While loop let the server ON until we decide to kill it
            while (true) {
                System.out.println("About to accept client connection...");
                // Socket method accept() to create connection between server and client eg.. telnet localhost port
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected with " + clientSocket);
                ServerWorker worker = new ServerWorker(this,clientSocket);
                workerList.add(worker);
                worker.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
