/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.awsloadtesting.scrty;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Properties;

public class SecurityTools {

    private static final String TLS_V_1_2 = "TLSv1.2";
    private static final String PROPERTY_FILE = "aws-iot-sdk-samples.properties";

    private SecurityTools() {
    }

    public static String getConfig(final String name) {
        final Properties prop = new Properties();
        final URL resource = SecurityTools.class.getResource(PROPERTY_FILE);
        if (resource == null) {
            return null;
        }
        try (final InputStream stream = resource.openStream()) {
            prop.load(stream);
        } catch (IOException e) {
            return null;
        }
        final String value = prop.getProperty(name);
        if (value == null || "".equals(value)) {
            return null;
        } else {
            return value;
        }
    }

    private static Certificate loadCertificateFromFile(final String filename) {
        Certificate certificate = null;

        final File file = new File(filename);
        if (!file.exists()) {
//            System.out.println("Certificate file not found: " + filename);
            return null;
        }
        try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            certificate = certFactory.generateCertificate(stream);
        } catch (IOException | CertificateException e) {
//            System.out.println("Failed to load certificate file " + filename);
        }

        return certificate;
    }

    private static PrivateKey loadPrivateKeyFromFile(final String filename, final String algorithm) {
        PrivateKey privateKey = null;

        final File file = new File(filename);
        if (!file.exists()) {
//            System.out.println("Private key file not found: " + filename);
            return null;
        }
        try (final DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            privateKey = PrivateKeyReader.getPrivateKey(stream, algorithm);
        } catch (IOException | GeneralSecurityException e) {
            //System.out.println("Failed to load private key from file " + filename);
        }

        return privateKey;
    }

    public static void initDefaultSSLContextFromFiles() {
        final String certificateFile = getConfig("certificateFile");
        final String privateKeyFile = getConfig("privateKeyFile");
        final Certificate certificate = loadCertificateFromFile(certificateFile);
        final PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile, "RSA");
        initDefaultSSLContext(certificate, privateKey);
    }

    public static void initDefaultSSLContext(final Certificate certificate, final PrivateKey privateKey) {
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(null);
            keyStore.setCertificateEntry("alias", certificate);
            keyStore.setKeyEntry("alias", privateKey, "changeit".toCharArray(), new Certificate[]{certificate});

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "changeit".toCharArray());
            final SSLContext context = SSLContext.getInstance(TLS_V_1_2);
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLContext.setDefault(context);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
