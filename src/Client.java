import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class Client{
	
	public static void main(String[] args) throws IOException{
		
	/*	String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket s = new Socket(host, port);
		*/String msg = "";
		
		Socket s = new Socket("localhost", 	6000);
		
		BufferedReader sockIn = new BufferedReader( new InputStreamReader(System.in));
		BufferedWriter sockOut = new BufferedWriter( new OutputStreamWriter(s.getOutputStream())); 
		
		while(true){
			msg = sockIn.readLine();
				
			sockOut.write(msg);
			sockOut.flush();
			
			if(msg.equals("Sair")  || msg.equals("Shutsown")) break; 
				
		}
		s.close();
		
	}
	
	
	
}
