import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class ClientListenerFromNServer extends Thread{
	Socket socket;
	ConcurrentHashMap<String,Location> map;
	public ClientListenerFromNServer(Socket socket,ConcurrentHashMap<String,Location> map){
		this.socket=socket;
		this.map=map;
	}

	public void run(){
		ObjectInputStream in_lookup=null;
		try{
			in_lookup = new ObjectInputStream(socket.getInputStream());
			while(true){
				NPacket packetFromServer;
                packetFromServer = (NPacket) in_lookup.readObject();
                map.put(packetFromServer.symbol,packetFromServer.location);
                System.out.println("added new client "+packetFromServer.symbol+" "+packetFromServer.location.toString());
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}