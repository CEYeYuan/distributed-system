import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class BrokerServerHandlerThread implements Runnable{
	private Socket socket = null;
	private ConcurrentHashMap<String,Integer> map=null;
	private String broker=null;
	private Socket other=null;
	private ObjectOutputStream other_out=null;
	private ObjectInputStream  other_in=null;
	private ObjectOutputStream out_lookup=null;
	private ObjectInputStream in_lookup=null;
	private Socket socket_lookup=null;
	BrokerPacket packetToServer ;
	BrokerPacket packetFromServer;
	//a socket connect to tse/nasdaq

	public BrokerServerHandlerThread(Socket socket,ConcurrentHashMap<String,Integer> map,String broker,ObjectOutputStream out_lookup,ObjectInputStream in_lookup,Socket socket_lookup) {
		this.map=map;
		this.socket = socket;
		this.broker=broker;
		this.in_lookup=in_lookup;
		this.out_lookup=out_lookup;
		this.socket_lookup=socket_lookup;
		System.out.println("Created new Thread to handle client");
	}

	public void run() {

		boolean gotByePacket = false;
		try {

			boolean inited=false;
			/* stream to read from client */
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			BrokerPacket packetFromClient;
			
			/* stream to write back to client */
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
			

			while (( packetFromClient = (BrokerPacket) fromClient.readObject()) != null) {
				/* create a packet to send reply back to client */
				BrokerPacket packetToClient = new BrokerPacket();
				
				
				/* process message */
				/* just echo in this example */
				if(packetFromClient.type == BrokerPacket.BROKER_REQUEST||packetFromClient.type == BrokerPacket. BROKER_FORWARD) {
					packetToClient.type = BrokerPacket.BROKER_QUOTE;
					if(map.get(packetFromClient.symbol.toLowerCase())==null||map.get(packetFromClient.symbol.toLowerCase())==0){
						//not found on the local server
						if(!inited){
							//create a connection to other server
							packetToServer = new BrokerPacket();
							packetToServer.type = BrokerPacket.LOOKUP_REQUEST;
							if(this.broker.equals("nasdaq"))
								packetToServer.symbol = "tse";
							else
								packetToServer.symbol="nasdaq";
							out_lookup.writeObject(packetToServer);

							/* print server reply */
							
							packetFromServer = (BrokerPacket) in_lookup.readObject();
							if(packetFromServer.symbol.indexOf("not")!=-1){
								System.out.println(packetToServer.symbol+" not found, try it again");
							}else{
								int start=packetFromServer.symbol.indexOf(" HOST: ");
								int end= packetFromServer.symbol.indexOf("PORT: ");
								String host=packetFromServer.symbol.substring(start+7,end);
								int port=Integer.parseInt(packetFromServer.symbol.substring(end+6));
								other=new Socket(host,port);
								other_out= new ObjectOutputStream(other.getOutputStream());
								other_in= new ObjectInputStream(other.getInputStream());
								System.out.println("Host: "+host+" Port: "+port);
								inited=true;
							}
						}

						packetToClient.type = BrokerPacket.BROKER_ERROR;
						packetToClient.symbol = "0";

						if(inited&&packetFromClient.type!=BrokerPacket.BROKER_FORWARD){
							BrokerPacket packetToServer = new BrokerPacket();
							packetToServer.type = BrokerPacket.BROKER_FORWARD;
							packetToServer.symbol=packetFromClient.symbol;
							other_out.writeObject(packetToServer);

							packetToClient = (BrokerPacket) other_in.readObject();
						}		
					}
					else
						packetToClient.symbol = map.get(packetFromClient.symbol)+"";
					
					System.out.println("From Client: " + packetFromClient.symbol);
				
					/* send reply back to client */
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}

				
				else if(packetFromClient.type == BrokerPacket.EXCHANGE_ADD) {	
					packetToClient.type = BrokerPacket.EXCHANGE_REPLY;
					if(map.get(packetFromClient.symbol.toLowerCase())==null){
						packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" added";
						map.put(packetFromClient.symbol.toLowerCase(),0);
					}
					else{
						packetToClient.type = BrokerPacket.ERROR_SYMBOL_EXISTS;
						packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" exists";
					}
					System.out.println("From Client: " + packetFromClient.symbol);
				
					/* send reply back to client */
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}

				else if(packetFromClient.type == BrokerPacket.EXCHANGE_UPDATE) {
					packetToClient.type = BrokerPacket.EXCHANGE_REPLY;
					String cmd[]=new String[2];
					StringTokenizer st = new StringTokenizer(packetFromClient.symbol);
					for(int i=0;i<2;i++){
						cmd[i]=null;
						if(st.hasMoreTokens())
							cmd[i]=st.nextToken();
					}	

					if(map.get(cmd[0].toLowerCase())==null){
						packetToClient.type = BrokerPacket.ERROR_INVALID_SYMBOL;
						packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" invalid";
					}
					else{
						if(Integer.parseInt(cmd[1])>300||Integer.parseInt(cmd[1])<1){
							packetToClient.type = BrokerPacket.ERROR_OUT_OF_RANGE;
							packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" out of range.";
						}
						else{
							map.put(cmd[0].toLowerCase(),Integer.parseInt(cmd[1]));
							packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" updated to "+cmd[1];
						}
					}
					System.out.println("From Client: " + packetFromClient.symbol);
				
					/* send reply back to client */
					
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}

				else if(packetFromClient.type == BrokerPacket.EXCHANGE_REMOVE) {	
					packetToClient.type = BrokerPacket.EXCHANGE_REPLY;
					if(map.get(packetFromClient.symbol.toLowerCase())==null){
						packetToClient.type = BrokerPacket.ERROR_INVALID_SYMBOL;
						packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" invalid";
					}
					else{
						map.remove(packetFromClient.symbol.toLowerCase());
						packetToClient.symbol = packetFromClient.symbol.toUpperCase()+" removed";			
					}
					System.out.println("From Client: " + packetFromClient.symbol);
				
					/* send reply back to client */
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}
				
				/* Sending an ECHO_NULL || ECHO_BYE means quit */
				else if (packetFromClient.type == BrokerPacket.BROKER_NULL || packetFromClient.type == BrokerPacket.BROKER_BYE) {
					gotByePacket = true;
					packetToClient = new BrokerPacket();
					packetToClient.type = BrokerPacket.BROKER_BYE;
					packetToClient.symbol = "Bye!";
					toClient.writeObject(packetToClient);
					break;
				}
				else{
					/* if code comes here, there is an error in the packet */
					//System.out.println(packetFromClient.type);
					System.err.println("ERROR: Unknown BROKER_* packet!!");
					System.exit(-1);
				}
				
			}
			
			
			fromClient.close();
			toClient.close();
			socket.close();

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
		finally{
			  /* cleanup when client exits */
			try{
				 PrintWriter writer = new PrintWriter(broker);
	            //update the new file
	            writer.print("");
	            for(String key:map.keySet()){
	                 writer.format("%s %d\n", key, map.get(key));
	            }
	            writer.close();
			}catch (Exception e){
				e.printStackTrace();
			}
           
		}
	}
}
