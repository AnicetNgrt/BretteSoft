package Services;


import java.io.IOException;
import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.MediathequeSharedDB;
import MediathequeLogic.ReservationException;
import Serveur.Service;
import SignauxFum�eApaches.Observable;
import SignauxFum�eApaches.Observer;

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
		String message = "Bienvenue au service de r�servation.\\n";
		message += "Liste des documents: \\n\\n";
		message += listeDocuments;
		message += "\\nInstructions: <num�ro abonn�> <identifiant document � r�server>";
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
	public String nomAm�rindien() {
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
					sendMessageToClient("Le document "+document.toString()+" vient tout juste d'�tre emprunt� ! Dommage...");
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
			sendMessageToClient("Vous avez bien r�serv� le document "+document.toString()+".");
			log("Document \""+document.toString()+"\" reserv� par l'abonn� n�"+abonne.numero()+".");
		} catch (ReservationException e) {
			if(e.getMessage().startsWith("30s") && document instanceof Observable) {
				((Observable)document).subscribe(this);
				abonneReserverBientot = abonne;
			}
			if(!(document instanceof Observable)) {
				sendMessageToClient("Ce document est r�serv�, mais pour pas tr�s longtemps.");
			} else {
				sendMessageToClient(e.getMessage());
			}
		}
	}
}
