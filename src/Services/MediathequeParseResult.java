package Services;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;

public class MediathequeParseResult {
	private Abonne abonne;
	private Document document;
	
	public MediathequeParseResult(Abonne abonne, Document document) {
		this.abonne = abonne;
		this.document = document;
	}

	public Abonne abonne() {
		return abonne;
	}

	public Document document() {
		return document;
	}
}
