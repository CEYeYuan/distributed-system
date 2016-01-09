import java.io.*;
import java.net.*;
import java.util.*;
public class OnlineBroker{
	public static void main(String[] args){
		int port=Integer.parseInt(args[0]);
		System.out.println("host: "+host+" port number: "+port);
		Socket socket=new Socket(host, port);
	}
}