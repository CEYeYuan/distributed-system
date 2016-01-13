import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerClient{

	public static void main(String[] args){
		String host=null;
		int port=-1;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		ObjectOutputStream out_lookup = null;
		ObjectInputStream in_lookup = null;
		try{
			if(args.length == 2 ) {
				host=args[0];
				port=Integer.parseInt(args[1]);
			} else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}	
			System.out.println("host: "+host+" port number: "+port);
			Socket socket_lookup=new Socket(host, port);
			Socket socket=null;
			//the socket to the naming service
			Scanner scr = new Scanner(System.in);	
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
			out_lookup = new ObjectOutputStream(socket_lookup.getOutputStream());
			in_lookup = new ObjectInputStream(socket_lookup.getInputStream());

			System.out.print("CONSOLE>");	
			String request=null;
			/**************lookup server-> parse broker server********/
			while((request = scr.next()) != null){
				String cmd[]=new String[2];
				StringTokenizer st = new StringTokenizer(request);
				for(int i=0;i<2;i++){
					cmd[i]=null;
					if(st.hasMoreTokens())
						cmd[i]=st.nextToken();
				}	
				if(!cmd[0].equals("local")){
					System.out.println("set up your local server first");
					continue;
				}
				BrokerPacket packetToServer = new BrokerPacket();
				packetToServer.type = BrokerPacket.LOOKUP_REQUEST;
				packetToServer.symbol = request;
				out_lookup.writeObject(packetToServer);

					/* print server reply */
				BrokerPacket packetFromServer;
				packetFromServer = (BrokerPacket) in_lookup.readObject();
				System.out.println(packetFromServer.symbol);
				if(packetFromServer.symbol.indexOf("not")!=-1){
					System.out.println("not found, try it again");
					continue;
				}else{
					try{
						int start=packetFromServer.symbol.indexOf(" HOST: ");
						int end= packetFromServer.symbol.indexOf(" PORT: ");
						host=packetFromServer.symbol.substring(start+8,end);
						port=Integer.parseInt(packetFromServer.symbol.substring(end+8));
						socket=new Socket(host, port);
						out = new ObjectOutputStream(socket.getOutputStream());
						in = new ObjectInputStream(socket.getInputStream());

					}
					catch(Exception e){
						e.printStackTrace();
					}


				}
			}

			/********************************************************/



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

				if (packetFromServer.type == BrokerPacket.BROKER_QUOTE)
					System.out.println("Quote from broker: " + packetFromServer.symbol);
				if(packetFromServer.type == BrokerPacket.BROKER_ERROR)
					System.out.println("Error: " + packetFromServer.symbol);
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
			socket_lookup.close();
			if(socket!=null)
				socket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}