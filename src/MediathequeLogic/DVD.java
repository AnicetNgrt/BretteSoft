package MediathequeLogic;

public class DVD extends DocumentImpl {
	private boolean pourAdultes;
	
	public DVD(String titre, boolean pourAdultes) {
		super(titre);
		this.pourAdultes = pourAdultes;
	}
	
	@Override
	public synchronized void reservationPour(Abonne ab) throws ReservationException {
		if(pourAdultes && !ab.estAdulte()) {
			throw new ReservationException("Tu n'as pas l'age requis pour consulter ce document.");
		}
		
		try {
			super.reservationPour(ab);
		} catch(Exception e) {
			throw e;
		}
	}
	
	@Override
	public synchronized void empruntPar(Abonne ab) throws EmpruntException {
		if(pourAdultes && !ab.estAdulte()) {
			throw new EmpruntException("Tu n'as pas l'age requis pour consulter ce document.");
		}
		
		try {
			super.empruntPar(ab);
		} catch(Exception e) {
			throw e;
		}
	}

	@Override
	public String nomAmérindien() {
		return "Bol Plat Brillant";
	}
}
