/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.scrty;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Properties;

public class SecurityTools {

    private static final String TLS_V_1_2 = "TLSv1.2";
    private static final String PropertyFile = "aws-iot-sdk-samples.properties";

    public static class KeyStorePasswordPair {
        public KeyStore keyStore;
        public String keyPassword;

        public KeyStorePasswordPair(KeyStore keyStore, String keyPassword) {
            this.keyStore = keyStore;
            this.keyPassword = keyPassword;
        }
    }

    public static String getConfig(String name) {
        Properties prop = new Properties();
        URL resource = SecurityTools.class.getResource(PropertyFile);
        if (resource == null) {
            return null;
        }
        try (InputStream stream = resource.openStream()) {
            prop.load(stream);
        } catch (IOException e) {
            return null;
        }
        String value = prop.getProperty(name);
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(String certificateFile, String privateKeyFile) {
        return getKeyStorePasswordPair(certificateFile, privateKeyFile, null);
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(String certificateFile, String privateKeyFile,
                                                               String keyAlgorithm) {
        if (certificateFile == null || privateKeyFile == null) {
            System.out.println("Certificate or private key file missing");
            return null;
        }

        java.security.cert.Certificate certificate = loadCertificateFromFile(certificateFile);
        PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile, keyAlgorithm);
        if (certificate == null || privateKey == null) {
            return null;
        }

        return getKeyStorePasswordPair(certificate, privateKey);
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(java.security.cert.Certificate certificate, PrivateKey privateKey) {
        KeyStore keyStore = null;
        String keyPassword = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("alias", certificate);

            // randomly generated key password for the key in the KeyStore
            keyPassword = new BigInteger(128, new SecureRandom()).toString(32);
            keyStore.setKeyEntry("alias", privateKey, keyPassword.toCharArray(), new java.security.cert.Certificate[]{certificate});
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            System.out.println("Failed to create key store");
            return null;
        }

        return new KeyStorePasswordPair(keyStore, keyPassword);
    }

    private static java.security.cert.Certificate loadCertificateFromFile(String filename) {
        java.security.cert.Certificate certificate = null;

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Certificate file not found: " + filename);
            return null;
        }
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            certificate = certFactory.generateCertificate(stream);
        } catch (IOException | CertificateException e) {
            System.out.println("Failed to load certificate file " + filename);
        }

        return certificate;
    }

    private static PrivateKey loadPrivateKeyFromFile(String filename, String algorithm) {
        PrivateKey privateKey = null;

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Private key file not found: " + filename);
            return null;
        }
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            privateKey = PrivateKeyReader.getPrivateKey(stream, algorithm);
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("Failed to load private key from file " + filename);
        }

        return privateKey;
    }

    public static void initDefaultSSLContextFromFiles() {
        String certificateFile = getConfig("certificateFile");
        String privateKeyFile = getConfig("privateKeyFile");
        Certificate certificate = loadCertificateFromFile(certificateFile);
        PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile, "RSA");
        initDefaultSSLContext(certificate, privateKey);
    }

    public static void initDefaultSSLContext(Certificate certificate, PrivateKey privateKey) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

//        final InputStream resourceAsStream = SecurityTools.class.getClassLoader().getResourceAsStream("resources/.keystore");
//        keyStore.load(resourceAsStream, "changeit".toCharArray());
            keyStore.load(null);
            keyStore.setCertificateEntry("alias", certificate);
            keyStore.setKeyEntry("alias", privateKey, "changeit".toCharArray(), new Certificate[]{certificate});

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "changeit".toCharArray());
            SSLContext context = SSLContext.getInstance(TLS_V_1_2);
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLContext.setDefault(context);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
