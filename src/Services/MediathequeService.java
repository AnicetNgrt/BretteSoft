package Services;

import java.net.Socket;

import MediathequeLogic.Abonne;
import MediathequeLogic.Document;
import MediathequeLogic.MediathequeSharedDB;
import Serveur.Service;

public abstract class MediathequeService extends Service {
	
	protected boolean secured;
	
	public MediathequeService(Socket socket, boolean secured) {
		super(socket);
		this.secured = secured;
	}
	
	protected MediathequeParseResult parseMessage(String message) throws MediathequeCommandParsingException {
		String[] parts = message.trim().split("\\s+");
		if((secured && parts.length < 2) || (!secured && parts.length < 1)) {
			throw new MediathequeCommandParsingException("ERREUR: Commande erronnée, il manque des arguments.");
		}
		
		Abonne abonne = null;
		if(secured) {
			int idAbonne = -1;
			try {
				idAbonne = Integer.parseInt(parts[0]);
			} catch(NumberFormatException e) {
				throw new MediathequeCommandParsingException("ERREUR: Le numéro d'abonné n'est pas valide.");
			}
			abonne = MediathequeSharedDB.getAbonne(idAbonne);
			if(abonne == null) {
				throw new MediathequeCommandParsingException("ERREUR: Le numéro d'abonné ne corespond à aucun abonné.");
			}
		}
		
		int idDoc = -1;
		try {
			idDoc = Integer.parseInt(parts[parts.length-1]);
		} catch(NumberFormatException e) {
			throw new MediathequeCommandParsingException("ERREUR: Le numéro de document n'est pas valide.");
		}
		Document document = MediathequeSharedDB.getDocument(idDoc);
		if(document == null) {
			throw new MediathequeCommandParsingException("ERREUR: Le numéro de document ne correspond à aucun document");
		}
		
		return new MediathequeParseResult(abonne, document);
	}
}
