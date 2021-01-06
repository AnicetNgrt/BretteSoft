package AppClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class App {
	
	public static final int MODE_RESERVATION = 0;
	public static final int MODE_EMPRUNT = 1;
	public static final int MODE_RETOUR = 2;
	
	public static Scanner scanner;
	
	public static String hostname = "localhost";
	public static int port = 3000; 
	
	public static Socket socket;
	public static DataOutputStream out;
	public static DataInputStream in;
	
	public static boolean stopConnexion = false;
	public static boolean stopProgramme = false;
	
	public static Clip audioPlayer;
	

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		
		System.out.println("Client m�diath�que bienvenue.\n");
		
		do {
			menuSelectionService();
            connexionService(hostname, port);
            boucleRequetesService();
		} while(!stopProgramme);
	}
	
	public static void menuSelectionService() {
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
		
		port = 3000 + (mode * 1000);
	}
	
	public static void connexionService(String hostname, int port) {
		try {
			socket = new Socket(hostname, port);
			
			InputStream inputStream = socket.getInputStream();
	        in = new DataInputStream(inputStream);

	        OutputStream outputStream = socket.getOutputStream();
	        out = new DataOutputStream(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void boucleRequetesService() {
		do {
        	String message = recevoirMessage(); 
            if(message != null) {
            	g�rerMessage(message);
            }
        } while(!stopConnexion && socket.isConnected());
		stopConnexion = false;

        System.out.println("D�connexion du service.");
	}
	
	public static void g�rerMessage(String message) {
		arr�terMusique();
		if(message.startsWith("30s")) {
			jouerMusique(cheminResources() + "Beethoven - Symphony No. 9 in D minor, Op. 125 - IV. Presto - Allegro (30s excerpt).wav");
			System.out.println("Veuillez patienter quelques secondes que la r�servation expire pour ce document ...");
		} else {
			System.out.println("Message re�u: "+message);
			
			System.out.print(">>>");
        	String commande = scanner.nextLine();
        	if(commande != null) g�rerCommande(commande);
		}
	}
	
	public static void g�rerCommande(String commande) {
		if(commande.startsWith("menu")) {
			stopConnexion = true;
		} else if(commande.startsWith("stop")) {
			stopProgramme = true;
		} else {
			envoyerMessage(commande);
		}
	}
	
	public static void envoyerMessage(String message) {
		try {
			out.writeUTF(message+"\n");
			out.flush();
			System.out.println("Message envoy�: "+message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String recevoirMessage() {
		try {
			return in.readUTF().replace("\\n", "\n");
		} catch (IOException e) {
			e.printStackTrace();
			return "Erreur de lecture, pas de message.";
		}
	}
	
	public static String cheminResources() {
		File directory = new File("./");
		String compiledPath = directory.getAbsolutePath();
		return compiledPath.substring(0, compiledPath.length()-1).replace("bin", "src") + "/_resources/";
	}
	
	public static synchronized void jouerMusique(final String url) {
		new Thread(new Runnable() {
		public void run() {
			try {
				AudioInputStream input = AudioSystem.getAudioInputStream(new File(url));
				audioPlayer = AudioSystem.getClip();
				audioPlayer.open(input);
				audioPlayer.start();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		}).start();
	}
	
	public static synchronized void arr�terMusique() {
		audioPlayer.stop();
	}

}
