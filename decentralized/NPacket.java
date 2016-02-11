import java.io.Serializable;
 


public class NPacket implements Serializable {
	public static final int PACKET_INTRO = 0;
	public static final int PACKET_NS_DONE =1;
	public String symbol;
	public String sender;
	public Location location;
	public int type;
}