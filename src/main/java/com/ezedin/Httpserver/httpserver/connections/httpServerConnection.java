package com.ezedin.Httpserver.httpserver.connections;

import com.ezedin.Httpserver.httpserver.httpDataUtil;
import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.httpserver.httpResponse;
import com.ezedin.Httpserver.httpserver.models.HttpMethod;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

import com.ezedin.Httpserver.httpserver.models.HttpStatus;
import com.ezedin.Httpserver.servlet.dispatcher;

public class httpServerConnection extends Thread {
    private final Socket socket;
    private final Logger logger = Logger.getLogger(this.getName());
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
                var bufferdReader = new BufferedReader(new InputStreamReader(inputStream));
                var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String inputLine;
            var startLine = "";
            while ((startLine = bufferdReader.readLine()) != null) {
                if (!startLine.isBlank()) break;
            }


            if (startLine == null || startLine.isBlank()) {
                System.out.println(startLine + " in while loop");
                throw new IllegalArgumentException("Empty HTTP request (startLine is null or blank)");
            }

            System.out.println("Start line: [" + startLine + "]");
            var request = httpDataUtil.parseBasicHttpRequest(startLine);
            System.out.println(request);
            while((inputLine = bufferdReader.readLine()) != null && !(inputLine.isEmpty())){
                var data = inputLine.split(": ");
                request.headers().put(data[0], data[1]);
            }
            System.out.println(request);
            if(request.httpMethod() == HttpMethod.POST){
                var size = Integer.parseInt(request.headers().getOrDefault("Content-Length", "0"));
                var inputStringBuilder = new StringBuilder();
                System.out.println("size : "+size);
                for(int i = 0; i < size; i++){
                    inputStringBuilder.append((char)bufferdReader.read());
                }
                request = httpRequest.withBody(request, inputStringBuilder.toString());
            }

            var body = dispatcher.dispatch(request.httpMethod(), request.Path());
            System.out.println(body);

            httpResponse response;
            if(body.startsWith("404")){
                response = httpResponse.basic(HttpStatus.NOT_FOUND, body);
            } else if(body.startsWith("500")){
                response = httpResponse.basic(HttpStatus.INTERNAL_SERVER_ERROR, body);
            } else {
                response = httpResponse.basic(HttpStatus.OK, body);
            }

            System.out.println(response);
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
