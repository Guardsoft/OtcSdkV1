package com.otc.sdk.pos.flows.domain.usecase.pax.signature;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public abstract class Signature {

    protected final String accessKey;
    protected RequestToSign request;
    protected String stringSignedHeader;

    public Signature(RequestToSign request) {
        this.request = request;
        this.accessKey = request.getAccessKey();
        this.stringSignedHeader = "";
    }

    public abstract String prepareStringToSign(String canonicalURL, String xAmzDate);

    public abstract String buildAuthorizationString(String strSignature);

    public String prepareCanonicalRequest(String xAmzDate) throws UnsupportedEncodingException {

        String httpMethodName = request.getMethod(); //message.getInboundProperty('http.method');
        String canonicalURI = request.getPath();
        StringBuilder canonicalURL = new StringBuilder("");

        canonicalURL.append(httpMethodName).append("\n"); //Step 1: Add Method;
        canonicalURI = canonicalURI.trim().isEmpty() ? "/" : canonicalURI;
        canonicalURL.append(canonicalURI).append("\n");

        StringBuilder queryString = new StringBuilder("");
        if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
            for (Map.Entry<String, String> entrySet : request.getQueryParams().entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                queryString.append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString())).append("&");
            }

            queryString.deleteCharAt(queryString.lastIndexOf("&"));

            queryString.append("\n");
        } else {
            queryString.append("\n");
        }

        canonicalURL.append(queryString); //Step 2: Add Query Params in UTF-8;


        TreeMap<String, String> awsHeaders = new TreeMap<>();
        awsHeaders.put("x-amz-date", xAmzDate);
        awsHeaders.put("host", request.getHost());
        awsHeaders.put("content-type", "application/json");
        awsHeaders.put("content-length", String.valueOf(request.getPayload().length()));

        StringBuilder signedHeaders = new StringBuilder("");
        if (!awsHeaders.isEmpty()) {
            for (Map.Entry<String, String> entrySet : awsHeaders.entrySet()) {
                String key = entrySet.getKey().toLowerCase();
                String value = entrySet.getValue();
                signedHeaders.append(key).append(";");
                canonicalURL.append(key).append(":").append(value).append("\n"); //Step 3: Add Headers;
            }
            canonicalURL.append("\n");
        } else {
            canonicalURL.append("\n");
        }

        stringSignedHeader = signedHeaders.substring(0, signedHeaders.length() - 1).toLowerCase();
        canonicalURL.append(stringSignedHeader).append("\n"); //Step 4: Add Signed Headers;

        String payload = request.getPayload() == null ? "" : request.getPayload();

        byte[] contentHash = SignatureUtil.hash(payload);
        String contentHashString = SignatureUtil.toHex(contentHash);

        canonicalURL.append(contentHashString); //Step 5: Add Payload;

        return canonicalURL.toString();
    }


}