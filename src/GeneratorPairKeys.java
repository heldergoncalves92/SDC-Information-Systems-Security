import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class KeyPairGenerator {
	
	public static void main(String[] args) {
		
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("RSA");
			SecretKey key = kgen.generateKey();
			
			KeySpec k;
			
			KeyFactory factory = KeyFactory.getInstance(kgen.getAlgorithm());
			Signature sign = Signature.getInstance(kgen.getAlgorithm());
			
			//RSAPrivateKeySpec privateKey = factory.generatePrivate(k);
			RSAPublicKeySpec publicKey;
		
			
			
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
