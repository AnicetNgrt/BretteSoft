package Serveur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Service implements Runnable {

    private final Socket client;
    private DataOutputStream out = null;
    
    public Service(Socket socket) {
        this.client = socket;
    }

    public void run() {
    	if(client != null) {
    		try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                OutputStream outputStream = client.getOutputStream();
                out = new DataOutputStream(outputStream);
                
                onConnection();
                
                String line = "stop";
                
                do {
                	line = in.readLine();
                	if(line != null) {
                		if(line.length() > 2) line = line.substring(2);
                		logMessage(line);
                        onMessage(line);
                	}
                } while(line != "stop" && client.isConnected());

                log("Closing socket and terminating program.");
                client.close();
            }
            catch(IOException e) {

            }
    	}
    }
    
    public abstract void onConnection() throws IOException;
    
    public abstract void onMessage(String message) throws IOException;
    
    public abstract String getNom();
    
    // Permet au serveur de récupérer une nouvelle instance d'un service sans avoir à se soucier du type
    // de service qu'il est en train de gérer.
    public abstract Service getInstanceDuMemeService(Socket socket);
    
    protected void sendMessageToClient(String message) throws IOException {
    	out.writeUTF(message);
    	out.flush();
    }
    
    protected void finalize() throws Throwable {
         client.close(); 
    }
    
    public void log(String message) {
    	System.out.println("\u001B[34m"+"["+this.toString()+"] "+"\u001B[0m"+message+"\n");
    }
    
    public void logMessage(String message) {
    	log("\u001B[35m"+"Message re�u: "+message+"\u001B[0m");
    }
}
