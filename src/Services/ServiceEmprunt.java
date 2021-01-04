package Services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.EmpruntException;
import MediathequeLogic.MediathequeSharedDB;
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
		String listeDocuments = "	"+MediathequeSharedDB.listeDocuments().replace("\n", "\\n	");
		String message = "Bienvenue au service d'emprunt.\\n";
		message += "\\nInstructions: <numéro abonné> <identifiant document à emprunter>";
		sendMessageToClient(message);
	}

	@Override
	public void onMessage(String message) throws IOException {
		Abonne abonne;
		Document document;
		try {
			MediathequeParseResult result = parseMessage(message);
			abonne = result.abonne();
			document = result.document();
		} catch (MediathequeCommandParsingException e) {
			sendMessageToClient(e.getMessage());
			return;
		}
		
		try {
			document.empruntPar(abonne);
			sendMessageToClient("Vous avez bien emprunté le document "+document.toString()+".");
		} catch (EmpruntException e) {
			sendMessageToClient(e.getMessage());
		}
	}

	@Override
	public String getNom() {
		return "Service Emprunt #"+numero;
	}

	@Override
	public Service getInstanceDuMemeService(Socket socket) {
		return new ServiceEmprunt(socket);
	}
}
