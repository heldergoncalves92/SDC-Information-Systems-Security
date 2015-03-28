import java.io.Serializable;


public class Message implements Serializable{
	
	private byte[] msg;
	
	public Message(byte[] msg){
		this.msg = msg;
	}
	
	public byte[] getMsg(){
		return this.msg;
	}
	
	
	
	
	

}
