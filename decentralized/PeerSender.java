import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerSender extends Thread{

	ConcurrentHashMap<String,ObjectOutputStream> map;  
	public PeerSender(ConcurrentHashMap<String,ObjectOutputStream> map){
		this.map=map;
	}

	public void run(){
		
		/*
		read from keyboard, broadcast to all users(check if has token first),pass the token
		*/
			
	}

	private void broadcast(NPacket packet){
		try{
			for (String s:map.keySet()){
			ObjectOutputStream tmp=map.get(s);
			assert(tmp!=null);
			tmp.writeObject(packet);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	private ObjectOutputStream findNext(){
		//find the next user for the token ring
		return null;
	}
}