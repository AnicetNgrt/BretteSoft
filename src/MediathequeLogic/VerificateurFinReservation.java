package MediathequeLogic;

import SignauxFuméeApaches.Observable;

public class VerificateurFinReservation extends Observable implements Runnable {
	private boolean stopped = false;
	
	private final long tempsMaxReservMs;
	private final long tsReservationMs;
	
	public VerificateurFinReservation(long tempsMaxReservMs, long tsReservationMs) {
		this.tempsMaxReservMs = tempsMaxReservMs;
		this.tsReservationMs = tsReservationMs;
	}
	
	public void run() {
		while(!stopped && System.currentTimeMillis() - tsReservationMs < tempsMaxReservMs);
		if(!stopped) {
			loopWait();
			sendSignal("FIN_RESERVATION");
		}
	}
	
	synchronized public void loopWait() {
		try {
			wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	synchronized public void stopper() {
		stopped = true;
	}
}
