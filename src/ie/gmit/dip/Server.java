package ie.gmit.dip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is the the Server class for the chat application. It creates a server socket and is capable of 
 * communication with multiple clients simultaneously. The server has a number of commands that clients
 * can accessed through the use of the prefix #.
 * 
 * @author PJ
 * @version 1.0
 * @since 1.0
 */
public class Server {

	private ServerSocket serverSocket;
	private static int SERVERPORT;

	/**
	 * Constructor to create Server instance using ServerSocket object.
	 * 
	 * @param serverSocket A new instance of a ServerSocketImpl class.
	 */
	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	// Method creates a server socket and then creates a new thread for the ClientThread for each new socket that its accepts.
	private void runServer() {
		System.out.println("Server started. Listening on port: " + serverSocket.getLocalPort());
		try {
			// while loops until the serverSocket is closed
			while (!serverSocket.isClosed()) {
				
				/*
				 * A block of code creates to new thread for each accepted socket using the ClientThread class 
				 */
				Socket socket = serverSocket.accept(); 								// Listens and accept the connection from the client. Blocking
				System.out.println(
						"A new client has connected at port " + socket.getPort()); 	// Display the client's port number in the server console
				ClientThread clientThread = new ClientThread(socket); 				// Pass the socket to a Runnable class
				Thread t = new Thread(clientThread); 								// Run the clientThread connection on its own thread
				t.start();															// Start thread				
			}
		} catch (IOException e) {
			closeServerSocket();													// Close the server socket if an error is thrown
		}
	}

	// Method closes the instance of the server socket
	private void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		Addressing ad = new Addressing();											// Create class for port number method 
		SERVERPORT = ad.getPortNumber(scanner);										// Validates and assigns the port number

		ServerSocket sc = new ServerSocket(SERVERPORT);								// Create a new ServerSocket
		Server server = new Server(sc);												// Create the server class with the new ServerSocket
		server.runServer();															// Run the main Server method
		
	}

}
