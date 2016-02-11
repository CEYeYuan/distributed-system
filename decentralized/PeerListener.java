import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerListener extends Thread{
	Socket socket;
	ConcurrentHashMap<String,ObjectOutputStream> map;  
	Location peer_location;
	String peer_name;
	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map){
		//server as sever
		this.socket=socket;
		this.map=map;
	}


	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map,String peer_name,Location peer_location){
		//learn from name service
		this.socket=socket;
		this.map=map;
		this.peer_name=peer_name;
		this.peer_location=peer_location;
	}
	public void run(){
		try{
			ObjectInputStream in= new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if(peer_name!=null&&peer_location!=null){
				//learn directly from naming service
				map.put(peer_name,out);
	            System.out.println("connection to "+peer_name+peer_location.toString()+" added");
			}

			for(String s:map.keySet()){
				// tell all the previous joined user about my self
				ObjectOutputStream tmp=map.get(s);
				NPacket packetToPeer = new NPacket();  
		        packetToPeer.location=peer_location;
		        packetToPeer.sender =peer_name;
		        packetToPeer.type=NPacket.PACKET_INTRO;
		        out.writeObject(packetToPeer);
			}
						
			while(true){
				NPacket packetFromPeer;
                packetFromPeer = (NPacket) in.readObject();
                if(packetFromPeer.type==NPacket.PACKET_INTRO){
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