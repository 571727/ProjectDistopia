package network.client.registry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import network.adt.ClientCallbackInterface;

public class ClientCallbackImplement extends UnicastRemoteObject implements ClientCallbackInterface {

	private static final long serialVersionUID = 1L;
	private boolean notified = false;

	protected ClientCallbackImplement() throws RemoteException {
		super();
	}

	@Override
	public void notify(String result) throws RemoteException {
		System.out.println(result);
		notified = true;
	}

	@Override
	public void acknowledge(String msg) throws RemoteException {
		
		System.out.println(msg);
	}

	@Override
	public boolean isNotified() throws RemoteException {
		
		return notified;
	}

	
}
