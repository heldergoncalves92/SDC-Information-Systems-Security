import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;



public class Client extends InitCipher{
	
	private byte[] key = "1234567812345678".getBytes();
	private byte[] iv = "1234567812345678".getBytes();
	
	private boolean bool = true;
	
	public Client(){}
	
	public void runClient(){
		/*	String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket s = new Socket(host, port);
		*/
		Cipher c;
		String msg = "";
		int i, offset;
		int blockDim = 16;
		byte[] toEncrypt;
		byte[] endLine = new byte[16];
		endLine[0]=(byte)'\n';
		
		Socket s;
		
		
		try {
			s = new Socket("localhost", 6000);
		
			BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
			//BufferedWriter sockOut = new BufferedWriter( new OutputStreamWriter(s.getOutputStream()));
			
			//Init Cipher
			String type = "AES/CBC/PKCS5Padding";
			c = initCipherByType(type, Cipher.ENCRYPT_MODE, key, iv);
			
			CipherOutputStream cos = new CipherOutputStream(s.getOutputStream(), c);
			
			while(bool){
				
			
				msg = in.readLine();
				System.out.println(msg.length());
				
				toEncrypt = msg.getBytes();
				for(i=0; (offset=i*blockDim) < msg.length(); i++){
					if(msg.length()-offset >= blockDim){
						cos.write(toEncrypt, offset, blockDim);
						cos.flush();
					}else{
						byte[] padding = new byte[blockDim];
						System.arraycopy(toEncrypt, offset, padding, 0, msg.length()-offset);
						cos.write(padding);
						cos.flush();
					}	
				}
				cos.write(endLine);
				cos.flush();
				
				
				/*
				sockOut.write(msg);
				sockOut.flush();
				if(msg.equals("Sair")  || msg.equals("Shutsown")) break; 
				 */		
			}
			cos.close();
			s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		Client cli = new Client();
		cli.runClient();
	
	}
	
	
	
}
