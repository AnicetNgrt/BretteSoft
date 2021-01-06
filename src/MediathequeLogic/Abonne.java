package MediathequeLogic;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
	private static int cpt = 0;
	
	private int numero;
	private LocalDate dateDeNaissance;
	
	public Abonne(LocalDate dateDeNaissance) {
		this.dateDeNaissance = dateDeNaissance;
		this.numero = cpt++;
	}
	
	public int numero() {
		return numero;
	}
	
	public boolean estAdulte() {
		Period period = Period.between(dateDeNaissance, LocalDate.now());
		return period.getYears() >= 18;
	}
}
