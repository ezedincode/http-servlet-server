package com.ezedin.Httpserver.httpserver.connections;

import com.ezedin.Httpserver.httpserver.httpDataUtil;
import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.httpserver.models.HttpMethod;

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
            var request = httpDataUtil.parseBasicHttpRequest(startLine);
            while((inputLine = bufferdReader.readLine()) != null && !(inputLine.isEmpty())){
                var data = inputLine.split(": ");
                request.headers().put(data[0], data[1]);
            }
            if(request.httpMethod() == HttpMethod.POST){
                var size = Integer.parseInt(request.headers().getOrDefault("Content-Length", "0"));
                var inputStringBuilder = new StringBuilder();
                for(int i = 0; i < size; i++){
                    inputStringBuilder.append((char)bufferdReader.read());
                }
                request = httpRequest.withBody(request,inputStringBuilder.toString());

            }


        }catch (IOException e){
            e.getMessage();
        }finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }

    }
}
