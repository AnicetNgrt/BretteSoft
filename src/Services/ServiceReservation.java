package Services;


import java.io.IOException;
import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.MediathequeSharedDB;
import MediathequeLogic.ReservationException;
import Serveur.Service;
import SignauxFuméeApaches.Augure;
import SignauxFuméeApaches.Chaman;

public class ServiceReservation extends MediathequeService implements Chaman {
	
	public static final boolean IS_SECURED = true;
	
	private static int cpt = 1;
    private final int numero;
    
    private Abonne abonneReserverBientot = null;
	
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
			String errorMessage = e.getMessage();
			sendMessageToClient(errorMessage);
			return;
		}
		
		essayerReservation(document, abonne);
	}
	
	private void essayerReservation(Document document, Abonne abonne) throws IOException {
		try {
			document.reservationPour(abonne);
			sendMessageToClient("Vous avez bien réservé le document "+document.toString()+".");
			log("Document \""+document.toString()+"\" reservé par l'abonné n°"+abonne.numero()+".");
		} catch (ReservationException e) {
			if(e.getMessage().startsWith("30s") && document instanceof Augure) {
				((Augure)document).gagnerAdepte(this);
				abonneReserverBientot = abonne;
			}
			if(!(document instanceof Augure)) {
				sendMessageToClient("Ce document est réservé, mais pour pas très longtemps.");
			} else {
				sendMessageToClient(e.getMessage());
			}
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

	@Override
	public String nomAmérindien() {
		return "Petit Nuage";
	}

	@Override
	public void quandUnSignalSeDévoile(Augure augure, String message) {
		if(augure instanceof Document) {
			Document document = (Document) augure;
			if(message.startsWith("FIN_RESERVATION")) {
				try {
					augure.perdreAdepte(this);
					essayerReservation(document, abonneReserverBientot);
				} catch (IOException e) {
					e.printStackTrace();
				}
				abonneReserverBientot = null;
			} else if(message.startsWith("EMPRUNTE")) {
				try {
					augure.perdreAdepte(this);
					sendMessageToClient("Le document "+document.toString()+" vient tout juste d'être emprunté ! Dommage...");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
