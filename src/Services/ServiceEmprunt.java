package Services;

import java.io.IOException;

import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.EmpruntException;
import Serveur.Service;

public class ServiceEmprunt extends MediathequeService {
	
	public static final boolean IS_SECURED = true;
	
	private static int cpt = 1;
	private final int numero;

	public ServiceEmprunt(Socket socket) {
		super(socket, IS_SECURED);
		numero = cpt++;
	}
	
	@Override
	public void onConnection() throws IOException {
		String message = "Bienvenue au service d'emprunt.\\n";
		message += "\\nInstructions: <num�ro abonn�> <identifiant document � emprunter>";
		sendMessageToClient(message);
	}

	@Override
	public String getNom() {
		return "Service Emprunt #"+numero;
	}

	@Override
	public Service getInstanceDuMemeService(Socket socket) {
		return new ServiceEmprunt(socket);
	}

	@Override
	public void handleRequest(Document document, Abonne abonne) throws IOException {
		try {
			document.empruntPar(abonne);
			sendMessageToClient("Vous avez bien emprunt� le document "+document.toString()+".");
			log("Document \""+document.toString()+"\" emprunt� par l'abonn� n�"+abonne.numero()+".");
		} catch (EmpruntException e) {
			sendMessageToClient(e.getMessage());
		}
	}
}
