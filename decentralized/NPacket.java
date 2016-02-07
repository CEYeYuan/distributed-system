import java.io.Serializable;
 


public class NPacket implements Serializable {
	public static final int NSERVER_REGISTER= 101;
	public static final int NSERVER_LOOKUP  = 102;
	public String symbol;
	public Location location;
	public int type;
}