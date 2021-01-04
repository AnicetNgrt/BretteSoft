package AppClient;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class App {
	
	public static final int MODE_RESERVATION = 0;
	public static final int MODE_EMPRUNT = 1;
	public static final int MODE_RETOUR = 2;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String hostname = "localhost";
		String line = "stop";
		
		System.out.println("Client médiathèque bienvenue.\n");
		
		do {
			System.out.println("Veuillez sélectionner un mode: ");
			System.out.println("	0. Réservation");
			System.out.println("	1. Emprunt");
			System.out.println("	2. Retour");
			
			int mode = -1;
			do {
				System.out.print(">>>");
				mode = Integer.parseInt(scanner.nextLine());
				System.out.println("\n");
			} while(mode < 0 || mode > 2);
	        
			int port = 3000;
			port += mode * 1000;
			
	        try (Socket socket = new Socket(hostname, port)) {         
	            InputStream inputStream = socket.getInputStream();
	            DataInputStream dataInputStream = new DataInputStream(inputStream);

	            OutputStream outputStream = socket.getOutputStream();
	            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
	            
	            String messageBienvenue = dataInputStream.readUTF();
	            System.out.println(messageBienvenue.replace("\\n", "\n"));
	            
	            do {
	            	System.out.print(">>>");
	            	line = scanner.nextLine();
	            	if(line != null && !line.startsWith("menu") && !line.startsWith("stop")) {
	            		System.out.println("Message envoyé: "+line);
	            		dataOutputStream.writeUTF(line+"\n");
	                    dataOutputStream.flush();
	                    
	                    String message = dataInputStream.readUTF();
	                    System.out.println("Message reçu: "+message);
	            	}
	            } while(!line.startsWith("menu") && !line.startsWith("stop") && socket.isConnected());

	            System.out.println("Closing sockets.");
	 	 
	        } catch (UnknownHostException ex) {
	            System.out.println("Server not found: " + ex.getMessage());	 
	        } catch (IOException ex) {
	            System.out.println("I/O error: " + ex.getMessage());
	        }
		} while(!line.startsWith("stop"));
	}

}
