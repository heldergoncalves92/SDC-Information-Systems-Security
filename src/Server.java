
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server extends Thread {
	
	private ServerSocket ss;
	private HashMap<Integer,ThreadServer> clients;
	private static int num=0;
	private BufferedWriter file;
	private Boolean bool=true;
	
	public Server(int port) throws IOException{
		
		ss = new ServerSocket(port);
		clients = new HashMap<Integer,ThreadServer>();

	}
	
	
	private void runServer() throws IOException{
		
		file = new BufferedWriter( new FileWriter("in.txt"));
		Socket s;
		
		while(bool){
				s = ss.accept();
			
				System.out.println("Nova Socket\n");
				ThreadServer cli = new ThreadServer(num, s, file, this);
				
				new Thread(cli).start();
				
				clients.put(num++, cli);
		}
		
		System.out.println("Fecha todas as conecções\n");
		for(ThreadServer cli: clients.values()){
			cli.closeConn();
			
		}
		file.close();
		ss.close();
	}
	
	protected void shutdown(){
		System.out.println("Close Server\n");
		System.exit(0);
	}
	
	
	
	public static void main(String[] args) throws IOException{
		
		Server server = new Server(6000);
		server.runServer();
	}
	
}
