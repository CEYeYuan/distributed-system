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
	String myself;
	Location mylocation;
	AtomicInteger token ;
	ConcurrentHashMap<String, Client> clientTable = null;
	AtomicInteger seq ;
	
	public PeerListener(Socket socket,ConcurrentHashMap<String,ObjectOutputStream> map,String myself,Location mylocation,AtomicInteger token, ConcurrentHashMap<String, Client>clientTable, AtomicInteger seq){
		//server as sever
		this.socket=socket;
		this.map=map;
		this.myself=myself;
		this.mylocation=mylocation;
		this.token=token;
		this.clientTable=clientTable;
		this.seq=seq;
	}


	public PeerListener(ConcurrentHashMap<String,ObjectOutputStream> map,String peer_name,Location peer_location,String myself,Location mylocation,AtomicInteger token, ConcurrentHashMap<String, Client> clientTable,AtomicInteger seq){
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
		this.clientTable=clientTable;
		this.seq=seq;
	}
	public void run(){
		try{

			assert(socket!=null);
			MPacket received = null;
	        Client client = null;
	        if(Debug.debug) System.out.println("Starting ClientListenerThread");
	        /**********************My code here******************************
	                1. use a min heap to keep track of all the received command
	                2. peek the root command of the heap when new cmd arrives, 
	                   if it's consistent with the expected seq. number, pop it,
	                   and then execute
	                3. if not, just wait for the next command   
	        *****************************************************************/
	        PriorityQueue<MPacket> queue = new PriorityQueue<MPacket>(20,new Comparator<MPacket>() {
	            @Override
	            public int compare(MPacket p1, MPacket p2) {
	                return p1.sequenceNumber-p2.sequenceNumber;
	            }
	        });


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
                	/*
					 thread to handle missle
                	*/
                	continue;//doing nothing
                }

                else if(packetFromPeer.type==MPacket.PACKET_NORMAL){
                	received = packetFromPeer;
	                System.out.println("Received " + received+" current seq="+seq);
	                queue.add(received);
	                while(!queue.isEmpty()){
	                    if(queue.peek().sequenceNumber==seq.get()){
	                        System.out.println("ececuting seq.: "+seq);
	                        seq.addAndGet(1);
	                        received=queue.poll();
	                        client = clientTable.get(received.name);
	                        if(received.event == MPacket.UP){
	                            client.forward();
	                        }else if(received.event == MPacket.DOWN){
	                            client.backup();
	                        }else if(received.event == MPacket.LEFT){
	                            client.turnLeft();
	                        }else if(received.event == MPacket.RIGHT){
	                            client.turnRight();
	                        }else if(received.event == MPacket.FIRE){
	                            client.fire();
	                           
	                        }else{
	                            throw new UnsupportedOperationException();
	                        }    
	                    }
	                    else
	                        break;
	                }
                }

                else{
                	/*
					missle
	                */
                }
                
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}