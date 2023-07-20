package ie.gmit.dip;

import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Class with some common UI addressing input methods that are shared between the server and client applications.
 * 
 * @author PJ
 * @version 1.0
 * @since 1.0
 */
public class Addressing {
		
	/**
	 * Method prompts the user to input a port number and validates the input
	 * @param sc Scanner for accepting user input
	 * @return The port number to assign the Socket/ServerSocket
	 */
	public int getPortNumber(Scanner sc) {
		int input = -1;
		// Input must be within port number range
		while (input < 1 || input > 65535) {
			input = intPrompt("Enter your port number:", sc);
		}
		return input;
	}
	
	/**
	 * 
	 * Methods prompts user with IP address options. They can use a default 'localhost' or
	 * enter an IP address manually by entering each digit of the IP address separately.
	 * @param scanner Scanner for accepting user input
	 * @return The IP address of the server that the user want to try to connect with.
	 */
	public String getAddress(Scanner scanner) {
		int input = 0;
		String ipAdress = "localhost";
		while (input < 1 || input > 2) {
			StringJoiner address = new StringJoiner(".");
			input = intPrompt("1) Enter IP address\n2) Use 'localhost'\nPlease enter a valid input[1-2]>", scanner);
			if (input == 1) { 										
				for (int i = 1; i < 5; i++) {
					int num = -1;
					while (num < 0 || num > 255) {
						num = intPrompt("Enter IP address number " + i + " of 4 in range [0-255] digits only", scanner);
					}
					address.add(num + "");	
				}
				ipAdress = address.toString();
			}
		}
		System.out.println("IP address set to: " + ipAdress);
		return ipAdress;
	}
	
	// Method prompts the user with an input message and validates the input to be an integer 
	private static int intPrompt(String subM, Scanner scanner) {	
		String nums = "^(-?[1-9]?[0-9]?[0-9]?[0-9]?[0-9])$"; 	// Regex for numbers between 0-99999
		System.out.println(subM);
		
		// Check if input was blank
		String input = scanner.nextLine();
		if (input.isEmpty()) {
			System.out.print("Blank entry. ");
			return -1; 											// Used as a invalid trigger
		}
		
		// Check input integer is within a range (i.e. nums: 0-10000)
		if (input.matches(nums)) {
			return Integer.parseInt(input); 					// Converts input to a integer
		} else { 
			// Else block catches strings and integers outside of regex nums's range
			System.out.print("Invalid input! ");
			return -1;											// Used as a invalid trigger
		}
	}

}
