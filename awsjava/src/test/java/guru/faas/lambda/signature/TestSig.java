package guru.faas.lambda.signature;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class TestSig {

	@Test
	public void test() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeyException, NoSuchProviderException, SignatureException, DecoderException {
		SignatureVerifier sv = new SignatureVerifier();
		String signaturehex = signTestFile();
		System.out.println(signaturehex);
		String resultado = sv.myHandler("This is a sample textfile:" + signaturehex, null);
		System.out.println("Result: " + resultado);
	}
	
	public  String signTestFile() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		InputStream keystoreLocation = TestSig.class.getClassLoader().getResourceAsStream("minhakeystore.jks");
	    byte[] realSig = null;
	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    String keystorePassword = "teste001";
	    keystore.load(keystoreLocation, keystorePassword.toCharArray());
	    Key key = keystore.getKey("meucertificado", keystorePassword.toCharArray());
	    if (key instanceof PrivateKey) {
	    	try {
		    	Signature sig = Signature.getInstance("MD5withRSA", "SunRsaSign"); 
		    	sig.initSign((PrivateKey) key);
		    	InputStream filepath = TestSig.class.getClassLoader().getResourceAsStream("text.txt");
		    	BufferedInputStream bufin = new BufferedInputStream(filepath);
		    	byte[] buffer = new byte[1024];
		    	int len;
		    	while ((len = bufin.read(buffer)) >= 0) {
		    		sig.update(buffer, 0, len);
		    	};
		    	bufin.close();	
		    	realSig = sig.sign();
	    	}
	    	catch (Exception ex) {
	    		System.out.println("@@@ Exception: " + ex.getLocalizedMessage());
	    		realSig = null;
	    	}
	    }
    	return Hex.encodeHexString(realSig);
	}

}
