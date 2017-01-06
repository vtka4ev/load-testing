/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.awsloadtesting.scrty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class SecurityTools {

    private static final String TLS_V_1_2 = "TLSv1.2";
    private final static Logger log = LoggerFactory.getLogger(SecurityTools.class);

    private SecurityTools() {
    }

    private static Certificate loadCertificate() {
        log.info("Loading certificate...");
        final String certString = "-----BEGIN CERTIFICATE-----\n"
                + "MIIDWTCCAkGgAwIBAgIUZUXbf0zOUhd1uzSvR5vXb7Z5TWowDQYJKoZIhvcNAQEL\n"
                + "BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g\n"
                + "SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTE2MTIwOTEyMzgw\n"
                + "M1oXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0\n"
                + "ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKShwwR3yhBzHYWJ7ivq\n"
                + "f+Hs4ZO4gvpDoO+0blx4Ua34T65z28724aT26RsOrtYAonp3T7396uHewtxNVRQ/\n"
                + "BF4XsM8cnAUxZ2cXOitScPEM3gsauwHpuOKMKKhzG83HYo6lLG/JVgrfMZcDP1y2\n"
                + "aTeM4IpKEKneBj77FsDIPh7WCyroyZ+2cQWQrzF6Y+QvUn7k1B/pa77Sx3uQQk0N\n"
                + "3tLVlMQJIsivo1Hky3fd63eMvOH2SEqgXkX9+EAcx/mSfip7qJ9UEXgSmPUXYGNi\n"
                + "0bx5w9auADI7rmHYQpWYSdPki4f4a91NQQBMFUVWu0m8XIVJ45y90mGi+jLxtRub\n"
                + "pNMCAwEAAaNgMF4wHwYDVR0jBBgwFoAUkTZAWgXA3FWOCz+f31H4qbF+/pgwHQYD\n"
                + "VR0OBBYEFAVk4IVeKDXasU1EJsTuTXu6ur4sMAwGA1UdEwEB/wQCMAAwDgYDVR0P\n"
                + "AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQCVz31WY5XY83EoyltnsW1PAF25\n"
                + "7HwSg9hfNbHwzsDjlJHfmz4Qz7/7vHUlZG7JZmhV1FtV3Y4qUAukeUAI38DnAACb\n"
                + "6fAXTxbda+srJaL51HTRpZ+hHodd0XiXsq7VfWoflo7wBH1nc+TUlzoHm4DYvMWM\n"
                + "DrPm6PQ/6E+/1oLx2XeyD+wKaRDs0gCkFn0X+DN1T4ff89Td1qc8H3TXdFwgxso3\n"
                + "2SZXYEunCtoDeXpMxMZcFdBl5IY5iBI2/h0yt+y66usRiuWCjM/sjBC6GYFvO/h0\n"
                + "wZnkU7s9y0OKfCcyIh+HIrtkMpbrl5FaS1lnkURuI3TlOBR+JFh9g1eQYCyA\n"
                + "-----END CERTIFICATE-----";
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            return certFactory.generateCertificate(new ByteArrayInputStream(certString.getBytes()));
        } catch (CertificateException e) {
            log.error("certificate parse failed", e);
            return null;
        }
    }

    private static PrivateKey loadPrivateKey(final String algorithm) {
        log.info("Loading private key ...");
        final String privateKeyStr = "-----BEGIN RSA PRIVATE KEY-----\n"
                + "MIIEowIBAAKCAQEAmj71rX0hK4yguxq4q1VK54cEi8v/8IPULE26mHGkHGexA4Mk\n"
                + "vCsTStQgHQzPFTlsv7jLkwsPdTx1Y8btxZP8FH3cxzrvs2CAetF9sA6alu3om4p0\n"
                + "52n7az4rKOxVPU9bNExg1aW5Gevzzaa1NNVlARTj3RlokxYBvcFulRohVnc9Os5A\n"
                + "cZe09AvbAB2wiNNvH/yQWaojP753xwsz0VkDRIXdR3h4ymyM3Oi6ORcLxCjvzXbZ\n"
                + "AY1v6qnhU1tbBWaudh4w1HxlyIBtHVXnARCdZ05uJ5S6OIF+Ic1SNM8JmB8TRUQ6\n"
                + "3HC4SrvivlK8IOd5Q6prttRdVHv7XEVX08m5AwIDAQABAoIBAFPu2qUzJ++YsIXf\n"
                + "/olM+luOfwzLT1RDiBsrmNQHUrQaQJqWrFCwZ+kaEPd91tnHy8Nv/WbgZ+L1qTNI\n"
                + "Nm6DHnLQImlPSswjQQaUJH6/E6P5hc/NwduDMkqjI707DQ81tX156l+XtGfEm4BW\n"
                + "rNg5HqM+CNhxURRorz3gmhoMhAOWOZRGJyzR9ReL8V5xVYxvUJsODhKmnEl1lX7z\n"
                + "82s5hC/sWx6vsrYb/BavmQhg8mbaa6XoyhqkqSaJCdg9+lndQijTXCSEQnWIipIo\n"
                + "w2UFHNQJkTjZUiNkCI57jNhIiy4Xal9H+2yroUanGa0tVv2+7TLZ/ob1uydbc3qV\n"
                + "bTmQ+vECgYEA3ENkg5Rn6Ul4v48d88wYC/Hd/GOlcFAxg+467Grg559sSpAqtUcE\n"
                + "4fVE2umMzABeP9jtCNVh07OLwERlCa91jZ1Q5Esz6xd/eG6gda603EyFUkXpBndt\n"
                + "xvNiSeDbSgI4uE08+JZep8CF27mRxzWMlx2CY8o4xP+DeQbDkW93q98CgYEAs0WL\n"
                + "vTdIgS+HDhnYyWbbQKYcVIbRfr9a4fuiIf3meYAyziWH9QDaAWasW/y6mREgiegh\n"
                + "d101XDBTUbSHUcDRf+dzFj0oyUwueyyJ1/YrGXa9TGQSVVoJE88hOllXJVfx7HyR\n"
                + "3u7NEK5yCZJ27kOmuYjd3fdYUTa7AxK51ysI110CgYAcIXdK3r9OqhWD0ZFvu5cu\n"
                + "n1tMiqVsbLGGOfzIiPXkXxYDh9oMgN98xEhg9QcIXtuqp9fOEwKFeR7WFWYaEJCg\n"
                + "34CfR4N/+OZMyUQxA3kR0awNT+Rs8P/SMu9QpCkdkJ8R2rt4vCumnQ37e/3ERXCJ\n"
                + "NDmc6QzLDB8Ma/K6NlRAXQKBgCKZtKvThLnyW2W8VVwh7wVeSi+CSeLlufvN3nAj\n"
                + "Gh2vQZ8KHWCLRohosbGbaMRsStRzKipoogjmBt7JMij0Rzshh9Pt//ZCLuJ1KTG2\n"
                + "gIEMquKYmfVBSGk7XBVv8uLxQ286Z8kYXBnxIW95hlzcT8yVfwT9XV1na9bfAWFn\n"
                + "G/C9AoGBAMQ+Wje1pDW6nAcQWVCVoSdrekZFfILJQELO53+PBGQWf1CHpPRuQvJ0\n"
                + "nP9ksHJzOj27TW8cNJ/RoXMAlRAJjzwrD4DOMVUvWXDgLw6dYBJfwsm8g3Kx5Ghf\n"
                + "Y5/4/cjG3/nxxWWWorwbjgdKFnjmQOku+6FD11+qAvJ5rdtQMiRa\n"
                + "-----END RSA PRIVATE KEY-----";
        try {
            return PrivateKeyReader
                    .getPrivateKey(new ByteArrayInputStream(privateKeyStr.getBytes()), algorithm);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error reading private key.");
            return null;
        }
    }

    public static void initDefaultSSLContextForTest() {
        log.error("Initializing SSL context from files...");
        final Certificate certificate = loadCertificate();
        final PrivateKey privateKey = loadPrivateKey("RSA");
        initDefaultSSLContext(certificate, privateKey);
    }

    public static void initDefaultSSLContext(final Certificate certificate, final PrivateKey privateKey) {
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(null);
            keyStore.setCertificateEntry("alias", certificate);
            keyStore.setKeyEntry("alias", privateKey, "changeit".toCharArray(), new Certificate[]{certificate});

            final TrustManagerFactory tmf =  TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
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
