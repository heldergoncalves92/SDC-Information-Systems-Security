import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPairGenerator;


public class GeneratorPairKeys {
	
	private FileOutputStream outPub, outPriv;
	
	public GeneratorPairKeys (){
		try {
			outPub = new FileOutputStream("public.key");
			outPriv = new FileOutputStream("private.key");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateKey() {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
			kgen.initialize(1024, new SecureRandom());
			KeyPair kpair = kgen.generateKeyPair();
			
			X509EncodedKeySpec x509 = new X509EncodedKeySpec(kpair.getPublic().getEncoded());
			outPub.write(x509.getEncoded());
			outPub.flush();
			outPub.close();
			
			PKCS8EncodedKeySpec pk8 = new PKCS8EncodedKeySpec(kpair.getPrivate().getEncoded());
			outPriv.write(pk8.getEncoded());
			outPriv.flush();
			outPriv.close();
			
			System.out.println("Genereted KeyPair in \"public.key\" and \"private.key\"!!\n");

		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GeneratorPairKeys gen = new GeneratorPairKeys();
		gen.generateKey();
	}
	
}
