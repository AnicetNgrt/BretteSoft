package Services;


import java.io.IOException;
import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.MediathequeSharedDB;
import MediathequeLogic.ReservationException;
import Serveur.Service;
import SignauxFuméeApaches.Observable;
import SignauxFuméeApaches.Observer;

public class ServiceReservation extends MediathequeService implements Observer {
	
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
	public void onSignal(Observable origin, String message) {
		if(origin instanceof Document) {
			Document document = (Document) origin;
			if(message.startsWith("FIN_RESERVATION")) {
				try {
					origin.unsubscribe(this);
					handleRequest(document, abonneReserverBientot);
				} catch (IOException e) {
					e.printStackTrace();
				}
				abonneReserverBientot = null;
			} else if(message.startsWith("EMPRUNTE")) {
				try {
					origin.unsubscribe(this);
					sendMessageToClient("Le document "+document.toString()+" vient tout juste d'être emprunté ! Dommage...");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handleRequest(Document document, Abonne abonne) throws IOException {
		try {
			document.reservationPour(abonne);
			sendMessageToClient("Vous avez bien réservé le document "+document.toString()+".");
			log("Document \""+document.toString()+"\" reservé par l'abonné n°"+abonne.numero()+".");
		} catch (ReservationException e) {
			if(e.getMessage().startsWith("30s") && document instanceof Observable) {
				((Observable)document).subscribe(this);
				abonneReserverBientot = abonne;
			}
			if(!(document instanceof Observable)) {
				sendMessageToClient("Ce document est réservé, mais pour pas très longtemps.");
			} else {
				sendMessageToClient(e.getMessage());
			}
		}
	}
}
