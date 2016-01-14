import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class OnlineBroker {
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
                while(true){
                    BrokerPacket packetToServer = new BrokerPacket();
                    packetToServer.type = BrokerPacket.LOOKUP_REGISTER;
                    packetToServer.locations=new BrokerLocation[]{new  BrokerLocation(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[2]))};
                    System.out.println();
                    packetToServer.symbol =args[3];
                    out_lookup.writeObject(packetToServer);


                        /* print server reply */
                    BrokerPacket packetFromServer;
                    packetFromServer = (BrokerPacket) in_lookup.readObject();
                    System.out.println(packetFromServer.symbol);
                    if(packetFromServer.symbol.indexOf("not")!=-1){
                        System.out.println("not found, try it again");
                        continue;
                    }
                    break;
               }

        	} else {
        		System.err.println("ERROR: Invalid arguments!");
        		System.exit(-1);
        	}
        } catch (Exception e) {
            System.err.println("ERROR: Could not listen on port!");
            System.exit(-1);
        }

        in_lookup.close();
        out_lookup.close();
        socket_lookup.close();
        System.out.println("now operates as a server");
        serverSocket = new ServerSocket(Integer.parseInt(args[2]));

        ConcurrentHashMap<String,Integer> map=new ConcurrentHashMap<String,Integer>(); 
        try{
            Scanner scr=new Scanner(new File(args[3]));
            while(scr.hasNext()){
                map.put(scr.next().toLowerCase(),scr.nextInt());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        while (listening) {
            new Thread(new BrokerServerHandlerThread(serverSocket.accept(),map,args[3])).start();
          
        }

        serverSocket.close();
    }
}
