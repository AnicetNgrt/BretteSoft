package MediathequeLogic;

import java.time.Duration;
import java.time.LocalDate;

public class Abonne {
	private static int cpt = 0;
	
	private LocalDate dateDeNaissance;
	private int numero;
	private String email;
	
	public Abonne(LocalDate dateDeNaissance) {
		this.dateDeNaissance = dateDeNaissance;
		this.numero = cpt++;
	}
	
	public boolean estAdulte() {
		Duration duration = Duration.between(dateDeNaissance, LocalDate.now());
		return duration.toDays() >= 365.25 * 16;
	}
}
