import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class PeerListenerDispatcher extends Thread{
	private ServerSocket serverSocket;
	private ConcurrentHashMap<String,ObjectOutputStream> map;
	private String myself;
	private Location mylocation;
	AtomicInteger token ;
	ConcurrentHashMap<String, Client> clientTable = null;
	AtomicInteger seq ;
	
	public PeerListenerDispatcher(ServerSocket serverSocket,ConcurrentHashMap<String,ObjectOutputStream> map,String myself,Location mylocation,AtomicInteger token, ConcurrentHashMap<String, Client> clientTable, AtomicInteger seq ){
		this.serverSocket=serverSocket;
		this.map=map;
		this.myself=myself;
		this.mylocation=mylocation;
		this.token=token;
		this.clientTable=clientTable;
		this.seq=seq;
	}


	public void run(){
		try{
			while(true){
            Socket socket=serverSocket.accept();
            new Thread(new PeerListener(socket,map,myself,mylocation,token,clientTable,seq)).start();
       		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}