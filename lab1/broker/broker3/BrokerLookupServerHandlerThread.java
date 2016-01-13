import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;

public class BrokerLookupServerHandlerThread implements Runnable{
	private Socket socket = null;
	private ConcurrentHashMap<String, ArrayList<BrokerLocation>> map=null;

	public BrokerLookupServerHandlerThread(Socket socket,ConcurrentHashMap<String,ArrayList<BrokerLocation>> map) {
		this.map=map;
		this.socket = socket;
		System.out.println("Created new Thread to handle server lookup/register");
	}

	public void run() {

		boolean gotByePacket = false;
		try {

			/* stream to read from client */
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			BrokerPacket packetFromClient;
			
			/* stream to write back to client */
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());				

			while (( packetFromClient = (BrokerPacket) fromClient.readObject()) != null) {
				/* create a packet to send reply back to client */
				BrokerPacket packetToClient = new BrokerPacket();

				if(packetFromClient.type == BrokerPacket.LOOKUP_REGISTER){
					ArrayList list=null;
					if(map.get(packetFromClient.symbol)==null){
						list=new ArrayList<BrokerLocation>();
						map.put(packetFromClient.symbol,list);
					}
					else
						list=map.get(packetFromClient.symbol);
					for(int i=0;i<packetFromClient.locations.length;i++)
						list.add(packetFromClient.locations[i]);
					
					/* send reply back to client */
					packetToClient.type=BrokerPacket.LOOKUP_REPLY;
					packetToClient.symbol=packetFromClient.symbol+ " registered";
					System.out.println(packetFromClient.symbol+ " registered");
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}

				if(packetFromClient.type==BrokerPacket.LOOKUP_REQUEST){
					String str=packetFromClient.symbol;
					System.out.println(str);
					if(map.get(str)==null){
						packetToClient.symbol=packetFromClient.symbol+ " not found, try again";
						System.out.println(packetFromClient.symbol+ " not found, try again");
					}
					else{
						ArrayList<BrokerLocation> list=map.get(packetFromClient.symbol);
						Random rg = new Random();
						packetToClient.symbol=list.get(rg.nextInt(list.size())).toString();
						System.out.println(packetFromClient.symbol+" found: "+packetToClient.symbol);
					}

					/* send reply back to client */
					packetToClient.type=BrokerPacket.LOOKUP_REPLY;
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}		
			}
			
			
			fromClient.close();
			toClient.close();
			socket.close();

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
	}
}
