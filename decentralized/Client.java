import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Client {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket_lookup=null;
        boolean listening = true;
        ObjectOutputStream out_lookup=null;
        ObjectInputStream in_lookup=null;
        try {
        	if(args.length == 4) {
                socket_lookup=new Socket(args[0],Integer.parseInt(args[1]));
                out_lookup = new ObjectOutputStream(socket_lookup.getOutputStream());
                in_lookup = new ObjectInputStream(socket_lookup.getInputStream());
                serverSocket = new ServerSocket(Integer.parseInt(args[2]));//servers as server
               
                NPacket packetToServer = new NPacket();
                Location mylocation=new  Location(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[2]));
                packetToServer.location=mylocation;
                packetToServer.symbol =args[3];
                out_lookup.writeObject(packetToServer);

                    /* print server reply */
                NPacket packetFromServer;
                packetFromServer = (NPacket) in_lookup.readObject();
                System.out.println(packetFromServer.symbol);
                if(packetFromServer.symbol.indexOf("used")!=-1)
                    return;
                //the name is already used, choose another one
                ConcurrentHashMap<String,ObjectOutputStream> out_map=new ConcurrentHashMap<String,ObjectOutputStream>();  
                new Thread(new PeerListenerDispatcher(serverSocket,out_map,args[3],mylocation)).start();//listen to the connection from other peers
               /***************************************
               syncing hashmap from server
                ***************************************/
               int count=0;
               while(true){
                    packetFromServer = (NPacket) in_lookup.readObject();
                    if(packetFromServer.type==NPacket.PACKET_NS_DONE){
                        System.out.println("syncing done: Know "+count+" peers");
                        break;
                    }    
                    //if(packetFromServer.symbol.equals(args[3]))//myself
                      //  continue;
                    if(out_map.get(packetFromServer.symbol)==null){
                        //System.out.println("trying to connect "+packetFromServer.symbol+" "+packetFromServer.location.toString());
                        new Thread(new PeerListener(out_map,packetFromServer.symbol,packetFromServer.location,args[3],mylocation)) .start();
                        count++;
                    }
                
                }




                

               
        	} else {
        		System.err.println("ERROR: Invalid arguments!");
        		System.exit(-1);
        	}
        } catch (Exception e) {
            System.err.println("ERROR: Could not listen on port!");
            System.exit(-1);
        }
        /************************************************************
        each client is a server to all the other clients, and it's a 
        client to all the other clients and naming service as well as the
        naming server
        *************************************************************/
      
    }
}