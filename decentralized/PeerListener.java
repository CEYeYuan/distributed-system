import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerListener extends Thread{
	Socket socket;
	ConcurrentHashMap<String,ObjectOutputStream> map;  
	Location peer_location;
	String peer_name;
	private String myself;
	private Location mylocation;
	
	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map,String myself,Location mylocation){
		//server as sever
		this.socket=socket;
		this.map=map;
		this.myself=myself;
		this.mylocation=mylocation;
	}


	public PeerListener(ConcurrentHashMap<String,ObjectOutputStream> map,String peer_name,Location peer_location,String myself,Location mylocation){
		//learn from name service
		try{
			socket=new Socket(peer_location.host,peer_location.port);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		this.map=map;
		this.peer_name=peer_name;
		this.peer_location=peer_location;
		this.myself=myself;
		this.mylocation=mylocation;
	}
	public void run(){
		try{

			assert(socket!=null);

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if(peer_name!=null&&peer_location!=null){
				//learn directly from naming service, introduce my self
				map.put(peer_name,out);
				NPacket packetToPeer = new NPacket();  
		        packetToPeer.location=mylocation;
		        packetToPeer.sender =myself;
		        packetToPeer.type=NPacket.PACKET_INTRO;
		        out.writeObject(packetToPeer);
		        out.flush();
		      	System.out.println("connection to "+peer_name+peer_location.toString()+" added");	           
			}

			ObjectInputStream in= new ObjectInputStream(socket.getInputStream());
		
	
						
			while(true){
				NPacket packetFromPeer;
                packetFromPeer = (NPacket) in.readObject();
                if(packetFromPeer.type==NPacket.PACKET_INTRO){
                	//learn by other client's broadcast
                	if(map.get(packetFromPeer.sender)==null){
                		map.put(packetFromPeer.sender,out);
                		System.out.println("connection to "+packetFromPeer.sender+packetFromPeer.location.toString()+" added");
                	}	
                }
                else{
                	/*
					TBD	execute the packet, pass the token ring
	                */
                }
                
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}