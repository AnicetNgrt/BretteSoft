package Services;

import java.io.IOException;
import java.net.Socket;

import MediathequeLogic.Document;
import Serveur.Service;

public class ServiceRetour extends MediathequeService {
	
	public static final boolean IS_SECURED = false;
	
	private static int cpt = 1;
    private final int numero;
	
	public ServiceRetour(Socket socket) {
		super(socket, IS_SECURED);
		numero = cpt++;
	}
	
	@Override
	public void onConnection() throws IOException {
		String message = "Bienvenue au service de retour.\\n";
		message += "\\nInstructions: <identifiant document à retourner>";
		sendMessageToClient(message);
	}

	@Override
	public void onMessage(String message) throws IOException  {
		Document document;
		try {
			MediathequeParseResult result = parseMessage(message);
			document = result.document();
		} catch (MediathequeCommandParsingException e) {
			sendMessageToClient(e.getMessage());
			return;
		}
		
		document.retour();
		sendMessageToClient("Document "+document.toString()+" retourné avec succès.");
		log("Document \""+document.toString()+"\" rendu.");
	}

	@Override
	public String getNom() {
		return "Service Retour #"+numero;
	}

	@Override
	public Service getInstanceDuMemeService(Socket socket) {
		return new ServiceRetour(socket);
	}

}
