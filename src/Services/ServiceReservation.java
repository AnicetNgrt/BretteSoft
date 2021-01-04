package Services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.EmpruntException;
import MediathequeLogic.MediathequeSharedDB;
import MediathequeLogic.ReservationException;
import Serveur.Service;

public class ServiceReservation extends MediathequeService {
	
	public static final boolean IS_SECURED = true;
	
	private static int cpt = 1;
    private final int numero;
	
	public ServiceReservation(Socket socket) {
		super(socket, IS_SECURED);
		numero = cpt++;
	}
	
	@Override
	public void onConnection() throws IOException {
		String listeDocuments = "	"+MediathequeSharedDB.listeDocuments().replace("\n", "\\n	");
		String message = "Bienvenue au service de réservation.\\n";
		message += "Liste des documents: \\n\\n";
		message += listeDocuments;
		message += "\\nInstructions: <numéro abonné> <identifiant document à réserver>";
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
			document.reservationPour(abonne);
			sendMessageToClient("Vous avez bien réservé le document "+document.toString()+".");
		} catch (ReservationException e) {
			sendMessageToClient(e.getMessage());
		}
	}

	@Override
	public String getNom() {
		return "Service Reservation #"+numero;
	}

	@Override
	public Service getInstanceDuMemeService(Socket socket) {
		return new ServiceReservation(socket);
	}

}
