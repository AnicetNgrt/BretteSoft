package Serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur implements Runnable {
	
	private ServerSocket listen_socket;
	private Service serviceModele;
	
	public Serveur(int port, Service serviceModele) throws IOException {
        listen_socket = new ServerSocket(port);
    }
	
	@Override
	public void run() {
		try {
            System.err.println("Lancement du serveur au port "+this.listen_socket.getLocalPort());
            while(true) {
            	Socket socket = listen_socket.accept();
            	Service service = serviceModele.getInstanceDuMemeService(socket);
            	new Thread((Runnable) service).start();
            }
		}
        catch (IOException e) {
            try {this.listen_socket.close();} catch (IOException e1) {}
            System.err.println("Arrêt du serveur au port "+this.listen_socket.getLocalPort());
        }
    }

     // restituer les ressources --> finalize
    protected void finalize() throws Throwable {
        try {this.listen_socket.close();} catch (IOException e1) {}
    }
}
