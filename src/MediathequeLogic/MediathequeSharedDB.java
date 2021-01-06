package MediathequeLogic;

import java.util.concurrent.ConcurrentHashMap;

public class MediathequeSharedDB {
	private static ConcurrentHashMap<Integer, Abonne> abonnesDB = new ConcurrentHashMap<Integer, Abonne>();
	private static ConcurrentHashMap<Integer, Document> documentsDB = new ConcurrentHashMap<Integer, Document>();
	
	public static void ajouterAbonne(Abonne a) {
		abonnesDB.put(a.numero(), a);
	}
	
	public static void ajouterDocument(Document d) {
		documentsDB.put(d.numero(), d);
	}
	
	public static Abonne getAbonne(int numero) {
		return abonnesDB.get(numero);
	}
	
	public static Document getDocument(int numero) {
		return documentsDB.get(numero);
	}
	
	public static int nbDocuments() {
		return documentsDB.size();
	}
	
	public static String listeDocuments() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < nbDocuments(); i++) {
			Document d = getDocument(i);
			String type = "???";
			if(d instanceof DVD) {
				type = "DVD  ";
			} else if(d instanceof CD) {
				type = "CD   ";
			} else if(d instanceof Livre) {
				type = "Livre";
			}
			if(d != null) sb.append(d.numero()+". "+type+" \""+d.toString()+"\"\n");
		}
		return sb.toString();
	}
}
