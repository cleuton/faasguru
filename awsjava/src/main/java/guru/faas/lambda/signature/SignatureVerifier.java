package guru.faas.lambda.signature;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.Signature;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SignatureVerifier {
    public String myHandler(String textAndSignature, Context context) throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException, DecoderException {
    	// Comment following two lines to run locally:
        LambdaLogger logger = context.getLogger();
        logger.log("received : " + textAndSignature);
        String [] parameters = textAndSignature.split(":");
        String returnValue = "";
		returnValue =  "Signature is " + verify(parameters[1],parameters[0],"*","meucertificado","teste001");
		// Comment following line to run locally:
		logger.log(returnValue);
        return returnValue;
    }
    
	public static boolean verify(String hexSignature, String texto,
			String keystorePath, String alias, String keystorePassword) 
					throws KeyStoreException, NoSuchAlgorithmException, 
							CertificateException, IOException, InvalidKeyException, 
							NoSuchProviderException, DecoderException, SignatureException {
		boolean resultado = false;
		InputStream keystoreLocation = null;
		if (!keystorePath.equals("*")) {
			FileInputStream fisKs = new FileInputStream(keystorePath);
			keystoreLocation = fisKs;
		}
		else {
			InputStream isKs = SignatureVerifier.class.getClassLoader().getResourceAsStream("minhakeystore.jks");
			keystoreLocation = isKs;
		}
	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(keystoreLocation, keystorePassword.toCharArray());
	    java.security.cert.Certificate certificate = keystore.getCertificate(alias);
	    PublicKey pubKey = certificate.getPublicKey();
		Signature sig = Signature.getInstance("MD5withRSA", "SunRsaSign"); 
		sig.initVerify(pubKey);
		Hex hex = new Hex();
		byte [] textContent = texto.getBytes("UTF-8");
	    sig.update(textContent);
		byte [] signature = (byte[]) hex.decode(hexSignature);
	    
		resultado = sig.verify(signature);
		
		return resultado;
	}
    
}
