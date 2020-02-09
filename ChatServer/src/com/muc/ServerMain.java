package com.muc;

public class ServerMain {
    public static void main(String[] args) {
        // Create port and server socket, socket need to be wrapped in Try/Catch
        int port = 8818;
        Server server = new Server(port);
        server.start();
    }

}
