package com.alertme.zoo.shared.load_testing.simulation

import java.io.ByteArrayInputStream
import java.security.cert.{Certificate, CertificateException, CertificateFactory, X509Certificate}
import java.security.{KeyStore, PrivateKey, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}

import com.alertme.zoo.shared.awsloadtesting.scrty.PrivateKeyReader

object SecurityTool {

  private val TLS_V_1_2: String = "SSL" //"TLSv1.2";

  private def loadCertificate: Certificate = {
    //        log.info("Loading certificate...");
    System.out.println("Loading certificate...")
    val certString: String =
      """-----BEGIN CERTIFICATE-----
        |MIIDWjCCAkKgAwIBAgIVAN0XI+NNyTNtWRknc6IlKnjr4ycyMA0GCSqGSIb3DQEB
        |CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t
        |IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0xNzAxMTAxMjE0
        |MjJaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh
        |dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCJQnvwBECnakbIyBuR
        |dbLUnGblISObSUDQGpyCQ7p+JSqXZ40mxj951aU88Sy4YIPnrA1rf5HSFBBml4NO
        |ywClMy8kXfex5mWyCVMHEQ7EWc5RLeTkZ3r9t5jPVxk39BQqSHH4ryvWzazjhbx6
        |gkRGV8JSdgvgx0J/QzWwJdx+aZJHO2uTIYEns1HXlP5CQO06qRGJzbnJDuScuNsv
        |RIKV0dfPLfYEyvindQaYn8PYhwE6o72K9l3D5neQxmv9rC1ycMIzsxsK9ReiYQxF
        |VdCESmWRufRcO94WQmXg6ifgqN2e2pCIFSEbAOV5Cj9fZhBvLtKazHL+ttX3NeGj
        |oeR3AgMBAAGjYDBeMB8GA1UdIwQYMBaAFG6NJgVBa8Z6T8Okg60R8gGVqVDxMB0G
        |A1UdDgQWBBSYz4YZ81IIxam5Z/axunpHf89EsjAMBgNVHRMBAf8EAjAAMA4GA1Ud
        |DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAHncGG64cKcD6WLrKFSjhBYCd
        |eW/LQhcduiP90E0mWaB5gwDa3cZd6ULvY1k6gDqTFiz4xcD6/6zB3kR4HOU5pJ88
        |IECM3KkuQ7b/dDtMtkgXAHNKmM1lO0PmhhwvezIjD4ya/GqWCtTD11DLu6fx9MbM
        |vKs64xZjlRCWL6ZyhF9dz8WCUe9qow7HqMWwpSrZlm7eCDPpxqC5s/XpEoSs7SwO
        |09g1xTJ8+wAt3Z73guvbOnxL/kWYC0yq6DSyLqGlvUwUjZ6b/XDwHVA2VTQ6/piB
        |yi9BmAF/RFpHG//E0GqatxncKQgdxmBDzDyM7RarA2vXfGDhDpZF4fCiCBLk8Q==
        |-----END CERTIFICATE-----"""
    try {
      val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
      return certFactory.generateCertificate(new ByteArrayInputStream(certString.getBytes))
    }
    catch {
      case e: Throwable => {
        //            log.error("certificate parse failed", e);
        System.out.println("certificate parse failed")
        e.printStackTrace()
        throw new RuntimeException(e)
      }
    }
  }

  private def loadPrivateKey(algorithm: String): PrivateKey = {
    //        log.info("Loading private key ...");
    System.out.println("Loading private key ...")
    //    val privateKeyStr: String = "-----BEGIN RSA PRIVATE KEY-----\n" + "MIIEogIBAAKCAQEAiUJ78ARAp2pGyMgbkXWy1Jxm5SEjm0lA0BqcgkO6fiUql2eN\n" + "JsY/edWlPPEsuGCD56wNa3+R0hQQZpeDTssApTMvJF33seZlsglTBxEOxFnOUS3k\n" + "5Gd6/beYz1cZN/QUKkhx+K8r1s2s44W8eoJERlfCUnYL4MdCf0M1sCXcfmmSRztr\n" + "kyGBJ7NR15T+QkDtOqkRic25yQ7knLjbL0SCldHXzy32BMr4p3UGmJ/D2IcBOqO9\n" + "ivZdw+Z3kMZr/awtcnDCM7MbCvUXomEMRVXQhEplkbn0XDveFkJl4Oon4KjdntqQ\n" + "iBUhGwDleQo/X2YQby7Smsxy/rbV9zXho6HkdwIDAQABAoIBACtXWPdDAH9cqZlA\n" + "xqO5vIwyDrOPMUUZbmPH41+mrz6h0b8ZLZLuyqBX++MwbGST5VLEG0C0eYESYNNk\n" + "SEwbAcsoTFx5Z3s/OyFqnFMA6d7KVMGBcmNE4as3zAK8h/QJGEz5rzNNbNRZAZye\n" + "YBN3CsQDdhK/v6RshuzUdiF28xbfHDSVlkdJX1KALqV+v5PbXg9k7vl2hYkvPGSH\n" + "srWXTkku+URYzPZRXT88aV6GWp9qXpWnzNXUQmloVSxCasHgvqtstS9pzufR/f1M\n" + "JF5KuQIh6miwkXyjjyYMEC8N0LYFPHVw+a2hraIxbOdO83g7tXlS6j+pCXmQmNAR\n" + "PxRaO4ECgYEAzZEWLDs2eCoqu7Zm75/eKaC68MY73T0Zrsbr8DdtxhvfVeeiurDt\n" + "k8Gdvpe3+kq9yBjZ9qEV0j/Q721qcH+Edyj1DxFYu9mBVHuPPddClsaPGxmDPe/H\n" + "XlZa0L2AA/rCXft83aNF/FDkSRyxjsniwGm3YDbRrFpPmaz1QlOjbeECgYEAqu9G\n" + "r+5ZvuQnqO/nAtmShCFHSqvMqN2LsXJKBSdEnVh03Vt7SDQ9vihZqUzXJZhQSW+x\n" + "vzk79jSOSaiY4iEPIxK873ol8NN5FYZYQXKdMVGGlbnEO0uPi0i1wBm3SXt867mq\n" + "QoKxQ93K+aCC0JwOCqiCEPOWgmwqPjyFKq+nLVcCgYB86S+O+wATLpQ+8gxEiWFG\n" + "7EsL6XkQ64LCqE9P7W5/1gn0ukcwqDgE8761xJ1fsrD1eNxhN+r5khuUkWj/KQ1G\n" + "FxYp7MF9jCJBQr98tWPaGJd2wR71sND1qwWOF8hFIseesiVizEbHliPRpWTjPhvS\n" + "DasHBOiNkWcTG30Aq7AAIQKBgAqtk97dpuGT4x5cjjPRX6O9aHSzsr9Bx744A4O6\n" + "5kBmDDbfxh3GlazRXHiFAlOo5isQPSxS6PoCYnkbfSFzKzznqMHVAZW/wCqmD9FW\n" + "1ZcFHsEvr6B8oeTzj9cGRDrk6fLX0FkDTQuOSWW6rzFU7lOgHy/r55USKLlmknMO\n" + "MgVVAoGAB5xxzBG4y7pdzgH5ARijsC6dhnGDw6o35LsunhzZ+odXNFEVDnU3tZ/A\n" + "WSMWNzKRKVzVCJVU9KraDqKmlZMWkGdc8J1cBVIfKYRA4yhAe+njTm9wQcxI16b5\n" + "YjoUAyBGt852QvEs4mMoJykcngEogtg++eH8HF9XQk3OAMc4LiU=\n" + "-----END RSA PRIVATE KEY-----\n"
    val privateKeyStr: String =
      """-----BEGIN RSA PRIVATE KEY-----
        |MIIEogIBAAKCAQEAiUJ78ARAp2pGyMgbkXWy1Jxm5SEjm0lA0BqcgkO6fiUql2eN
        |JsY/edWlPPEsuGCD56wNa3+R0hQQZpeDTssApTMvJF33seZlsglTBxEOxFnOUS3k
        |5Gd6/beYz1cZN/QUKkhx+K8r1s2s44W8eoJERlfCUnYL4MdCf0M1sCXcfmmSRztr
        |kyGBJ7NR15T+QkDtOqkRic25yQ7knLjbL0SCldHXzy32BMr4p3UGmJ/D2IcBOqO9
        |ivZdw+Z3kMZr/awtcnDCM7MbCvUXomEMRVXQhEplkbn0XDveFkJl4Oon4KjdntqQ
        |iBUhGwDleQo/X2YQby7Smsxy/rbV9zXho6HkdwIDAQABAoIBACtXWPdDAH9cqZlA
        |xqO5vIwyDrOPMUUZbmPH41+mrz6h0b8ZLZLuyqBX++MwbGST5VLEG0C0eYESYNNk
        |SEwbAcsoTFx5Z3s/OyFqnFMA6d7KVMGBcmNE4as3zAK8h/QJGEz5rzNNbNRZAZye
        |YBN3CsQDdhK/v6RshuzUdiF28xbfHDSVlkdJX1KALqV+v5PbXg9k7vl2hYkvPGSH
        |srWXTkku+URYzPZRXT88aV6GWp9qXpWnzNXUQmloVSxCasHgvqtstS9pzufR/f1M
        |JF5KuQIh6miwkXyjjyYMEC8N0LYFPHVw+a2hraIxbOdO83g7tXlS6j+pCXmQmNAR
        |PxRaO4ECgYEAzZEWLDs2eCoqu7Zm75/eKaC68MY73T0Zrsbr8DdtxhvfVeeiurDt
        |k8Gdvpe3+kq9yBjZ9qEV0j/Q721qcH+Edyj1DxFYu9mBVHuPPddClsaPGxmDPe/H
        |XlZa0L2AA/rCXft83aNF/FDkSRyxjsniwGm3YDbRrFpPmaz1QlOjbeECgYEAqu9G
        |r+5ZvuQnqO/nAtmShCFHSqvMqN2LsXJKBSdEnVh03Vt7SDQ9vihZqUzXJZhQSW+x
        |vzk79jSOSaiY4iEPIxK873ol8NN5FYZYQXKdMVGGlbnEO0uPi0i1wBm3SXt867mq
        |QoKxQ93K+aCC0JwOCqiCEPOWgmwqPjyFKq+nLVcCgYB86S+O+wATLpQ+8gxEiWFG
        |7EsL6XkQ64LCqE9P7W5/1gn0ukcwqDgE8761xJ1fsrD1eNxhN+r5khuUkWj/KQ1G
        |FxYp7MF9jCJBQr98tWPaGJd2wR71sND1qwWOF8hFIseesiVizEbHliPRpWTjPhvS
        |DasHBOiNkWcTG30Aq7AAIQKBgAqtk97dpuGT4x5cjjPRX6O9aHSzsr9Bx744A4O6
        |5kBmDDbfxh3GlazRXHiFAlOo5isQPSxS6PoCYnkbfSFzKzznqMHVAZW/wCqmD9FW
        |1ZcFHsEvr6B8oeTzj9cGRDrk6fLX0FkDTQuOSWW6rzFU7lOgHy/r55USKLlmknMO
        |MgVVAoGAB5xxzBG4y7pdzgH5ARijsC6dhnGDw6o35LsunhzZ+odXNFEVDnU3tZ/A
        |WSMWNzKRKVzVCJVU9KraDqKmlZMWkGdc8J1cBVIfKYRA4yhAe+njTm9wQcxI16b5
        |YjoUAyBGt852QvEs4mMoJykcngEogtg++eH8HF9XQk3OAMc4LiU=
        |-----END RSA PRIVATE KEY-----"""
    try {
      return PrivateKeyReader.getPrivateKey(new ByteArrayInputStream(privateKeyStr.getBytes), algorithm)
    }
    catch {
      case e: Throwable => {
        //            log.error("Error reading private key.", e);
        System.out.println("Error reading private key.")
        e.printStackTrace()
        throw new RuntimeException(e)
      }
    }
  }

  def initDefaultSSLContextForTest() {
    //log.error("Initializing SSL context from files...");
    System.out.println("Initializing SSL context from files...")
    val certificate: Certificate = loadCertificate
    val privateKey: PrivateKey = loadPrivateKey("RSA")
    initDefaultSSLContext(certificate, privateKey)
  }

  def main(args: Array[String]) {
    initDefaultSSLContextForTest()
  }

  def initDefaultSSLContext(certificate: Certificate, privateKey: PrivateKey): SSLContext = {
    try {
      val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      keyStore.load(null)
      keyStore.setCertificateEntry("alias", certificate)
      keyStore.setKeyEntry("alias", privateKey, "".toCharArray, Array[Certificate](certificate))
      val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(keyStore, "".toCharArray)
      val context: SSLContext = SSLContext.getInstance("TLS")
      context.init(kmf.getKeyManagers, trustEverything, new SecureRandom)
      SSLContext.setDefault(context)
      return context
    }
    catch {
      case ex: Throwable => {
        ex.printStackTrace()
        throw new RuntimeException(ex)
      }
    }
  }

  private def trustEverything: Array[TrustManager] = {
    return Array[TrustManager](new X509TrustManager() {
      @throws[CertificateException]
      def checkClientTrusted(chain: Array[X509Certificate], authType: String) {
      }

      @throws[CertificateException]
      def checkServerTrusted(chain: Array[X509Certificate], authType: String) {
      }

      def getAcceptedIssuers: Array[X509Certificate] = new Array[X509Certificate](0)
    })
  }
}
