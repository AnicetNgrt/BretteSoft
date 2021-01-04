package Serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur implements Runnable {
	
	private ServerSocket listen_socket;
	private Service serviceModele;
	
	public Serveur(int port, Service serviceModele) throws IOException {
        this.serviceModele = serviceModele;
        listen_socket = new ServerSocket(port);
    }
	
	@Override
	public void run() {
		try {
            log("Lancement du serveur au port "+this.listen_socket.getLocalPort());
            while(true) {
            	Socket socket = listen_socket.accept();
                Service service = serviceModele.getInstanceDuMemeService(socket);
                log("\u001B[33m"+"Connexion au serveur par "+socket.toString()+" sur service "+service.toString()+"\u001B[0m");
            	new Thread((Runnable) service).start();
            }
		}
        catch (IOException e) {
            try {this.listen_socket.close();} catch (IOException e1) {}
            log("Arrêt du serveur au port "+this.listen_socket.getLocalPort());
        }
    }

     // restituer les ressources --> finalize
    protected void finalize() throws Throwable {
        try {this.listen_socket.close();} catch (IOException e1) {}
    }
    
    private void log(String message) {
    	System.out.println("[Serveur: "+serviceModele.getClass().toString().substring(6)+"] "+message+"\n");
    }
}
