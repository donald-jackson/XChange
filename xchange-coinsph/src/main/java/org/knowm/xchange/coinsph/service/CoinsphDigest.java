package org.knowm.xchange.coinsph.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.knowm.xchange.exceptions.ExchangeException;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;
// import org.knowm.xchange.utils.DigestUtils; // Replaced with Apache Commons Codec
import org.apache.commons.codec.binary.Hex; // Added for Hex encoding
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.QueryParam; // Changed to Jakarta for ResCU 3.0
import java.lang.annotation.Annotation; // Added for reflection
import java.util.Set; // For paramNames
import java.util.TreeMap; // Added for sorted map

public class CoinsphDigest implements ParamsDigest {

  private static final Logger logger = LoggerFactory.getLogger(CoinsphDigest.class);
  private static final String HMAC_SHA_256 = "HmacSHA256";
  private final Mac mac;

  private CoinsphDigest(String secretKey) {
    try {
      if (secretKey == null || secretKey.isEmpty()) {
        logger.warn("CoinsphDigest: Secret key is null or empty. Signature will be invalid.");
        this.mac = Mac.getInstance(HMAC_SHA_256); 
        this.mac.init(new SecretKeySpec(new byte[0], HMAC_SHA_256)); 
        return;
      } else {
        logger.debug("CoinsphDigest: Initializing with secretKey starting with: {}", secretKey.substring(0, Math.min(secretKey.length(), 5)));
      }
      mac = Mac.getInstance(HMAC_SHA_256);
      mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
    } catch (Exception e) {
      logger.error("Failed to initialize CoinsphDigest with secretKey", e);
      throw new ExchangeException("Failed to initialize CoinsphDigest", e);
    }
  }

  public static CoinsphDigest createInstance(String secretKey) {
    if (secretKey == null || secretKey.isEmpty()) { 
        logger.warn("CoinsphDigest.createInstance: Secret key is null or empty. Returning null digest instance.");
        return null;
    }
    return new CoinsphDigest(secretKey);
  }

  private String buildCanonicalQueryString(RestInvocation invocation) {
    Map<String, String> actualQueryParams = new TreeMap<>(); // TreeMap for sorting by key

    // Get the map of Params objects, grouped by annotation type.
    Map<Class<? extends Annotation>, si.mazi.rescu.Params> allParamsGroupedByAnnotation = invocation.getParamsMap();
    if (allParamsGroupedByAnnotation == null) {
        logger.debug("invocation.getParamsMap() returned null.");
        return "";
    }

    // Get the Params object specifically for @QueryParam
    si.mazi.rescu.Params queryParamsAsParamsObject = allParamsGroupedByAnnotation.get(QueryParam.class);
    if (queryParamsAsParamsObject == null) {
        logger.debug("No Params object found for @QueryParam.");
        return "";
    }

    // Now, get the actual Map<String, Object> from this specific Params object
    Map<String, Object> queryParamsNameValueMap = queryParamsAsParamsObject.get(QueryParam.class);
    if (queryParamsNameValueMap == null || queryParamsNameValueMap.isEmpty()) {
        logger.debug("No name-value map found from queryParamsAsParamsObject.get(QueryParam.class).");
        return "";
    }
    
    for (Map.Entry<String, Object> entry : queryParamsNameValueMap.entrySet()) {
        String paramName = entry.getKey();
        Object paramValue = entry.getValue();

        if ("signature".equals(paramName)) { // Don't include signature itself in dataToSign
            continue;
        }

        // paramValue here should be resolved as ParamsDigestInvocationHandler calls resolveFactories.
        if (paramValue != null) {
            try {
                String encodedValue = java.net.URLEncoder.encode(paramValue.toString(), java.nio.charset.StandardCharsets.UTF_8.toString());
                actualQueryParams.put(paramName, encodedValue);
            } catch (java.io.UnsupportedEncodingException e) {
                // Should not happen with UTF-8
                logger.error("UTF-8 encoding not supported, cannot build query string for signature", e);
                throw new RuntimeException("UTF-8 encoding not supported", e);
            }
        } else {
            // As per Coins.ph docs (and common practice), null parameters are typically omitted from the signature string.
            logger.debug("Query param '{}' is null, omitting from signature string.", paramName);
        }
    }
    
    if (actualQueryParams.isEmpty()) {
        return "";
    }

    // Join sorted, non-null query parameters
    return actualQueryParams.entrySet().stream()
        .map(e -> e.getKey() + "=" + e.getValue()) // URL encoding should be handled by ResCU when it builds the final URL. Here we need raw values.
        .collect(Collectors.joining("&"));
  }

  @Override
  public String digestParams(RestInvocation invocation) {
    String httpMethod = invocation.getHttpMethod();
    
    String queryStringPart = buildCanonicalQueryString(invocation);
    String requestBodyPart = invocation.getRequestBody();
    if (requestBodyPart == null) { // Ensure requestBodyPart is not null for concatenation
        requestBodyPart = "";
    }

    // As per Coins.ph: "totalParams is defined as the query string concatenated with the request body"
    // Example 3 (mixed query string and request body) shows NO '&' between them:
    // "symbol=BTCPHP&side=BUY&type=LIMIT&timeInForce=GTCquantity=1&price=0.1&recvWindow=5000&timestamp=1538323200000"
    // This implies direct concatenation.
    String dataToSign = queryStringPart + requestBodyPart;

    logger.debug("CoinsphDigest.digestParams: Method='{}', Path='{}'", httpMethod, invocation.getPath());
    logger.debug("CoinsphDigest.digestParams: Constructed QueryString part for signing='{}'", queryStringPart);
    logger.debug("CoinsphDigest.digestParams: RequestBody part for signing='{}'", requestBodyPart);
    logger.debug("CoinsphDigest.digestParams: Calculated DataToSign='{}'", dataToSign);
    
    if (this.mac == null) {
        logger.error("CoinsphDigest.digestParams: MAC instance is null. Cannot generate signature.");
        return "MAC_INITIALIZATION_ERROR"; 
    }

    mac.update(dataToSign.getBytes(StandardCharsets.UTF_8));
    String signature = Hex.encodeHexString(mac.doFinal()).toLowerCase(); 
    logger.debug("CoinsphDigest.digestParams: Generated Signature='{}'", signature);
    return signature;
  }
}