package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a socket for the clients side of the chat application.
 * 
 * There is three commands that the client can use with the command prefix # when sending a chat message:
 * <ol>
 * 	<li><strong>\q:</strong><p>Exits the chat room.</p>
 * 	<li><strong>#userlist</strong><p>Lists users in the chat room.</p>
 * 	<li><strong>#*</strong><p>Replace the * with a username to send a private message to that user.</p>
 * <ol>
 * 
 * @author PJ
 * @version 1.0
 * @since 1.0
 */
public class Client {

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String userName;
	private static boolean toQuit = false;
	private static int PORT;
	private static String IPADDRESS;

	/**
	 * Constructor creates a client instance using a socket and a chat room username.
	 * 
	 * @param socket A connected stream socket connected to the user's specified port & IP
	 * @param userName A chat room username chosen by the user.
	 */
	public Client(Socket socket, String userName) {
		try {
			this.socket = socket;
			this.userName = userName;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			closeResources(socket, bufferedReader, bufferedWriter);
		}
	}
	
	// Method attempts to make a connection to the server for a number of specified attempts
	private static void connect(String userName) {
		Socket socket;
		// A short loop for demonstration purposes
		int reconnect = 3;		// Number of attempts to reconnect
		int keepAlive = 5; 		// Time (s) between each attempt

		while (reconnect > 0 && !toQuit) {
			try {
				socket = new Socket(IPADDRESS, PORT);		// Create a socket using the saved IP Address/Port number
				Client client = new Client(socket, userName);
				System.out.print("You have now entered the chat room as ");
				client.listen();							// Method runs inside its own thread so it does not block the application
				client.sendMessage();
			} catch (ConnectException e) {
				System.out.println("Cannot find server! \nTry to reconnect in " + keepAlive + " seconds" + " Number of attempts remaining: " + reconnect);
		        // Timer to between reconnect attempts
				try {
		            TimeUnit.SECONDS.sleep(keepAlive); 
		        } catch(InterruptedException ie) {
		            // Interrupted.
		        }
			} catch (SocketException se) {
				System.out.println("No server active on that port");
				break; 
			} catch (IOException e) {
				System.out.println("IO Exception");
			}
			reconnect--;
		}
	}
	
	// Method reads input from the user's console and sends it to the server
	private void sendMessage() {
		try {
			// Send the username to identify the sender
			flushToBuffer(userName);
			Scanner sc = new Scanner(System.in);
			String outputMessage = "";
			
			// Loop reads each line of input from the client application
			while (!toQuit) {
				outputMessage = sc.nextLine(); 				// Blocking operation
				// COMMAND: If user inputs quit command, send command to server and exit the current loop
				if ("\\q".equalsIgnoreCase(outputMessage)) {
					flushToBuffer("\\q");
					System.out.println("You left the chat room.");
					toQuit = true;
				}
				// COMMAND: If the user requests the current online users in the chat room send command to server
				else if ("#userlist".equalsIgnoreCase(outputMessage)) {
					flushToBuffer("#userlist");
					continue;
				// Send the user's input to the server along with their username prefix.
				} else {
					flushToBuffer(userName + ":" + outputMessage);
				}
			}
			sc.close();
		} catch (Exception e) {
			closeResources(socket, bufferedReader, bufferedWriter);
		}
	}
	
	// Method writes input message to bufferWriter and flushes the stream
	private void flushToBuffer(String msg) throws IOException {
		bufferedWriter.write(msg);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}

	// Reading input from the input stream is processed in its own thread to prevent blocking
	private void listen() throws SocketException {
		// use lambda syntax for creating a new Thread
		new Thread(() ->  {
				String incomingMessage;									// Store the incoming message as a string
				
				// Continue to read from the socket input stream until quit trigger set
				while (!toQuit) {
					try {
						incomingMessage = bufferedReader.readLine(); 	// Blocking operation
		
						if (incomingMessage.contains("#name")) {
							incomingMessage = incomingMessage.replace("#name", "");
							userName = incomingMessage;
							System.out.println(incomingMessage);
						}
						else if (incomingMessage != null) {
							System.out.println(incomingMessage);
						} 
					} catch (Exception e) { 
						System.out.println("Disconnected from server."); 
						break; 											// Break out of the listening for input loop
					}
				}
		}).start(); 
	}
	
	// Method closes these resources and the underlying streams they are wrapping
	private void closeResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			// Close BufferedReader/InputStreamReader
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			// Close BufferedWriter/OutputStreamWriter
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			// Closes Socket/InputStream/OutputStream
			if (socket != null) {
				socket.close();
				// System.out.println("Socket Closed"); Causes infinite loop.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Main method
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Addressing ad = new Addressing();
		System.out.println("Socket Chat Client Application V1.0");
		IPADDRESS = ad.getAddress(sc);									// Prompts user with IP address options
		PORT = ad.getPortNumber(sc);									// Prompts user to enter a port number
		System.out.println("Enter your user name for the group chat: ");
		String userName = sc.nextLine();								// Save user name
		connect(userName);												// Attempt to create a socket using the input port number
		
		// Loop to keep the application alive and connect to another port or shutdown
		while (!toQuit) {
			System.out.println("Close application? [Y/N]");
			String res = sc.nextLine();
			if ("n".equalsIgnoreCase(res)) {
				System.out.println("Ok try again");
				ad.getPortNumber(sc);
				connect(userName);
			} else if ("y".equalsIgnoreCase(res)) {
				toQuit = true;
				System.out.println("Client shutdown");
			} else {
				System.out.println("Try again");
			}
		}
		sc.close();
	}

}
