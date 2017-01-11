import com.alertme.zoo.shared.awsloadtesting.scrty.PrivateKeyReader;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class A {
    public static void main(final String[] args) throws Exception {
//        SecurityTools.initDefaultSSLContextForTest();
        final Certificate certificate = loadCertificate();
        final PrivateKey privateKey = loadPrivateKey("RSA");

        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(null);
        keyStore.setCertificateEntry("alias", certificate);
        keyStore.setKeyEntry("alias", privateKey, "".toCharArray(), new Certificate[]{certificate});
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "".toCharArray());

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), trustEverything(), new SecureRandom());


        final MQTT mqtt = new MQTT();
        mqtt.setHost(URI.create("tls://a3gemn1nf8j60e.iot.eu-west-1.amazonaws.com:8883"));
        mqtt.setClientId("ale");
//        mqtt.setVersion("3.1.1");
        mqtt.setSslContext(sslContext);
        final BlockingConnection connection = mqtt.blockingConnection();
        System.out.println("connecting ...");
        connection.connect();
        System.out.println("connected");
        connection.publish("$aws/things/alessio-test/shadow/update", ("{\n" +
                "    \"state\": {\n" +
                "        \"desired\" : {\n" +
                "            \"color\" : { \"AAAA\" : 10 },\n" +
                "            \"engine\" : \"OFF\"\n" +
                "        }\n" +
                "    }\n" +
                "}").getBytes(), QoS.AT_LEAST_ONCE, false);
        connection.disconnect();
        System.out.println("THE END");
    }


    private static TrustManager[] trustEverything() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
                            throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }

    private static Certificate loadCertificate() {
//        log.info("Loading certificate...");
        System.out.println("Loading certificate...");
        final String certString = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDWjCCAkKgAwIBAgIVAN0XI+NNyTNtWRknc6IlKnjr4ycyMA0GCSqGSIb3DQEB\n" +
                "CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t\n" +
                "IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0xNzAxMTAxMjE0\n" +
                "MjJaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh\n" +
                "dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCJQnvwBECnakbIyBuR\n" +
                "dbLUnGblISObSUDQGpyCQ7p+JSqXZ40mxj951aU88Sy4YIPnrA1rf5HSFBBml4NO\n" +
                "ywClMy8kXfex5mWyCVMHEQ7EWc5RLeTkZ3r9t5jPVxk39BQqSHH4ryvWzazjhbx6\n" +
                "gkRGV8JSdgvgx0J/QzWwJdx+aZJHO2uTIYEns1HXlP5CQO06qRGJzbnJDuScuNsv\n" +
                "RIKV0dfPLfYEyvindQaYn8PYhwE6o72K9l3D5neQxmv9rC1ycMIzsxsK9ReiYQxF\n" +
                "VdCESmWRufRcO94WQmXg6ifgqN2e2pCIFSEbAOV5Cj9fZhBvLtKazHL+ttX3NeGj\n" +
                "oeR3AgMBAAGjYDBeMB8GA1UdIwQYMBaAFG6NJgVBa8Z6T8Okg60R8gGVqVDxMB0G\n" +
                "A1UdDgQWBBSYz4YZ81IIxam5Z/axunpHf89EsjAMBgNVHRMBAf8EAjAAMA4GA1Ud\n" +
                "DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAHncGG64cKcD6WLrKFSjhBYCd\n" +
                "eW/LQhcduiP90E0mWaB5gwDa3cZd6ULvY1k6gDqTFiz4xcD6/6zB3kR4HOU5pJ88\n" +
                "IECM3KkuQ7b/dDtMtkgXAHNKmM1lO0PmhhwvezIjD4ya/GqWCtTD11DLu6fx9MbM\n" +
                "vKs64xZjlRCWL6ZyhF9dz8WCUe9qow7HqMWwpSrZlm7eCDPpxqC5s/XpEoSs7SwO\n" +
                "09g1xTJ8+wAt3Z73guvbOnxL/kWYC0yq6DSyLqGlvUwUjZ6b/XDwHVA2VTQ6/piB\n" +
                "yi9BmAF/RFpHG//E0GqatxncKQgdxmBDzDyM7RarA2vXfGDhDpZF4fCiCBLk8Q==\n" +
                "-----END CERTIFICATE-----\n";
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            return certFactory.generateCertificate(new ByteArrayInputStream(certString.getBytes()));
        } catch (final Throwable e) {
//            log.error("certificate parse failed", e);
            System.out.println("certificate parse failed");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static PrivateKey loadPrivateKey(final String algorithm) {
//        log.info("Loading private key ...");
        System.out.println("Loading private key ...");
        final String privateKeyStr = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEogIBAAKCAQEAiUJ78ARAp2pGyMgbkXWy1Jxm5SEjm0lA0BqcgkO6fiUql2eN\n" +
                "JsY/edWlPPEsuGCD56wNa3+R0hQQZpeDTssApTMvJF33seZlsglTBxEOxFnOUS3k\n" +
                "5Gd6/beYz1cZN/QUKkhx+K8r1s2s44W8eoJERlfCUnYL4MdCf0M1sCXcfmmSRztr\n" +
                "kyGBJ7NR15T+QkDtOqkRic25yQ7knLjbL0SCldHXzy32BMr4p3UGmJ/D2IcBOqO9\n" +
                "ivZdw+Z3kMZr/awtcnDCM7MbCvUXomEMRVXQhEplkbn0XDveFkJl4Oon4KjdntqQ\n" +
                "iBUhGwDleQo/X2YQby7Smsxy/rbV9zXho6HkdwIDAQABAoIBACtXWPdDAH9cqZlA\n" +
                "xqO5vIwyDrOPMUUZbmPH41+mrz6h0b8ZLZLuyqBX++MwbGST5VLEG0C0eYESYNNk\n" +
                "SEwbAcsoTFx5Z3s/OyFqnFMA6d7KVMGBcmNE4as3zAK8h/QJGEz5rzNNbNRZAZye\n" +
                "YBN3CsQDdhK/v6RshuzUdiF28xbfHDSVlkdJX1KALqV+v5PbXg9k7vl2hYkvPGSH\n" +
                "srWXTkku+URYzPZRXT88aV6GWp9qXpWnzNXUQmloVSxCasHgvqtstS9pzufR/f1M\n" +
                "JF5KuQIh6miwkXyjjyYMEC8N0LYFPHVw+a2hraIxbOdO83g7tXlS6j+pCXmQmNAR\n" +
                "PxRaO4ECgYEAzZEWLDs2eCoqu7Zm75/eKaC68MY73T0Zrsbr8DdtxhvfVeeiurDt\n" +
                "k8Gdvpe3+kq9yBjZ9qEV0j/Q721qcH+Edyj1DxFYu9mBVHuPPddClsaPGxmDPe/H\n" +
                "XlZa0L2AA/rCXft83aNF/FDkSRyxjsniwGm3YDbRrFpPmaz1QlOjbeECgYEAqu9G\n" +
                "r+5ZvuQnqO/nAtmShCFHSqvMqN2LsXJKBSdEnVh03Vt7SDQ9vihZqUzXJZhQSW+x\n" +
                "vzk79jSOSaiY4iEPIxK873ol8NN5FYZYQXKdMVGGlbnEO0uPi0i1wBm3SXt867mq\n" +
                "QoKxQ93K+aCC0JwOCqiCEPOWgmwqPjyFKq+nLVcCgYB86S+O+wATLpQ+8gxEiWFG\n" +
                "7EsL6XkQ64LCqE9P7W5/1gn0ukcwqDgE8761xJ1fsrD1eNxhN+r5khuUkWj/KQ1G\n" +
                "FxYp7MF9jCJBQr98tWPaGJd2wR71sND1qwWOF8hFIseesiVizEbHliPRpWTjPhvS\n" +
                "DasHBOiNkWcTG30Aq7AAIQKBgAqtk97dpuGT4x5cjjPRX6O9aHSzsr9Bx744A4O6\n" +
                "5kBmDDbfxh3GlazRXHiFAlOo5isQPSxS6PoCYnkbfSFzKzznqMHVAZW/wCqmD9FW\n" +
                "1ZcFHsEvr6B8oeTzj9cGRDrk6fLX0FkDTQuOSWW6rzFU7lOgHy/r55USKLlmknMO\n" +
                "MgVVAoGAB5xxzBG4y7pdzgH5ARijsC6dhnGDw6o35LsunhzZ+odXNFEVDnU3tZ/A\n" +
                "WSMWNzKRKVzVCJVU9KraDqKmlZMWkGdc8J1cBVIfKYRA4yhAe+njTm9wQcxI16b5\n" +
                "YjoUAyBGt852QvEs4mMoJykcngEogtg++eH8HF9XQk3OAMc4LiU=\n" +
                "-----END RSA PRIVATE KEY-----\n";
        try {
            return PrivateKeyReader
                    .getPrivateKey(new ByteArrayInputStream(privateKeyStr.getBytes()), algorithm);
        } catch (final Throwable e) {
//            log.error("Error reading private key.", e);
            System.out.println("Error reading private key.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
