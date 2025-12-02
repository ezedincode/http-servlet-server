package com.ezedin.Httpserver.httpserver.connectionUtil;

import java.io.IOException;
import java.net.ServerSocket;

public class httpConnectionUtil {

    private httpConnectionUtil() {

    }
    public static ServerSocket createHttpServerSocket (int port) {
        try{
            var serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try{
                    serverSocket.close();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }) );
            return serverSocket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
