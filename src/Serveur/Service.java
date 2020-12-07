package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Service {

    private final Socket client;
    
    public Service(Socket socket) {
        this.client = socket;
    }

    public void run() {
    	if(client != null) {
    		try {
                BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
                PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
                String line = in.readLine();
                onMessage(line, out);
            }
            catch (IOException e) {
            }
    	}
        try {client.close();} catch (IOException e2) {}
    }
    
    public abstract void onMessage(String message, PrintWriter out);
    
    public abstract String getNom();
    
    // Permet au serveur de récupérer une nouvelle instance d'un service sans avoir à se soucier du type
    // de service qu'il est en train de gérer.
    public abstract Service getInstanceDuMemeService(Socket socket);
    
    protected void finalize() throws Throwable {
         client.close(); 
    }
}
