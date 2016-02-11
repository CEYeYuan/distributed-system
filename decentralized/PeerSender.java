import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerSender extends Thread{

	ConcurrentHashMap<String,ObjectOutputStream> map;  
	String myself;
	public PeerSender(ConcurrentHashMap<String,ObjectOutputStream> map,String myself){
		this.map=map;
		this.myself=myself;
	}

	public void run(){
		while(true){
			/*
			if hold the token, call broadcast
			pass token to next person


			else
				queue all the keypress
			*/
		}
			
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
		int size=map.size();
		String arr[]=new String[size];
		int i=0;
		for (String s:map.keySet()){
			arr[i]=s;
			i++;
		}
		Arrays.sort(arr);
		for(i=0;i<size;i++){
			if(arr[i].equals(myself))
				break;
		}
		if(i+1<size)
			return map.get(arr[i+1]);
		else
			return map.get(arr[0]);
		
	}
}