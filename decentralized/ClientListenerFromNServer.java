import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class ClientListenerFromNServer extends Thread{
	ObjectInputStream in;
	ConcurrentHashMap<String,ObjectOutputStream> map;  

	public ClientListenerFromNServer(ObjectInputStream in,ConcurrentHashMap<String,ObjectOutputStream> map){
		this.in=in;
		this.map=map;
	}

	public void run(){
		ObjectInputStream in_lookup=null;
		try{
			while(true){
				NPacket packetFromServer;
                packetFromServer = (NPacket) in.readObject();
                if(packetFromServer.symbol==null||packetFromServer.location==null){
                	continue;
                }
                if(map.get(packetFromServer.symbol)==null){
                	Socket socket=new Socket(packetFromServer.location.host,packetFromServer.location.port);
                	new Thread(new PeerListener(socket,map,packetFromServer.symbol,packetFromServer.location)).start();
                }
                
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}