package com.ezedin.Httpserver.httpserver.connections;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class httpServerConnection extends Thread {
    private final Socket socket;
    private final Logger logger = Logger.getLogger(this.getName());

    public httpServerConnection(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try(
                var inputStream = socket.getInputStream();
                var outputStream = socket.getOutputStream();
                var bufferdReader = new BufferedReader(new InputStreamReader(inputStream));
                var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                ){
            String inputLine;
            var startLine = bufferdReader.readLine();

        }

    }
}
