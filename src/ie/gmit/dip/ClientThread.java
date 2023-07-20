package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * This class implements the Runnable interface to create a thread for each new client.
 * This allows the server to send and receive data concurrently from multiple clients. 
 * 
 * @author PJ
 * @version 1.0
 * @since 1.0
 */
public class ClientThread implements Runnable {

	public static ArrayList<ClientThread> usersList = new ArrayList<>();	// Stores the ClientThread object for current users in the chat room
	private Socket clientSocket;											// Socket object for the new client
	private BufferedReader bufferedReader;									// Reading clients input stream
	private BufferedWriter bufferedWriter;									// Sending server output stream
	private String userName;												// Each user has unique chat name
	
	
	/**
	 * Constructor for creating a new ClientThread using a Socket object.
	 * 
	 * @param clientSocket A new instance of the Socket object to communicate with a new client application.
	 */
	public ClientThread(Socket clientSocket) {

		try {
			this.clientSocket = clientSocket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			String inputName = bufferedReader.readLine(); 					// Receive the username input send from the client
			String newName = uniqueName(inputName);							// Check/modifies a username if already in use
			this.userName = newName;
			usersList.add(this);											// Add this ClientThread to the usersList
	
			groupMessage(
					"Server: " + userName + " has entered the chat.");		// Let other users know the name of the new user in the chat room		
			directMessage("#name" + userName, userName); 					// Update the client with the assigned/modified username
		} catch (IOException e) {
			e.printStackTrace();
			closeResources(clientSocket, bufferedReader, bufferedWriter);
		}

	}
	
	/**
	 * The threaded process. Method listens for incoming messages from clients input stream and send messages back to connected clients.
	 * As the process is multi-threaded the server can accepts and send messages to more than one client at a time as each has their own thread.
	 */
	@Override
	public void run() {
		String incomingMessage;

		while (clientSocket.isConnected()) {
			try {
				// Server listens for input from the client socket input stream
				incomingMessage = bufferedReader.readLine(); // Blocking operation
				
				// Check if the incoming message has the direct message (DM) command 
				boolean containsUser = false;
				String privateUser = "";
				for (ClientThread clientThread : usersList) {
					// Check if the DM command matches to a currently online username
					if (incomingMessage.contains('#' + clientThread.userName)) {
						privateUser = clientThread.userName;				// Assign the username to receive the DM
						containsUser = true;								// Flag the message as a DM
					}
				}
				
				/*
				 * Client side console commands that are sent via input text with a 
				 * special character that the server screens for in received messages. 
				 */
				// COMMAND: Client sends a leave chat room request
				if ("\\q".equalsIgnoreCase(incomingMessage)) {
					// Close all the I/O (Close socket to remove client from connection to server)
					closeResources(clientSocket, bufferedReader, bufferedWriter);
					break;
				// COMMAND: Client requests current users online 
				} else if ("#userlist".equalsIgnoreCase(incomingMessage)) {
					incomingMessage = "Users online: " + getClientList();
					// Use directMessage to send user list back to sender
					directMessage(incomingMessage, userName);
				// COMMAND: Client sends a direct message to another user currently online
				} else if (containsUser) {
					// Do not broadcast but return command to the specified user
					incomingMessage = incomingMessage.replace("#" + privateUser, ""); // Remove the command prefix from the message
					directMessage("DM from " + incomingMessage, privateUser);
				// Send received message back to other users
				} else {
					groupMessage(incomingMessage);
				}
			} catch (IOException e) {
				closeResources(clientSocket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}

	// Method broadcasts the input message to the client of all users on the userList
	private void groupMessage(String message) {
		for (ClientThread clientThread : usersList) {
			try {
				// Exclude the sender from the group message
				if (!clientThread.userName.equals(userName)) {
					writeToBuffer(message, clientThread);
				}
			} catch (IOException e) {
				e.printStackTrace();
				closeResources(clientSocket, bufferedReader, bufferedWriter);
			}
		}
	}

	// Sends a message to only the user on the user list with the input name parameter
	private void directMessage(String message, String inputName) {
		for (ClientThread clientThread : usersList) {
			try {
				// Send the message to only the input username
				if (clientThread.userName.equals(inputName)) {
					writeToBuffer(message, clientThread);
				}
			} catch (IOException e) {
				e.printStackTrace();
				closeResources(clientSocket, bufferedReader, bufferedWriter);
			}
		}
	}
	
	// Method removes the ClientThread object from the user list
	private void removeUser() {
		usersList.remove(this); 											// Remove the current user
		groupMessage("Server: " + userName + " has left the chat.");		// Broadcast to others user has left chat room
	}
	
	// Writes the input message for the input ClientThread object (i.e. user)
	private void writeToBuffer(String message, ClientThread clientThread) throws IOException {
		clientThread.bufferedWriter.write(message);
		clientThread.bufferedWriter.newLine();
		clientThread.bufferedWriter.flush();
	}
	
	// Method closes all the IO level resources 
	private void closeResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		removeUser();														// Remove the user's ClientThread object
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
			}
			System.out.println("Closed resources for " + userName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Returns the list of current username as a string
	private String getClientList() {
		// Join each username with the specified delimiter
		StringJoiner users = new StringJoiner(", ");
		
		for (ClientThread clientThread : usersList) {
			// Exclude the sender from the group message
			if (!clientThread.userName.equals(userName))
				users.add(clientThread.userName);
		}
		return users.toString();
	}
	
	// Method checks if input username is in the username ArrayList and returns true/false
	private boolean onUserList(String inputName) {
		for (ClientThread clientThread : usersList) {
			if (clientThread.userName.equals(inputName)) {
				return true;
			} 
		}
		return false;
	}
	
	// Method checks against the current list of users and adds a numerical suffix to the username if name is not unique
	private String uniqueName(String inputName) {
		int counter = 1;
		String newName = inputName;
		while(onUserList(newName)) {
			newName = inputName + counter;
			counter++;
		}
		return newName;
	}


}