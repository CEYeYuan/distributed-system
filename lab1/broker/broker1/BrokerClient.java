import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerClient{

	public static void main(String[] args){
		try{
			String host=args[0];
			int port=Integer.parseInt(args[1]);
			System.out.println("host: "+host+" port number: "+port);
			Socket socket=new Socket(host, port);
			Scanner scr = new Scanner(System.in);	
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			ObjectInputStream socket_reader=new ObjectInputStream(socket.getInputStream()); //for reading lines
			ObjectOutputStream writer=new ObjectOutputStream(socket.getOutputStream());	
			while(true){
				String request=scr.next();
				BrokerPacket packet=new BrokerPacket();
				packet.type=BrokerPacket.BROKER_REQUEST; 
				packet.symbol=request;
				writer.writeObject(packet);

				BrokerPacket reply=(BrokerPacket)socket_reader.readObject();
				System.out.println("Quote from broker: "+reply.symbol);

			}	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}