package MediathequeLogic;
public class Livre extends DocumentImpl {

	public Livre(String titre) {
		super(titre);
	}

	@Override
	public String nomAm�rindien() {
		return "Vautour Savant";
	}
}
