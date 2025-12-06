package com.ezedin.Httpserver.httpserver.connections;

import com.ezedin.Httpserver.httpserver.httpDataUtil;
import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.httpserver.httpResponse;
import com.ezedin.Httpserver.httpserver.models.HttpMethod;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import com.ezedin.Httpserver.httpserver.models.HttpStatus;
import com.ezedin.Httpserver.servlet.dispatcher;

public class httpServerConnection extends Thread {
    private final Socket socket;
    private final dispatcher dispatcher;

    public httpServerConnection(Socket socket,dispatcher dispatchers) {
        this.socket = socket;
        this.dispatcher = dispatchers;
    }

    @Override
    public void run() {
        try{
                var inputStream = socket.getInputStream();
                var outputStream = socket.getOutputStream();
                var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String inputLine;
            var startLine = "";
            while ((startLine = bufferedReader.readLine()) != null) {
                if (!startLine.isBlank()) break;
            }


            if (startLine == null || startLine.isBlank()) {
                return;
            }

            var request = httpDataUtil.parseBasicHttpRequest(startLine);
            while((inputLine = bufferedReader.readLine()) != null && !(inputLine.isEmpty())){
                var data = inputLine.split(": ");
                request.headers().put(data[0], data[1]);
            }
            if(request.httpMethod() == HttpMethod.POST){
                var size = Integer.parseInt(request.headers().getOrDefault("Content-Length", "0"));
                var inputStringBuilder = new StringBuilder();
                for(int i = 0; i < size; i++){
                    inputStringBuilder.append((char)bufferedReader.read());
                }
                request = httpRequest.withBody(request, inputStringBuilder.toString());
            }
            Object  body;
            if(Objects.equals(request.headers().get("Content-Type"), "application/json")){
                body = dispatcher.dispatch(request,"json");
            }
            else {
                body = dispatcher.dispatch(request,"text");
            }


            httpResponse response;
            if(body.toString().startsWith("404")){
                response = httpResponse.basic(HttpStatus.NOT_FOUND, body);
            } else if(body.toString().startsWith("500")){
                response = httpResponse.basic(HttpStatus.INTERNAL_SERVER_ERROR, body);
            } else {
                response = httpResponse.basic(HttpStatus.OK, body);
            }

            var httpFormatResponse = response.createHttpResponseMsg();
            bufferedWriter.write(httpFormatResponse);
            bufferedWriter.flush();
            socket.close();



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
