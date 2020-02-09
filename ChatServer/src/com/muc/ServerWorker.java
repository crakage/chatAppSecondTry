package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ServerWorker extends Thread{
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    // Moved from ServerMain for clarity, this is what happen the connection is accepted
    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0){
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)){
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }

        }
//        OutputStream outputStream = clientSocket.getOutputStream();
//        for (int i=0; i<10; i++){
//            outputStream.write(("Time is now " + new Date() + "\n").getBytes());
//            Thread.sleep(1000);
//        }
//        // Close the connection once job is done.
        clientSocket.close();
    }

    private void handleLogoff() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        // Send other online user, current user's status
        String onlineMsg = "offline: " + login + "\n";
        for (ServerWorker worker : workerList){
            if (!login.equals(worker.getLogin())){
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest")) || ((login.equals("jim") && password.equals("jim")))){
                String msg = "OK login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in Successfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                // Server current user all other online logins
                for (ServerWorker worker: workerList){
                    if (worker.getLogin() != null){
                        if (!login.equals(worker.getLogin())){
                            String msg2 = "online: " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }
                // Send other online user, current user's status
                String onlineMsg = "online: " + login + "\n";
                for (ServerWorker worker : workerList){
                    if (!login.equals(worker.getLogin())){
                        worker.send(onlineMsg);
                    }
                }

            } else {
                String msg = "Error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        if(login != null){
            outputStream.write(msg.getBytes());
        }

    }
}
