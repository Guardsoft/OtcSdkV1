package com.otc.sdk.pos.flows.domain.usecase.pax.signature;

import java.util.Map;
import java.util.TreeMap;

public class RequestToSign {

    private String method;
    private String host;
    private String path;
    private String region;
    private String service;
    private String accessKey;
    private String payload;
    private Map<String, String> queryParams;

    private RequestToSign(String method, String host, String path, String region,
                          String service, String accessKey, String payload,
                          Map<String, String> queryParams) {
        this.method = method;
        this.host = host;
        this.path = path;
        this.region = region;
        this.service = service;
        this.accessKey = accessKey;
        this.payload = payload;
        this.queryParams = queryParams;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        if (method == null)
            method = "";
        this.method = method;
    }

    public String getPath() {
        if (path == null)
            path = "";
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getQueryParams() {
        if (queryParams == null) {
            queryParams = new TreeMap<>();
        }
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "RequestToSign{" + "method=" + method + ", host=" + host + ", path=" + path + ", region=" + region + ", service=" + service + ", accessKey=" + accessKey + ", payload=" + payload + ", queryParams=" + queryParams + '}';
    }

    public static final class Builder {
        private String method;
        private String host;
        private String path;
        private String region;
        private String service;
        private String accessKey;
        private String payload;
        private Map<String, String> queryParams;

        public Builder() {

        }

        public Builder withMethod(String method) {
            this.method = method != null ? method.trim().toLowerCase() : null;
            return this;
        }

        public Builder withHost(String host) {
            this.host = host != null ? host.trim().toLowerCase() : null;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path != null ? path.trim().toLowerCase() : null;
            return this;
        }

        public Builder withRegion(String region) {
            this.region = region != null ? region.trim().toLowerCase() : null;
            return this;
        }

        public Builder withService(String service) {
            this.service = service != null ? service.trim().toLowerCase() : null;
            return this;
        }

        public Builder withAccessKey(String accessKey) {
            this.accessKey = accessKey != null ? accessKey.trim() : null;
            return this;
        }

        public Builder withPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder withQueryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public RequestToSign build() {
            return new RequestToSign(method, host, path, region, service, accessKey, payload, queryParams);
        }
    }

}
