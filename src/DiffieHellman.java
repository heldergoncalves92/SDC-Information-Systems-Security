import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;



public class DiffieHellman {

	public DiffieHellman() {
	}

	public KeyPair generateKey() {

		KeyPair aPair = null;
		try {
			BigInteger P = new BigInteger(
					"99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
			BigInteger G = new BigInteger(
					"44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");

			DHParameterSpec dh = new DHParameterSpec(P, G);
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DH");
			keyGenerator.initialize(dh, new SecureRandom());
			aPair = keyGenerator.generateKeyPair();

		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aPair;
	}

	public byte[] sessionKey(PrivateKey secretKey, PublicKey publicKey) {

		byte[] key = null;

		try {
			KeyAgreement keyAgg = KeyAgreement.getInstance("DH");
			keyAgg.init(secretKey);
			keyAgg.doPhase(publicKey, true);
			key = keyAgg.generateSecret();

		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return key;
	}
	
}
