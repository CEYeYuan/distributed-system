import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

public class ClientListenerKeyBoard implements Runnable {

    private BlockingQueue<MPacket> eventQueue = null;
    
    public ClientListenerKeyBoard(BlockingQueue eventQueue){
      
        this.eventQueue = eventQueue;
    }
    
    public void run() {
        MPacket toServer = null;
        if(Debug.debug) System.out.println("Starting ClientListenerKeyBoard");
        int seq=1;
        while(true){
            try{                
                //Take packet from queue
                toServer = (MPacket)eventQueue.take();
                //add a sequence number to make sure a client's move won't be out of order
                toServer.sequenceNumber=seq;
                seq++;
                if(Debug.debug) System.out.println("Sending " + toServer);
                //mSocket.writeObject(toServer);    
            }catch(InterruptedException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();    
            }
            
        }
    }
}
