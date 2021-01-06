package MediathequeLogic;
public class Livre extends DocumentImpl {

	public Livre(String titre) {
		super(titre);
	}

	@Override
	public String nomAmérindien() {
		return "Vautour Savant";
	}
}
