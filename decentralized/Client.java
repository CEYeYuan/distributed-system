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
               
                NPacket packetToServer = new NPacket();
                packetToServer.location=new  Location(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[2]));
                packetToServer.symbol =args[3];
                out_lookup.writeObject(packetToServer);

                    /* print server reply */
                NPacket packetFromServer;
                packetFromServer = (NPacket) in_lookup.readObject();
                System.out.println(packetFromServer.symbol);
                if(packetFromServer.symbol.indexOf("used")!=-1)
                    return;
                //the name is already used, choose another one

                ConcurrentHashMap<String,Location> map=new ConcurrentHashMap<String,Location>();  
                Thread nlistener=new ClientListenerFromNServer(in_lookup,map);
                nlistener.start();
                packetToServer = new NPacket();
                out_lookup.writeObject(packetToServer);

                

               
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
        //System.out.println("now operates as a server");
       /* serverSocket = new ServerSocket(Integer.parseInt(args[2]));

        try{
            Scanner scr=new Scanner(new File(args[3]));
            while(scr.hasNext()){
                map.put(scr.next().toLowerCase(),scr.nextInt());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        while (listening) {
            new Thread(new BrokerServerHandlerThread(serverSocket.accept(),map,args[3],out_lookup,in_lookup,socket_lookup)).start();
          
        }

        serverSocket.close();
        */
    }
}