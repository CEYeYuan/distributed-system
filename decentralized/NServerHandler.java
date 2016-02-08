import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;

public class NServerHandler implements Runnable{
	private Socket socket = null;
	private ConcurrentHashMap<String,Location> map=null;
	private ConcurrentHashMap<String,ObjectOutputStream> out_map=null;
	public NServerHandler(Socket socket,ConcurrentHashMap<String,Location> map,ConcurrentHashMap<String,ObjectOutputStream> out_map) {
		this.map=map;
		this.socket = socket;
		this.out_map=out_map;
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

			packetFromClient = (NPacket) fromClient.readObject();
			/* create a packet to send reply back to client */
			NPacket packetToClient = new NPacket();
			NPacket myself=new NPacket();
		
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
			

			packetFromClient = (NPacket) fromClient.readObject();
			
			//sycn the global hashmap
			System.out.println("syncing "+myself.symbol+"'s local hashmap");
			for(String s:map.keySet()){
				packetToClient = new NPacket();
				packetToClient.symbol=s;
				packetToClient.location=map.get(s);
				toClient.writeObject(packetToClient);
			}
			
			System.out.println("broadcasting :"+myself.symbol+" "+myself.location.toString()+ " to "+out_map.size()+" users");
			//signal all the other user 
            for(String s:out_map.keySet()){
                ObjectOutputStream out=out_map.get(s);
                out.writeObject(packetToClient);
            }
				
			// while(true){
			// 	;
			// }
			

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