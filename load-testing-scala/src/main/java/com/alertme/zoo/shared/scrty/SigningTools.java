/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.scrty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SigningTools {

    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }

    public static void main(String[] args) throws Exception {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()).toString();
        final byte[] signatureKey = getSignatureKey("TSmqlNynWF+jd+owYJNQyHJUAIGdPDXaPe0FWsfX", timestamp, "eu-west-1", "iot");
        System.out.println(new String(signatureKey));
    }


}
