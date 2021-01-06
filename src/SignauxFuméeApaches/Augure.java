package SignauxFum�eApaches;

import java.util.concurrent.LinkedBlockingQueue;

// Observable du grand pattern observer apache et thread safe
public abstract class Augure implements Apache {
	
	private LinkedBlockingQueue<Chaman> �tresSensiblesAuxSignaux = new LinkedBlockingQueue<Chaman>();
	
	public void gagnerAdepte(Chaman �tre) {
		�tresSensiblesAuxSignaux.add(�tre);
	}
	
	public void perdreAdepte(Chaman �tre) {
		�tresSensiblesAuxSignaux.remove(�tre);
	}
	
	protected void envoyerSignal(String message) {
		LinkedBlockingQueue<Chaman> �tresCopi�s = new LinkedBlockingQueue<Chaman>(�tresSensiblesAuxSignaux);
		for(Chaman �tre: �tresCopi�s) {
			�tre.quandUnSignalSeD�voile(this, message);
		}
	}
	
	public String nomAm�rindien() {
		return "Sitting Bull";
	}
}
