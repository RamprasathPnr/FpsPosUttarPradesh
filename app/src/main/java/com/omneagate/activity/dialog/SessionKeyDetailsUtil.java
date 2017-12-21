package com.omneagate.activity.dialog;

import java.io.Serializable;

/**
 * Created by AJITH on 16-09-2015.
 */
public class SessionKeyDetailsUtil implements Serializable{

    /**
     * Flag indicating whether synchronized key being used.
     */
    boolean isSynchronizedKeySchemeUsed;

    /**
     * Flag indicating whether this session key represents initialize of synchronized key,
     * in which case a seed key will be used along with key identifier.
     */
    boolean isSynchronizedKeyBeingInitialized;

    /**
     * Seed skey for synchronized key scheme.  It is a RSA2048 encrypted AES key, that is encrypted using UIDAI public key.
     */
    byte[] seedSkeyForSynchronizedKey;

    /**
     * Random number for synchronized key scheme
     */
    byte[] randomNumberForSynchornizedKey;

    /**
     * Key identifier for synchronized key scheme
     */
    String keyIdentifier;

    /**
     * Skey value when not using synchronized key.  It is a RSA2048 encrypted AES key, that is encrypted using UIDAI public key.
     */
    byte[] normalSkey;

    private SessionKeyDetailsUtil() {

    }

    public static SessionKeyDetailsUtil createSkeyToInitializeSynchronizedKey(String ki, byte[] encyprtedSeedKey) {
        SessionKeyDetailsUtil d = new SessionKeyDetailsUtil();

        d.setSynchronizedKeySchemeUsed(true);
        d.setKeyIdentifier(ki);
        d.setSynchornizedKeyBeingInitialized(true);
        d.setSeedSkeyForSynchronizedKey(encyprtedSeedKey);

        return d;

    }

    public static SessionKeyDetailsUtil createSkeyToUsePreviouslyGeneratedSynchronizedKey(String ki, byte[] synchronizedKeyRandom) {
        SessionKeyDetailsUtil d = new SessionKeyDetailsUtil();
        d.setSynchronizedKeySchemeUsed(true);
        d.setKeyIdentifier(ki);
        d.setSynchornizedKeyBeingInitialized(false);
        d.setRandomNumberForSynchornizedKey(synchronizedKeyRandom);
        return d;
    }

    public static SessionKeyDetailsUtil createNormalSkey(byte[] encyprtedSeedKey) {
        SessionKeyDetailsUtil d = new SessionKeyDetailsUtil();
        d.setSynchronizedKeySchemeUsed(false);
        d.setNormalSkey(encyprtedSeedKey);
        return d;
    }

    public String getKeyIdentifier() {
        if (isSynchronizedKeySchemeUsed) {
            return this.keyIdentifier;
        } else {
            return null;
        }
    }

    public byte[] getSkeyValue() {
        if (isSynchronizedKeySchemeUsed) {
            if (isSynchronizedKeyBeingInitialized) {
                return this.seedSkeyForSynchronizedKey;
            } else {
                return this.randomNumberForSynchornizedKey;
            }
        } else {
            return this.normalSkey;
        }
    }

    public void setKeyIdentifier(String ki) {
        this.keyIdentifier = ki;
    }

    public void setSeedSkeyForSynchronizedKey(byte[] seedSkey) {
        this.seedSkeyForSynchronizedKey = seedSkey;
    }

    public void setSynchronizedKeySchemeUsed(boolean isSSK) {
        this.isSynchronizedKeySchemeUsed = isSSK;
    }

    public void setSynchornizedKeyBeingInitialized(boolean sskInit) {
        this.isSynchronizedKeyBeingInitialized = sskInit;
    }

    public void setRandomNumberForSynchornizedKey(byte[] sskRandom) {
        this.randomNumberForSynchornizedKey = sskRandom;
    }

    public void setNormalSkey(byte[] normalSkey) {
        this.normalSkey = normalSkey;
    }
}
