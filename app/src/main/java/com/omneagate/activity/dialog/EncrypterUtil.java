package com.omneagate.activity.dialog;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * Created by AJITH on 16-09-2015.
 */
public final class EncrypterUtil {
    private static final String JCE_PROVIDER = "BC";

    private static final String ASYMMETRIC_ALGO = "RSA/ECB/PKCS1Padding";
    private static final int SYMMETRIC_KEY_SIZE = 256;

    private static final String CERTIFICATE_TYPE = "X.509";

    private PublicKey publicKey;
    private Date certExpiryDate;


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Constructor
     * @param publicKeyFileName Location of UIDAI public key file (.cer file)
     */
    public EncrypterUtil(String publicKeyFileName) {
        FileInputStream fileInputStream = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE, JCE_PROVIDER);
            fileInputStream = new FileInputStream(new File(publicKeyFileName));
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fileInputStream);
            publicKey = cert.getPublicKey();
            certExpiryDate = cert.getNotAfter();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not intialize encryption module", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EncrypterUtil(InputStream fileInputStream) {
        //FileInputStream fileInputStream = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE, JCE_PROVIDER);
            //fileInputStream = new FileInputStream(new File(publicKeyFileName));
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fileInputStream);
            publicKey = cert.getPublicKey();
            certExpiryDate = cert.getNotAfter();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not intialize encryption module", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a AES key that can be used as session key (skey)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES", JCE_PROVIDER);
        kgen.init(SYMMETRIC_KEY_SIZE);
        SecretKey key = kgen.generateKey();
        byte[] symmKey = key.getEncoded();
        return symmKey;
    }

    /**
     * Encrypts given data using UIDAI public key
     * @param data Data to encrypt
     * @return Encrypted data
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public byte[] encryptUsingPublicKey(byte[] data) throws IOException, GeneralSecurityException {
        // encrypt the session key with the public key
        Cipher pkCipher = Cipher.getInstance(ASYMMETRIC_ALGO, JCE_PROVIDER);
        pkCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encSessionKey = pkCipher.doFinal(data);
        return encSessionKey;
    }

    /**
     * Encrypts given data using session key
     * @param skey Session key
     * @param data Data to encrypt
     * @return Encrypted data
     * @throws InvalidCipherTextException
     */
    public byte[] encryptUsingSessionKey(byte[] skey, byte[] data) throws InvalidCipherTextException {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine(), new PKCS7Padding());

        cipher.init(true, new KeyParameter(skey));

        int outputSize = cipher.getOutputSize(data.length);

        byte[] tempOP = new byte[outputSize];
        int processLen = cipher.processBytes(data, 0, data.length, tempOP, 0);
        int outputLen = cipher.doFinal(tempOP, processLen);

        byte[] result = new byte[processLen + outputLen];
        System.arraycopy(tempOP, 0, result, 0, result.length);
        return result;

    }

    /**
     * Returns UIDAI certificate's expiry date in YYYYMMDD format using GMT time zone.
     * It can be used as certificate identifier
     * @return Certificate identifier for UIDAI public certificate
     */
    public String getCertificateIdentifier() {
        SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
        ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String certificateIdentifier = ciDateFormat.format(this.certExpiryDate);
        return certificateIdentifier;
    }
}

