import java.io.*;
import java.net.*;
import java.util.*;
public class BrokerExchange{

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
				BrokerPacket packetToServer = new BrokerPacket();
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
				BrokerPacket packetFromServer;
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