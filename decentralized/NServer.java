import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class NServer{
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = null;
		ConcurrentHashMap<String,Location> map=new ConcurrentHashMap<String,Location>();
		boolean listening=true;
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
         while (listening) {
            new Thread(new NServerHandler(serverSocket.accept(),map)).start();
          
        }
	}
}