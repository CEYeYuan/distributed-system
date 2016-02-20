import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class NServerHandler implements Runnable{
	private Socket socket = null;
	private ConcurrentHashMap<String,Location> map=null;
	private int index;
	private ConcurrentHashMap<String,ObjectOutputStream> out_map=null;
	public NServerHandler(Socket socket,ConcurrentHashMap<String,Location> map,ConcurrentHashMap<String,ObjectOutputStream> out_map,int index) {
		this.map=map;
		this.socket = socket;
		this.out_map=out_map;
		this.index=index;
		System.out.println("Created new Thread to handle new player");
	}

	public void  run() {

		boolean gotByePacket = false;
		try {

				/* stream to read from client */
				ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
				MPacket packetFromClient;
				
				/* stream to write back to client */
				ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());				

				packetFromClient = (MPacket) fromClient.readObject();
				/* create a packet to send reply back to client */
				MPacket packetToClient = new MPacket();
				MPacket myself=new MPacket();
			
				if(map.get(packetFromClient.symbol)==null){
					map.put(packetFromClient.symbol,packetFromClient.location);
				    out_map.put(packetFromClient.symbol,toClient);

					/* send reply back to client */
					packetToClient.symbol=packetFromClient.symbol+ " "+map.get(packetFromClient.symbol).toString()+" registered ";
					System.out.println(packetFromClient.symbol+ map.get(packetFromClient.symbol).toString());
					toClient.writeObject(packetToClient);
					myself.symbol=packetFromClient.symbol;
					myself.location=packetFromClient.location;
				}
				else{
					packetToClient.symbol=packetFromClient.symbol+ " already used";
					System.out.println(packetFromClient.symbol+ " already used");
					toClient.writeObject(packetToClient);
					return;//exit current thread
				}
				

		
				
				//sync the global hashmap
				System.out.println("syncing "+myself.symbol+"'s local hashmap");
				for(String s:map.keySet()){
					packetToClient = new MPacket();
					packetToClient.symbol=s;
					packetToClient.location=map.get(s);
					toClient.writeObject(packetToClient);
				}
				
				packetToClient = new MPacket();
				if(index==1){
					//first user
					packetToClient.first = 1;
					System.out.println("Value is xxxxxxxxxxxxxxxxxxxxxxxxxxxxx: " + packetToClient.first);
					//packetToClient.first = 1;
					packetToClient.type=MPacket.TOKEN ;
					packetToClient.sequenceNumber=0;
					toClient.writeObject(packetToClient);
					
				}
				else{
					packetToClient.type=MPacket.PACKET_NS_DONE;	
					toClient.writeObject(packetToClient);
					
				}
					
				
//				if(index==1){
//					//first user -> token
//					packetToClient = new MPacket();
//					packetToClient.type=MPacket.TOKEN;
//					packetToClient.sequenceNumber=0;
//					toClient.writeObject(packetToClient);
//					
//					//first user -> bullets
////	                MPacket packetMissile = new MPacket();
////	                packetMissile.type = MPacket.BULLET;
////	                toClient.writeObject(packetMissile);
//				}
//				else{
//					//packetToClient.first = 1;
//					packetToClient.type=MPacket.PACKET_NS_DONE;	
//					toClient.writeObject(packetToClient);	
//				}
					
			

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