package org.knowm.xchange.coinsph.service;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import org.knowm.xchange.service.BaseParamsDigest;
import si.mazi.rescu.RestInvocation;

/**
 * HMAC-SHA256 implementation for Coins.ph authentication
 */
public class CoinsPHHmacDigest extends BaseParamsDigest {

  private CoinsPHHmacDigest(String secretKeyBase64) throws IllegalArgumentException {
    super(secretKeyBase64, HMAC_SHA_256);
  }

  public static CoinsPHHmacDigest createInstance(String secretKeyBase64) {
    return secretKeyBase64 == null ? null : new CoinsPHHmacDigest(secretKeyBase64);
  }

  @Override
  public String digestParams(RestInvocation restInvocation) {
    String queryString = restInvocation.getQueryString();
    String bodyString = restInvocation.getRequestBody();
    String totalParams = "";

    // Combine query string and request body as per Coins.ph documentation
    if (queryString != null && !queryString.isEmpty()) {
      totalParams += queryString;
    }
    
    if (bodyString != null && !bodyString.isEmpty()) {
      totalParams += bodyString;
    }

    Mac mac = getMac();
    byte[] signature = mac.doFinal(totalParams.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(signature);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}