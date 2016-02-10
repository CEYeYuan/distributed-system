import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerListener extends Thread{
	Socket socket;
	ConcurrentHashMap<String,ObjectOutputStream> map;  
	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map){
		this.socket=socket;
		this.map=map;
	}

	public void run(){
		in= new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		try{
			boolean added=false;
			while(true){
				NPacket packetFromPeer;
                packetFromPeer = (NPacket) in.readObject();
                if(!added){
                	map.put(packetFromPeer.sender,out);
                	System.out.println("connection to "+packetFromPeer.sender+packetFromPeer.location.toString()+" added");
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