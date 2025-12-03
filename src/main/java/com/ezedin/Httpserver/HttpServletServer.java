package com.ezedin.Httpserver;

import com.ezedin.Httpserver.httpserver.connectionUtil.httpConnectionUtil;
import com.ezedin.Httpserver.httpserver.connections.httpServerConnection;
import com.ezedin.Httpserver.servlet.dispatcher;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ezedin.Httpserver.httpserver.commonConstants.DEFAULT_PORT;

public class HttpServletServer {

	private final int port;
	private final String basePackage;

	public HttpServletServer(String basePackage, int port) {
		this.basePackage = basePackage;
		this.port = port;
	}

	public HttpServletServer(String basePackage) {
		this(basePackage, DEFAULT_PORT);
	}

	public void start() throws IOException {
		var serverSocket = httpConnectionUtil.createHttpServerSocket(port);
		dispatcher dispatchers = new dispatcher(basePackage);

		ExecutorService pool = Executors.newFixedThreadPool(50);

		while(true) {
			try {
				var clientSocket = serverSocket.accept();
				pool.submit(() -> {
					var httpConnection = new httpServerConnection(clientSocket, dispatchers);
					httpConnection.start();
				});
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

	}
}
