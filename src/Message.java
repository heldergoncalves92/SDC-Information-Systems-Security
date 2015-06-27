import java.io.Serializable;


public class Message implements Serializable{
	
	private byte[] msg;
	private byte[] mac;
	
	public Message(byte[] msg, byte[] mac){
		this.msg = msg;
		this.mac = mac;
	}
	
	public Message(byte[] msg){
		this.msg = msg;
	}
	
	public byte[] getMsg(){
		return this.msg;
	}
	
	public byte[] getMac(){
		return this.mac;
	}
}
