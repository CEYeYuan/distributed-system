import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class ClientListenerFromNServer extends Thread{
	ObjectInputStream in;
	ConcurrentHashMap<String,ObjectOutputStream> map;  
	String my_name;
	Location my_location;
	public ClientListenerFromNServer(ObjectInputStream in,ConcurrentHashMap<String,ObjectOutputStream> map,String my_name,Location my_location){
		this.in=in;
		this.map=map;
		this.my_name=my_name;
		this.my_location=my_location;
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
                	map.put(packetFromServer.symbol,packetFromServer.location);
                	Socket socket=new Socket(packetFromServer.location.host,packetFromServer.location.port);
                	new Thread(new PeerListener(socket,out_map)).start();
                }
                
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}