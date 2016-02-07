import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class ClientListenerFromNServer extends Thread{
	ObjectInputStream in;
	ConcurrentHashMap<String,Location> map;
	public ClientListenerFromNServer(ObjectInputStream in,ConcurrentHashMap<String,Location> map){
		this.in=in;
		this.map=map;
	}

	public void run(){
		ObjectInputStream in_lookup=null;
		try{
			while(true){
				NPacket packetFromServer;
                packetFromServer = (NPacket) in.readObject();
                if(packetFromServer.symbol.indexOf("registered")!=-1)
                	continue;

                map.put(packetFromServer.symbol,packetFromServer.location);
                System.out.println("added new client "+packetFromServer.symbol);
            	System.out.println(packetFromServer.location.toString());
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}