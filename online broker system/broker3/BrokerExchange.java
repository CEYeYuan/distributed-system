import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerExchange{

	public static void main(String[] args){
		String host=null;
		int port=-1;
		String query=null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		ObjectOutputStream out_lookup = null;
		ObjectInputStream in_lookup = null;
		String request=null;
		try{
			if(args.length == 3 ) {
				host=args[0];
				port=Integer.parseInt(args[1]);
				query=args[2];
			} else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}	
			System.out.println("host: "+host+" port number: "+port);
			Socket socket_lookup=new Socket(host, port);
			Scanner scr = new Scanner(System.in);	
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		
			out_lookup = new ObjectOutputStream(socket_lookup.getOutputStream());
			
			System.out.println("Connecting to the default broker");

			BrokerPacket packetToServer = new BrokerPacket();
			packetToServer.type = BrokerPacket.LOOKUP_REQUEST;
			packetToServer.symbol = args[2];
			out_lookup.writeObject(packetToServer);

			/* print server reply */
			in_lookup = new ObjectInputStream(socket_lookup.getInputStream());
			BrokerPacket packetFromServer;
			packetFromServer = (BrokerPacket) in_lookup.readObject();
			//System.out.println(packetFromServer.symbol);
			if(packetFromServer.symbol.indexOf("not")!=-1){
				System.out.println("not found, try it again");
				System.exit(0);
			}else{
				int start=packetFromServer.symbol.indexOf(" HOST: ");
				int end= packetFromServer.symbol.indexOf("PORT: ");
				host=packetFromServer.symbol.substring(start+7,end);
				port=Integer.parseInt(packetFromServer.symbol.substring(end+6));
				System.out.println("Host: "+host+" Port: "+port);
			}
			
			out_lookup.close();
			in_lookup.close();
			socket_lookup.close();
			Socket socket=new Socket(host, port);
			System.out.println("connected !");
            out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			System.out.print("CONSOLE>");	
			while((request = scr.nextLine()) != null
				&& request.toLowerCase().indexOf("bye") == -1){
				/* make a new request packet */
				StringTokenizer st = new StringTokenizer(request);
				String[] cmd=new String[3];
				for(int i=0;i<=2;i++){
					cmd[i]=null;
					if(st.hasMoreTokens()){
						cmd[i]=st.nextToken();
					}
				}
				if(!(cmd[0].toLowerCase().equals("add")||cmd[0].toLowerCase().equals("update")||cmd[0].toLowerCase().equals("remove")))
					continue;
				packetToServer = new BrokerPacket();
				if(cmd[0].toLowerCase().equals("add")){
					packetToServer.type = BrokerPacket.EXCHANGE_ADD;
				}
				if(cmd[0].toLowerCase().equals("remove"))
					packetToServer.type = BrokerPacket.EXCHANGE_REMOVE;
				packetToServer.symbol = cmd[1];
				
				if(cmd[0].toLowerCase().equals("update")){
					//two parameters
					packetToServer.type = BrokerPacket.EXCHANGE_UPDATE;
					packetToServer.symbol = cmd[1]+" "+cmd[2];
				}
				//System.out.println(packetToServer.symbol);	
				out.writeObject(packetToServer);

				/* print server reply */
				packetFromServer = (BrokerPacket) in.readObject();

				if (packetFromServer.type ==BrokerPacket.EXCHANGE_REPLY)
					System.out.println(packetFromServer.symbol);
				if ((packetFromServer.type ==BrokerPacket.ERROR_INVALID_SYMBOL)||
					(packetFromServer.type ==BrokerPacket.ERROR_OUT_OF_RANGE)||(packetFromServer.type ==BrokerPacket.ERROR_SYMBOL_EXISTS))
					System.out.println("Error: "+packetFromServer.symbol);
				/* re-print console prompt */
				System.out.print("CONSOLE>");

			}
			/* tell server that i'm quitting */
			packetToServer = new BrokerPacket();
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