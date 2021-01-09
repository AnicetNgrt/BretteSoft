package SignauxFuméeApaches;

import java.util.concurrent.LinkedBlockingQueue;

// Apache et thread safe !!!
public abstract class Observable implements Apache {
	
	private LinkedBlockingQueue<Observer> subscribers = new LinkedBlockingQueue<Observer>();
	
	public void subscribe(Observer observer) {
		subscribers.add(observer);
	}
	
	public void unsubscribe(Observer observer) {
		subscribers.remove(observer);
	}
	
	protected void sendSignal(String message) {
		LinkedBlockingQueue<Observer> subscribersCopy = new LinkedBlockingQueue<Observer>(subscribers);
		for(Observer subscriber: subscribersCopy) {
			subscriber.onSignal(this, message);
		}
	}
	
	public String nomAmérindien() {
		return "Sitting Bull";
	}
}
