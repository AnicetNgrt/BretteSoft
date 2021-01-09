package SignauxFuméeApaches;

//Observer du grand pattern observer apache et thread safe
public interface Observer extends Apache {
	public void onSignal(Observable origin, String message);
}
