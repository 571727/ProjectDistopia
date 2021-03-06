package network.server.registry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map.Entry;

import adt.Unit;
import elem.Tile;
import elem.User;
import game.scenes.world.World;
import network.adt.ClientCallbackInterface;
import network.adt.ServerCallbackInterface;

public class ServerCallbackImplement extends UnicastRemoteObject implements ServerCallbackInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ClientCallbackInterface clientcallbackobj;

	// Integer: UserID, Stack of units to replace (because of new stats)
	private HashMap<Integer, User> users;
	private HashMap<Integer, Stack<Unit>> unitUpdate;
	// Integer: UserID, Stack of units to replace (because of new stats)
	private HashMap<Integer, Stack<Tile>> tileUpdate;
	private World world;

	protected ServerCallbackImplement(World world) throws RemoteException {
		super();

		this.world = world;

	}

	@Override
	public void registerClientCallbackObject(ClientCallbackInterface clientcallbackobj) throws RemoteException {

		this.clientcallbackobj = clientcallbackobj;

		// acknowledge client message
		clientcallbackobj.acknowledge("From Server: Message recieved!"); // send a message back to the client

	}

	@Override
	public Tile attack(Unit attacker, Tile from, Tile dest) throws RemoteException {
		// TODO
		clientcallbackobj.acknowledge("From Server: Attack");
		return dest;
	}

	@Override
	public Tile move(Unit unit, Tile from, Tile dest) throws RemoteException {
		from.getObjects().clear();

		if (dest.getObjects().size() > 0)
			dest.getObjects().set(0, unit);
		else
			dest.getObjects().add(unit);
		
		dest.setFaction(unit.getFaction());

		for (Entry<Integer, User> entry : users.entrySet()) {
			tileUpdate.get(entry.getKey()).push(from);
			tileUpdate.get(entry.getKey()).push(dest);
		}

		return dest;
	}

	@Override
	public void createBuilding(Tile where) throws RemoteException {
		// TODO
		clientcallbackobj.acknowledge("From Server: Message recieved!");
	}

	@Override
	public Stack<Tile> getTileUpdates(Integer userID) throws RemoteException {
		@SuppressWarnings("unchecked")
		Stack<Tile> res = (Stack<Tile>) tileUpdate.get(userID).clone();
		tileUpdate.get(userID).clear();
		return res;
	}

	/**
	 * Used when joining a game midgame or having just started up a savegame
	 */
	@Override
	public World getAllWorldInfo() throws RemoteException {
		return world;
	}

	@Override
	public void createUnit(Unit unit, Tile where) throws RemoteException {

		if (where.getObjects().size() > 0)
			where.getObjects().set(0, unit);
		else
			where.getObjects().add(unit);

		for (Entry<Integer, User> entry : users.entrySet()) {
			tileUpdate.get(entry.getKey()).push(where);
		}
	}

	public void startUp(HashMap<Integer, User> users) {
		this.users = users;

		unitUpdate = new HashMap<Integer, Stack<Unit>>();
		tileUpdate = new HashMap<Integer, Stack<Tile>>();

		for (Entry<Integer, User> entry : users.entrySet()) {
			unitUpdate.put(entry.getKey(), new Stack<Unit>());
			tileUpdate.put(entry.getKey(), new Stack<Tile>());
		}
	}

	@Override
	public int getTurn() throws RemoteException {
		return world.getTurn();
	}

}
