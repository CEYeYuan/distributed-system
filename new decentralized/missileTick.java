import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class missileTick implements Runnable {

    ConcurrentHashMap<String,ObjectOutputStream> map;  
    String myself;
    boolean added=false;
    AtomicInteger token;
    Socket socket;
    BlockingQueue<MPacket> eventQueue;
    String name;
    
    
    //String myself,Location mylocation,AtomicInteger token, ConcurrentHashMap<String, Client>clientTable, AtomicInteger seq
    //BlockingQueue eventQueue
    public missileTick(String name, AtomicInteger token, BlockingQueue<MPacket> eventQueue){
        this.token=token; 
        this.eventQueue=eventQueue;
        this.name = name;
    }


  
        public void run(){
          try {
            while (true) {
                //System.out.println("Ticking..." + name + " " +  MPacket.MISSILE + " " + MPacket.PROJ);
                //MPacket to_all = new MPacket();
                //to_all.type = MPacket.MISSILE;
                //eventQueue.add(to_all);
                //name = "p1";
                
//                MPacket to_all = new MPacket();
//                to_all.type =  MPacket.MISSILE;
                		
                eventQueue.put(new MPacket(name, MPacket.MISSILE, MPacket.PROJ));
                //eventQueue.put(to_all);
                Thread.sleep(200);
            }
          } catch (Exception e) {
            System.err.println("ERROR: encountered during missileTick. Exiting.");
            e.printStackTrace();
            System.exit(1);
          }
        } 
}
