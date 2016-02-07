import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;

public class NServerHandler implements Runnable{
	private Socket socket = null;
	private ConcurrentHashMap<String,Location> map=null;

	public NServerHandler(Socket socket,ConcurrentHashMap<String,Location> map) {
		this.map=map;
		this.socket = socket;
		System.out.println("Created new Thread to handle new player");
	}

	public void run() {

		boolean gotByePacket = false;
		try {

			/* stream to read from client */
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			NPacket packetFromClient;
			
			/* stream to write back to client */
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());				

			while (( packetFromClient = (NPacket) fromClient.readObject()) != null) {
				/* create a packet to send reply back to client */
				NPacket packetToClient = new NPacket();
				if(map.get(packetFromClient.symbol)==null){
					map.put(packetFromClient.symbol,packetFromClient.location);

					/* send reply back to client */
					packetToClient.symbol=packetFromClient.symbol+ " registered";
					System.out.println(packetFromClient.symbol+ " registered");
					toClient.writeObject(packetToClient);
					break;
				}
				else{
					packetToClient.symbol=packetFromClient.symbol+ " already used";
					System.out.println(packetFromClient.symbol+ " already used");
					toClient.writeObject(packetToClient);
					break;
				}
				
			}
			

			/*
			keep the connection
			fromClient.close();
			toClient.close();
			socket.close();
			*/

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
	}
}