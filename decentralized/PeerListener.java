import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PeerListener extends Thread{
	Socket socket;
	ConcurrentHashMap<String,ObjectOutputStream> map;  
	Location peer_location;
	String peer_name;
	private String myself;
	private Location mylocation;
	AtomicInteger token ;
	
	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map,String myself,Location mylocation,AtomicInteger token){
		//server as sever
		this.socket=socket;
		this.map=map;
		this.myself=myself;
		this.mylocation=mylocation;
		this.token=token;
	}


	public PeerListener(ConcurrentHashMap<String,ObjectOutputStream> map,String peer_name,Location peer_location,String myself,Location mylocation,AtomicInteger token){
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
		this.token=token;
	}
	public void run(){
		try{

			assert(socket!=null);

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if(peer_name!=null&&peer_location!=null){
				//learn directly from naming service, introduce my self
				map.put(peer_name,out);
				MPacket packetToPeer = new MPacket();  
		        packetToPeer.location=mylocation;
		        packetToPeer.sender =myself;
		        packetToPeer.type=MPacket.PACKET_INTRO;
		        out.writeObject(packetToPeer);
		        out.flush();
		      	System.out.println("connection to "+peer_name+peer_location.toString()+" added");	           
			}

			ObjectInputStream in= new ObjectInputStream(socket.getInputStream());
		
	
						
			while(true){
				MPacket packetFromPeer;
                packetFromPeer = (MPacket) in.readObject();
                if(packetFromPeer.type==MPacket.PACKET_INTRO){
                	//learn by other client's broadcast
                	if(map.get(packetFromPeer.sender)==null){
                		map.put(packetFromPeer.sender,out);
                		System.out.println("connection to "+packetFromPeer.sender+packetFromPeer.location.toString()+" added");
                	}	
                }

                else if(packetFromPeer.type==MPacket.TOKEN){
                	token.set(packetFromPeer.sequenceNumber);
                	continue;//doing nothing
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