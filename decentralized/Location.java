import java.io.Serializable;
public class Location implements Serializable {
	public String  broker_host;
	public Integer broker_port;
	
	/* constructor */
	public Location(String host, Integer port) {
		this.broker_host = host;
		this.broker_port = port;
	}
	
	/* printable output */
	public String toString() {
		return " HOST: " + broker_host + " PORT: " + broker_port; 
	}
	
}