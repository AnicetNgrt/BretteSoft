package MediathequeLogic;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class DocumentImpl implements Document {
	
	public static final long TEMPS_MAX_RESERV = 3600 * 2 * 1000; 
	private static int numeroMin = 0;
	
	private int numero;
	private String titre;
	private long tsReservationMs;
	private Abonne reserve;
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
				message = "Document reservé jusqu'à "+heureFinReservation()+".";
			} else if(!this.enMediatheque) {
				message = "Document déjà emprunté.";
			}
			throw new ReservationException(message);
		}
		
		this.tsReservationMs = System.currentTimeMillis();
		this.reserve = ab;
		
		Thread t = new Thread() {
			public void run() {
				while(System.currentTimeMillis() - tsReservationMs < TEMPS_MAX_RESERV);
				reserve = null;
			}
	    };
	    t.start();
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
			String message = "Document indisponible";
			if(this.reserve != ab) {
				message = "Document reservé jusqu'à "+heureFinReservation()+".";
			} else if(!this.enMediatheque) {
				message = "Document déjà emprunté.";
			}
			throw new EmpruntException(message);
		} else if(this.reserve == null) {
			String message = "Vous n'avez pas encore reservé ce document, mais il semble disponible.";
			throw new EmpruntException(message);
		}
		
		this.reserve = null;
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
