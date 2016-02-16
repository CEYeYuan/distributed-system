import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class PeerSender extends Thread{

	ConcurrentHashMap<String,ObjectOutputStream> map;  
	String myself;
	boolean added=false;
	String arr[];


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

	private void broadcast(MPacket packet){
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
		int i=0;
		if(added==false){	
			arr=new String[map.size()];
			i=0;
			for (String s:map.keySet()){
				arr[i]=s;
				i++;
			}
			Arrays.sort(arr);
			added=true;
		}
	
		for(i=0;i<map.size();i++){
			if(arr[i].equals(myself))
				break;
		}
		if(i+1<map.size())
			return map.get(arr[i+1]);
		else
			return map.get(arr[0]);
		
	}
}