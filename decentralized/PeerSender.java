import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerSender extends Thread{

	ConcurrentHashMap<String,ObjectOutputStream> map;  
	public ClientSender(ConcurrentHashMap<String,ObjectOutputStream> map){
		this.map=map;
	}

	public void run(){
		
		/*
		read from keyboard, broadcast to all users(check if has token first)
		*/
		/*ObjectInputStream in_lookup=null;
		try{
			while(true){
				NPacket packetFromServer;
                packetFromServer = (NPacket) in.readObject();
                if(packetFromServer.symbol==null||packetFromServer.location==null){
                	continue;
                }
                if(map.get(packetFromServer.symbol)==null){
                	map.put(packetFromServer.symbol,packetFromServer.location);
                	System.out.println("local hashmap insertion: "+packetFromServer.symbol+" :"+packetFromServer.location.toString());
                }
                
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
		
	}
}