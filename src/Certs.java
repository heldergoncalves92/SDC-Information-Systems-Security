import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class Certs {

	private Signature toSign;
	private Signature toVerify;
	
	public Certs(){}
	
	public void initToSign(){
		
		byte[] encodedKey = new byte[1024];

		try {
			FileInputStream priv = new FileInputStream("private.key");
			
			// read from file
			priv.read(encodedKey);
			priv.close();
			
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PrivateKey privKey = keyFactory.generatePrivate(keySpec);
			
			toSign = Signature.getInstance("SHA1withRSA");
			toSign.initSign(privKey);
			
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void initToVerify(){
		
		try {
			byte[] encodedKey = new byte[1024];
			FileInputStream pub = new FileInputStream("public.key");
			
			// read from file
			pub.read(encodedKey);
			pub.close();
			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PublicKey pubKey = keyFactory.generatePublic(keySpec);
	        
	        toVerify = Signature.getInstance("SHA1withRSA");
			toVerify.initVerify(pubKey);
			
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] signMsg(byte[] msg){
		byte[] signed = null;

		try {
			toSign.update(msg);
			signed = toSign.sign();
			
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return signed;
	}
	
	public boolean verifyMsg(byte[] signed){
		boolean res = false;
		try {
			res = toVerify.verify(signed);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[] args) {
		Certs c1 = new Certs();
		Certs c2 = new Certs();
		
		c1.initToVerify(); //Serv
		c2.initToSign();//Cli
		
		byte[] encoded = "Ola".getBytes();
		byte[] signed = c2.signMsg(encoded);
		
		if(c1.verifyMsg(signed))
			System.out.println("True");
		else 
			System.out.println("False");
	}
}
