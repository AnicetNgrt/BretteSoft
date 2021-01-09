package MediathequeLogic;

import java.time.LocalDateTime;

import SignauxFuméeApaches.Observable;
import SignauxFuméeApaches.Observer;

public abstract class DocumentImpl extends Observable implements Document, Observer {
	
	public static final long TEMPS_MAX_RESERV = 35 * 1000; //3600 * 2 * 1000; 
	public static final long TEMPS_RESERV_FAIBLE = 30 * 1000; 
	private static int numeroMin = 0;
	
	private int numero;
	private String titre;
	private long tsReservationMs;
	private Abonne reserve;
	private VerificateurFinReservation verificateur = null;
	private boolean enMediatheque;
	
	public DocumentImpl(String titre) {
		this.numero = DocumentImpl.numeroMin++;
		this.titre = titre;
		this.reserve = null;
		this.enMediatheque = true;
	}
	
	@Override
	public int numero() {
		return numero;
	}

	@Override
	public synchronized void reservationPour(Abonne ab) throws ReservationException {
		if(!estDisponible()) {
			String message = "Document indispible";
			if(this.reserve != null) {
				if(tempsRestantReservMs() < TEMPS_RESERV_FAIBLE) {
					message = "30s";
					throw new ReservationException(message);
				} else {
					message = "Document reservé jusqu'à "+heureFinReservation()+".";
				}
			} else if(!this.enMediatheque) {
				message = "Document déjà emprunté.";
			}
			throw new ReservationException(message);
		}
		
		this.tsReservationMs = System.currentTimeMillis();
		this.reserve = ab;
		
		lancerVerificateurFinReservation();
	}
	
	private void lancerVerificateurFinReservation() {
		verificateur = new VerificateurFinReservation(TEMPS_MAX_RESERV, tsReservationMs);
		verificateur.subscribe(this);
		new Thread(verificateur).start();
	}
	
	@Override
	public void onSignal(Observable origin, String message) {
		if(origin instanceof VerificateurFinReservation && message.startsWith("FIN_RESERVATION")) {
			verificateur = null;
			stopperReservation();
			sendSignal("FIN_RESERVATION");
		}
	}
	
	private void stopperReservation() {
		if(verificateur != null) {
			verificateur.stopper();
			verificateur = null;
		}
		reserve = null;
	}
	
	private long tempsRestantReservMs() {
		if(this.reserve == null) {
			return 0;
		}
		long tempsEcouleMs = System.currentTimeMillis() - tsReservationMs;
		return TEMPS_MAX_RESERV - tempsEcouleMs;
	}
	
	private String heureFinReservation() {
		long tempsRestantMs = tempsRestantReservMs();
		int tempsRestantMinutes = (int) (tempsRestantMs / 60000);
		int tempsRestantHeures = (int) (tempsRestantMinutes / 60);
		tempsRestantMinutes -= tempsRestantHeures * 60;
		
		int heureMaintenant = LocalDateTime.now().getHour();
		int minutesMaintenant = LocalDateTime.now().getMinute();
		
		int minutesFin = (minutesMaintenant + tempsRestantMinutes);
		int heureFin = (heureMaintenant + tempsRestantHeures) % 24;
		if (minutesFin >= 60) {
			heureFin++;
			minutesFin %= 60;
		}
		return heureFin+"h"+minutesFin;
	}

	@Override
	public synchronized void empruntPar(Abonne ab) throws EmpruntException {
		if(!estDisponible()) {
			if(this.reserve != ab) {
				throw new EmpruntException("Ce document est déjà réservé par quelqu'un d'autre.");
			} else if(!this.enMediatheque) {
				throw new EmpruntException("Ce document n'est pas disponible pour le moment. Renvoyez \"\"");
			}
		} else if(this.reserve == null) {
			String message = "Vous n'avez pas encore reservé ce document, mais il est";
			message += " encore disponible à la réservation.";
			throw new EmpruntException(message);
		}
		
		sendSignal("EMPRUNTE");
		stopperReservation();
		this.enMediatheque = false;
	}
	
	private boolean estDisponible() {
		return this.reserve == null && this.enMediatheque;
	}

	@Override
	public synchronized void retour() {
		assert(!estDisponible());
		
		this.reserve = null;
		this.enMediatheque = true;
	}
	
	public String toString() {
		return titre;
	}
}
