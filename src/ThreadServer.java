import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ThreadServer implements Runnable {

	private Server server;
	private int id;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		this.server=server;
		this.id=num;
		this.socket=s;
		try{
		this.in = new BufferedReader( new InputStreamReader(s.getInputStream()));
		}catch(IOException e){System.out.println("ERROR: Creating inputStream!!\n");}
		this.out = file;
	}
	
	public void run() {
		String str="";
		
		try{
			while(true){
				str = in.readLine();
				if(str.equals("Sair")) break;
				else if(str.equals("Shutdown")){ server.shutdown(); break;}
				
				out.write(id+": "+ str + "\n");
				out.flush();
			}
			
			System.out.println("=["+ id +"]=\n");
			
			in.close();
			socket.close();
		}catch(IOException e){}
	}
	
	public void closeConn() throws IOException{socket.close();}
	
	
}
