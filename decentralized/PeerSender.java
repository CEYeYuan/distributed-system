import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PeerSender implements Runnable {

    ConcurrentHashMap<String,ObjectOutputStream> map;  
    String myself;
    boolean added=false;
    String arr[];
    AtomicInteger token ;
    BlockingQueue<MPacket> eventQueue=null;


    public PeerSender(ConcurrentHashMap<String,ObjectOutputStream> map,String myself,AtomicInteger token,BlockingQueue eventQueue){
        this.map=map;
        this.myself=myself;
        this.token=token;
        this.eventQueue=eventQueue;
    }


    public void run(){
        try{
            while(true){

                if(token.get()==-1){
                    continue;//not holding token, do nothing
                }
                    
                else{

                    if(eventQueue.size()!=0){
                        //do have at least one packet to broadcast
                        try{                
                        //Take packet from queue
                            MPacket to_all = (MPacket)eventQueue.take();
                            to_all.type=MPacket.PACKET_NORMAL;
                            //add a sequence number to make sure a client's move won't be out of order
                            to_all.sequenceNumber=token.get();
                            to_all.name=myself;
                            token.getAndAdd(1);
                            if(Debug.debug) System.out.println("broadcast opration " + to_all);
                            broadcast(to_all);    
                         }catch(InterruptedException e){
                            e.printStackTrace();
                            Thread.currentThread().interrupt();    
                        } 
                    }

                    
                    //System.out.println(myself+" hold the token "+" seq: " +token.get()+",then pass to "+findNext());
                    
                    ObjectOutputStream out=map.get(findNext());
                    MPacket packetToClient = new MPacket();
                    packetToClient.type=MPacket.TOKEN ;
                    packetToClient.sequenceNumber=token.get();
                    Thread.sleep(1000);
                    out.writeObject(packetToClient);
                    token.set(-1);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
            /*
            if hold the token, call broadcast
            pass token to next person


            else
                queue all the keypress
            */
        
            
    }

    private void broadcast(MPacket packet){
        try{
            for (String s:map.keySet()){
            ObjectOutputStream tmp=map.get(s);
            assert(tmp!=null);
            tmp.writeObject(packet);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    

    private String findNext(){
        //find the next user for the token ring
        int i=0;
        if(added==false){   
            arr=new String[map.size()];
            i=0;
            for (String s:map.keySet()){
                arr[i]=s;
                i++;
            }
            Arrays.sort(arr);
            added=true;
        }
    
        for(i=0;i<map.size();i++){
            if(arr[i].equals(myself))
                break;
        }
        if(i+1<map.size())
            return arr[i+1];
        else
            return arr[0];
        
    }
}
