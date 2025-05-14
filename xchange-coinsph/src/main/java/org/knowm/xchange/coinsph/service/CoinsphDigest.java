package org.knowm.xchange.coinsph.service;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.knowm.xchange.exceptions.ExchangeException;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;
import org.knowm.xchange.utils.DigestUtils;

public class CoinsphDigest implements ParamsDigest {

  private static final String HMAC_SHA_256 = "HmacSHA256";
  private final Mac mac;

  private CoinsphDigest(String secretKey) {
    try {
      mac = Mac.getInstance(HMAC_SHA_256);
      mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
    } catch (Exception e) {
      throw new ExchangeException("Failed to initialize CoinsphDigest", e);
    }
  }

  public static CoinsphDigest createInstance(String secretKey) {
    return secretKey == null ? null : new CoinsphDigest(secretKey);
  }

  @Override
  public String digestParams(si.mazi.rescu.RestInvocation restInvocation) {
    String dataToSign;
    String httpMethod = restInvocation.getHttpMethod();

    // The data to sign is the "totalParams" string which is either the query string (for GET/DELETE)
    // or the request body (for POST/PUT). This string must include the timestamp, apiKey,
    // recvWindow (if used), and all other request-specific parameters, sorted alphabetically.
    // This pre-formatted string is expected to be available from restInvocation.
    if ("GET".equals(httpMethod) || "DELETE".equals(httpMethod)) {
      dataToSign = restInvocation.getQueryString();
    } else if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
      dataToSign = restInvocation.getRequestBody();
    } else {
      throw new ExchangeException("Unsupported HTTP method for signing: " + httpMethod);
    }

    // If dataToSign is null (e.g. POST with no body, though unlikely for authenticated endpoints),
    // behavior might be undefined by Coins.ph. Assuming it's always non-null for signed requests.
    if (dataToSign == null) {
        dataToSign = ""; // Or handle as an error, depending on API spec for empty data signing
    }
    
    mac.update(dataToSign.getBytes(StandardCharsets.UTF_8));
    return DigestUtils.bytesToHex(mac.doFinal()).toLowerCase();
  }
}