package AppServeur;
import java.io.IOException;
import java.time.LocalDate;

import MediathequeLogic.*;
import Serveur.Serveur;
import Services.ServiceEmprunt;
import Services.ServiceReservation;
import Services.ServiceRetour;

public class App {
	
	private static Document[] documents = new Document[] {
		new DVD("Le vent se lève", false),
		new DVD("The Shining", true),
		new Livre("Chroniques de l'oiseau à ressort"),
		new Livre("Le meurtre du commandeur vol.1"),
		new Livre("Le grand cahier"),
		new CD("I Robot"),
		new CD("Continuo"),
		new CD("Async")
	};
	
	private static Abonne[] abonnes = new Abonne[] {
		new Abonne(LocalDate.parse("2015-12-22")),
		new Abonne(LocalDate.parse("2001-06-07")),
		new Abonne(LocalDate.parse("1935-08-02")),
	};
	
	public static void main(String[] args) {
		
		for(Document d:documents) {
			MediathequeSharedDB.ajouterDocument(d);
		}
		
		for(Abonne a:abonnes) {
			MediathequeSharedDB.ajouterAbonne(a);
		}
		
		ServiceEmprunt serviceEmpruntModele = new ServiceEmprunt(null);
		ServiceReservation serviceReservationModele = new ServiceReservation(null);
		ServiceRetour serviceRetourModele = new ServiceRetour(null);
		
		try {
			Serveur serveurReservation = new Serveur(3000, serviceReservationModele);
			Serveur serveurEmprunt = new Serveur(4000, serviceEmpruntModele);
			Serveur serveurRetour = new Serveur(5000, serviceRetourModele);
			
			new Thread(serveurReservation).start();
			new Thread(serveurEmprunt).start();
			new Thread(serveurRetour).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
