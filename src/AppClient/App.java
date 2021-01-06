package AppClient;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class App {
	
	public static final int MODE_RESERVATION = 0;
	public static final int MODE_EMPRUNT = 1;
	public static final int MODE_RETOUR = 2;
	
	public static void main(String[] args) {
		playSound("Beethoven - Symphony No. 9 in D minor, Op. 125 - IV. Presto - Allegro (30s excerpt).ogg");
		
		Scanner scanner = new Scanner(System.in);
		String hostname = "localhost";
		String line = "stop";
		
		System.out.println("Client m�diath�que bienvenue.\n");
		
		do {
			System.out.println("Veuillez s�lectionner un mode: ");
			System.out.println("	0. R�servation");
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
	            		System.out.println("Message envoy�: "+line);
	            		dataOutputStream.writeUTF(line+"\n");
	                    dataOutputStream.flush();
	                    
	                    String message = dataInputStream.readUTF();
	                    System.out.println("Message re�u: "+message);
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
	
	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
		// The wrapper thread is unnecessary, unless it blocks on the
		// Clip finishing; see comments.
		public void run() {
			try {
				Clip clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(
				App.class.getResourceAsStream(url));
				clip.open(inputStream);
				clip.start(); 
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		}).start();
	}

}
