package Services;

import java.io.PrintWriter;
import java.net.Socket;

import Serveur.Service;

public class ServiceReservation extends Service {

	private static int cpt = 1;
    private final int numero;
	
	public ServiceReservation(Socket socket) {
		super(socket);
		numero = cpt++;
	}

	@Override
	public void onMessage(String message, PrintWriter out) {
		
	}

	@Override
	public String getNom() {
		return "Service Reservation #"+numero;
	}

	@Override
	public Service getInstanceDuMemeService(Socket socket) {
		return new ServiceReservation(socket);
	}

}
