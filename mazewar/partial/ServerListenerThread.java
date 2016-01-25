import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.*;

public class ServerListenerThread implements Runnable {

    private MSocket mSocket =  null;
    private BlockingQueue eventQueue = null;

    public ServerListenerThread( MSocket mSocket, BlockingQueue eventQueue){
        this.mSocket = mSocket;
        this.eventQueue = eventQueue;
    }

    public void run() {
        MPacket received = null;
        if(Debug.debug) System.out.println("Starting a listener");
        //keep a local sequence number for each client thread
        int seq=0;
        PriorityQueue<MPacket> queue = new PriorityQueue<MPacket>(20,new Comparator<MPacket>() {
            @Override
            public int compare(MPacket p1, MPacket p2) {
                return p1.sequenceNumber-p2.sequenceNumber;
            }
        });
        while(true){
            try{
                received = (MPacket) mSocket.readObject();
                queue.add(received);
                if(Debug.debug) System.out.println("Received: " + received);
                    while(!queue.isEmpty()){
                        if(queue.peek().sequenceNumber==seq){
                            received=queue.poll();
                            eventQueue.put(received); 
                            seq++;
                        }
                        else
                            break;
                    }
                  
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();    
            }catch(IOException e){
                Thread.currentThread().interrupt();
            }catch(ClassNotFoundException e){
                Thread.currentThread().interrupt();    
            }
            
        }
    }
}
