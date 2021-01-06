package SignauxFuméeApaches;

import java.util.concurrent.LinkedBlockingQueue;

// Observable du grand pattern observer apache et thread safe
public abstract class Augure implements Apache {
	
	private LinkedBlockingQueue<Chaman> êtresSensiblesAuxSignaux = new LinkedBlockingQueue<Chaman>();
	
	public void gagnerAdepte(Chaman être) {
		êtresSensiblesAuxSignaux.add(être);
	}
	
	public void perdreAdepte(Chaman être) {
		êtresSensiblesAuxSignaux.remove(être);
	}
	
	protected void envoyerSignal(String message) {
		LinkedBlockingQueue<Chaman> êtresCopiés = new LinkedBlockingQueue<Chaman>(êtresSensiblesAuxSignaux);
		for(Chaman être: êtresCopiés) {
			être.quandUnSignalSeDévoile(this, message);
		}
	}
	
	public String nomAmérindien() {
		return "Sitting Bull";
	}
}
