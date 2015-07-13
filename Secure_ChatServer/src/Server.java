
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends Thread {
	
	private ServerSocket ss;
	private HashMap<Integer,ServerHandler> online;
	private static int num=0;
	private BufferedWriter file;
	private Boolean bool=true;
	private Logger logger;
	
	public Server(int port) throws IOException{
		
		ss = new ServerSocket(port);
		online = new HashMap<Integer,ServerHandler>();
		initLogger();
	}
	
	private void initLogger(){
		String logPath = "./";
		try {
			this.logger = Logger.getLogger(Server.class.getName());
			Handler fileHandler = new FileHandler(logPath + "chatServer.log", 2000, 5);
			fileHandler.setFormatter(new MyFormatter());
			this.logger.addHandler(fileHandler);
			//System.out.println("LOGGER STARTED!!");
			
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void runServer() throws IOException{
		
		file = new BufferedWriter( new FileWriter("in.txt"));
		Socket s;
		
		while(bool){
				s = ss.accept();
			
				logger.log(Level.CONFIG, "NEW Scocket Connection");
				ServerHandler cli = new ServerHandler(num++, s, file, online, logger);
				
				new Thread(cli).start();
		}
		
		System.out.println("Fecha todas as conecções\n");
		for(ServerHandler cli: online.values()){
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
