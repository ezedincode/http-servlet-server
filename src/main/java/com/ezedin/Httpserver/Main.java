package com.ezedin.Httpserver;


import com.ezedin.Httpserver.httpserver.connectionUtil.httpConnectionUtil;
import com.ezedin.Httpserver.httpserver.connections.httpServerConnection;

import java.io.IOException;

import static com.ezedin.Httpserver.httpserver.commonConstants.DEFAULT_PORT;

public class Main {

	public static void main(String[] args) throws IOException {
		var serverSocket = httpConnectionUtil.createHttpServerSocket(DEFAULT_PORT);
		while (true) {
			var clientSocket = serverSocket.accept();
			var httpConnection = new httpServerConnection(clientSocket);
			httpConnection.start();
		}
	}


}
