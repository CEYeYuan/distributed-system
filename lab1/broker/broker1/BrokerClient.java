import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerClient{

	public static void main(String[] args){
		String host=null;
		int port=-1;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
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
		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			System.out.print("CONSOLE>");	
			String request=null;
			while((request = scr.next()) != null
				&& request.toLowerCase().indexOf("bye") == -1){
				/* make a new request packet */
				BrokerPacket packetToServer = new BrokerPacket();
				packetToServer.type = BrokerPacket.BROKER_REQUEST;
				packetToServer.symbol = request;
				out.writeObject(packetToServer);

				/* print server reply */
				BrokerPacket packetFromServer;
				packetFromServer = (BrokerPacket) in.readObject();

				if (packetFromServer.type == BrokerPacket.BROKER_QUOTE )
					System.out.println("Quote from broker: " + packetFromServer.symbol);

				/* re-print console prompt */
				System.out.print("CONSOLE>");

			}
			/* tell server that i'm quitting */
			BrokerPacket packetToServer = new BrokerPacket();
			packetToServer.type = BrokerPacket.BROKER_BYE ;
			packetToServer.symbol = "Bye!";
			out.writeObject(packetToServer);

			out.close();
			in.close();
			reader.close();
			socket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}