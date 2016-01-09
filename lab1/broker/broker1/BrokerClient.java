import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerClient{

	public static void main(String[] args){
		String host=null;
		int port=-1;
		try{
			if(args.length == 2 ) {
				host=args[0];
				port=Integer.parseInt(args[1]);
			} else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}	
			System.out.println("host: "+host+" port number: "+port);
			Socket socket=new Socket(host, port);
			Scanner scr = new Scanner(System.in);	
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			ObjectInputStream socket_reader=new ObjectInputStream(socket.getInputStream()); //for reading lines
			ObjectOutputStream writer=new ObjectOutputStream(socket.getOutputStream());

			System.out.print("CONSOLE>");	
			String request=null;
			while((request = scr.next()) != null
				&& request.toLowerCase().indexOf("bye") == -1){
				BrokerPacket packet=new BrokerPacket();
				packet.type=BrokerPacket.BROKER_REQUEST; 
				packet.symbol=request;
				writer.writeObject(packet);

				BrokerPacket reply=(BrokerPacket)socket_reader.readObject();
				if (reply.type == BrokerPacket.BROKER_QUOTE)
					System.out.println("Quote from broker: "+reply.symbol);
				/* re-print console prompt */
				System.out.print("CONSOLE>");

			}
			/* tell server that i'm quitting */
			BrokerPacket packetToServer = new BrokerPacket();
			packetToServer.type = BrokerPacket.BROKER_BYE ;
			packetToServer.symbol = "Bye!";
			writer.writeObject(packetToServer);

			writer.close();
			socket_reader.close();
			reader.close();
			socket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}