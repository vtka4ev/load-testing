/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */

package com.alertme.zoo.shared.awsloadtesting.scrty;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * Class for reading RSA or ECC private key from PEM file.
 * 
 * It can read PEM files with PKCS#8 or PKCS#1 encodings. It doesn't support
 * encrypted PEM files.
 */
public class PrivateKeyReader {

    // Private key file using PKCS #1 encoding
    public static final String P1_BEGIN_MARKER = "-----BEGIN RSA PRIVATE KEY"; //$NON-NLS-1$
    public static final String P1_END_MARKER = "-----END RSA PRIVATE KEY"; //$NON-NLS-1$

    // Private key file using PKCS #8 encoding
    public static final String P8_BEGIN_MARKER = "-----BEGIN PRIVATE KEY"; //$NON-NLS-1$
    public static final String P8_END_MARKER = "-----END PRIVATE KEY"; //$NON-NLS-1$


    private PrivateKeyReader() {
    }
    /**
     * Get a RSA Private Key from InputStream.
     *
     * @param fileName
     *            file name
     * @return Private key
     * @throws IOException
     *             IOException resulted from invalid file IO
     * @throws GeneralSecurityException
     *             GeneralSecurityException resulted from invalid key format
     */
    public static PrivateKey getPrivateKey(final String fileName) throws IOException, GeneralSecurityException {
        try (InputStream stream = new FileInputStream(fileName)) {
            return getPrivateKey(stream, null);
        }
    }

    /**
     * Get a Private Key from InputStream.
     *
     * @param fileName
     *            file name
     * @param algorithm
     *            the name of the key algorithm, for example "RSA" or "EC"
     * @return Private key
     * @throws IOException
     *             IOException resulted from invalid file IO
     * @throws GeneralSecurityException
     *             GeneralSecurityException resulted from invalid key data
     */
    public static PrivateKey getPrivateKey(final String fileName, final String algorithm) throws IOException,
            GeneralSecurityException {
        try (final InputStream stream = new FileInputStream(fileName)) {
            return getPrivateKey(stream, algorithm);
        }
    }

    /**
     * Get a Private Key for the file.
     *
     * @param stream
     *            InputStream object
     * @param algorithm
     *            the name of the key algorithm, for example "RSA" or "EC"
     * @return Private key
     * @throws IOException
     *             IOException resulted from invalid file IO
     * @throws GeneralSecurityException
     *             GeneralSecurityException resulted from invalid key data
     */
    public static PrivateKey getPrivateKey(final InputStream stream, final String algorithm) throws IOException,
            GeneralSecurityException {
        PrivateKey key = null;
        boolean isRSAKey = false;

        final BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        final StringBuilder builder = new StringBuilder();
        boolean inKey = false;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (!inKey) {
                if (line.startsWith("-----BEGIN ") && line.endsWith(" PRIVATE KEY-----")) {
                    inKey = true;
                    isRSAKey = line.contains("RSA");
                }
                continue;
            } else {
                if (line.startsWith("-----END ") && line.endsWith(" PRIVATE KEY-----")) {
                    inKey = false;
                    isRSAKey = line.contains("RSA");
                    break;
                }
                builder.append(line);
            }
        }
        KeySpec keySpec = null;
        final byte[] encoded = DatatypeConverter.parseBase64Binary(builder.toString());
        if (isRSAKey) {
            keySpec = getRSAKeySpec(encoded);
        } else {
            keySpec = new PKCS8EncodedKeySpec(encoded);
        }
        final KeyFactory kf = KeyFactory.getInstance((algorithm == null) ? "RSA" : algorithm);
        key = kf.generatePrivate(keySpec);

        return key;
    }

    /**
     * Convert PKCS#1 encoded private key into RSAPrivateCrtKeySpec.
     * 
     * <p/>
     * The ASN.1 syntax for the private key with CRT is
     * 
     * <pre>
     * -- 
     * -- Representation of RSA private key with information for the CRT algorithm.
     * --
     * RSAPrivateKey ::= SEQUENCE {
     *   version           Version, 
     *   modulus           INTEGER,  -- n
     *   publicExponent    INTEGER,  -- e
     *   privateExponent   INTEGER,  -- d
     *   prime1            INTEGER,  -- p
     *   prime2            INTEGER,  -- q
     *   exponent1         INTEGER,  -- d mod (p-1)
     *   exponent2         INTEGER,  -- d mod (q-1) 
     *   coefficient       INTEGER,  -- (inverse of q) mod p
     *   otherPrimeInfos   OtherPrimeInfos OPTIONAL 
     * }
     * </pre>
     * 
     * @param keyBytes
     *            PKCS#1 encoded key
     * @return KeySpec
     * @throws IOException
     *             IOException resulted from invalid file IO
     */
    private static RSAPrivateCrtKeySpec getRSAKeySpec(final byte[] keyBytes) throws IOException {

        DerParser parser = new DerParser(keyBytes);

        final Asn1Object sequence = parser.read();
        if (sequence.getType() != DerParser.SEQUENCE) {
            throw new IOException("Invalid DER: not a sequence"); //$NON-NLS-1$
        }

        // Parse inside the sequence
        parser = sequence.getParser();

        parser.read(); // Skip version
        final BigInteger modulus = parser.read().getInteger();
        final BigInteger publicExp = parser.read().getInteger();
        final BigInteger privateExp = parser.read().getInteger();
        final BigInteger prime1 = parser.read().getInteger();
        final BigInteger prime2 = parser.read().getInteger();
        final BigInteger exp1 = parser.read().getInteger();
        final BigInteger exp2 = parser.read().getInteger();
        final BigInteger crtCoef = parser.read().getInteger();

        final RSAPrivateCrtKeySpec keySpec =
                new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1,
                exp2, crtCoef);

        return keySpec;
    }
}

