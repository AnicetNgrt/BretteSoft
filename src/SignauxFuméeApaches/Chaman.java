package SignauxFuméeApaches;

//Observer du grand pattern observer apache et thread safe
public interface Chaman extends Apache {
	public void quandUnSignalSeDévoile(String message);
}
