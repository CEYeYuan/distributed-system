import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class PeerListenerDispatcher extends Thread{
	private ServerSocket serverSocket;
	private ConcurrentHashMap<String,ObjectOutputStream> map;
	private String myself;
	private Location mylocation;
	
	public PeerListenerDispatcher(ServerSocket serverSocket,ConcurrentHashMap<String,ObjectOutputStream> map,String myself,Location mylocation){
		this.serverSocket=serverSocket;
		this.map=map;
		this.myself=myself;
		this.mylocation=mylocation;
	}


	public void run(){
		try{
			while(true){
            Socket socket=serverSocket.accept();
            new Thread(new PeerListener(socket,map,myself,mylocation)).start();
       		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}