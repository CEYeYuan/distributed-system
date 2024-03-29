import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class OnlineBroker {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
        	if(args.length == 1) {
        		serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        	} else {
        		System.err.println("ERROR: Invalid arguments!");
        		System.exit(-1);
        	}
        } catch (IOException e) {
            System.err.println("ERROR: Could not listen on port!");
            System.exit(-1);
        }

        ConcurrentHashMap<String,Integer> map=new ConcurrentHashMap<String,Integer>(); 
        try{
            Scanner scr=new Scanner(new File("nasdaq"));
            while(scr.hasNext()){
                map.put(scr.next().toLowerCase(),scr.nextInt());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        while (listening) {
            new Thread(new BrokerServerHandlerThread(serverSocket.accept(),map)).start();
          
        }

        serverSocket.close();
    }
}
