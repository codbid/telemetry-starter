package com.codbid.telemetry.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telemetry.kafka.producer")
public class TelemetryKafkaProducerProperties {

    private String acks = "all";
    private Integer retries = 5;
    private Integer lingerMs = 20;
    private Integer batchSize = 32768;
    private String compressionType = "snappy";

    private Integer deliveryTimeoutMs = 120000;
    private Integer requestTimeoutMs = 30000;
    private Integer maxInFlightRequests = 5;

    private Integer bufferMemory = 33554432;
    private Integer maxRequestSize = 1048576;

    private Integer reconnectBackoffMs = 50;
    private Integer reconnectBackoffMaxMs = 1000;

    private Integer retryBackoffMs = 100;

    private Integer metadataMaxAgeMs = 300000;

    private Integer connectionsMaxIdleMs = 540000;

    private Integer sendBufferBytes = 131072;
    private Integer receiveBufferBytes = 32768;

    private Boolean enableIdempotence = true;

    private String clientId = "telemetry-producer";

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(Integer lingerMs) {
        this.lingerMs = lingerMs;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public Integer getDeliveryTimeoutMs() {
        return deliveryTimeoutMs;
    }

    public void setDeliveryTimeoutMs(Integer deliveryTimeoutMs) {
        this.deliveryTimeoutMs = deliveryTimeoutMs;
    }

    public Integer getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(Integer requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public Integer getMaxInFlightRequests() {
        return maxInFlightRequests;
    }

    public void setMaxInFlightRequests(Integer maxInFlightRequests) {
        this.maxInFlightRequests = maxInFlightRequests;
    }

    public Integer getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(Integer bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public Integer getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(Integer maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public Integer getReconnectBackoffMs() {
        return reconnectBackoffMs;
    }

    public void setReconnectBackoffMs(Integer reconnectBackoffMs) {
        this.reconnectBackoffMs = reconnectBackoffMs;
    }

    public Integer getReconnectBackoffMaxMs() {
        return reconnectBackoffMaxMs;
    }

    public void setReconnectBackoffMaxMs(Integer reconnectBackoffMaxMs) {
        this.reconnectBackoffMaxMs = reconnectBackoffMaxMs;
    }

    public Integer getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(Integer retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public Integer getMetadataMaxAgeMs() {
        return metadataMaxAgeMs;
    }

    public void setMetadataMaxAgeMs(Integer metadataMaxAgeMs) {
        this.metadataMaxAgeMs = metadataMaxAgeMs;
    }

    public Integer getConnectionsMaxIdleMs() {
        return connectionsMaxIdleMs;
    }

    public void setConnectionsMaxIdleMs(Integer connectionsMaxIdleMs) {
        this.connectionsMaxIdleMs = connectionsMaxIdleMs;
    }

    public Integer getSendBufferBytes() {
        return sendBufferBytes;
    }

    public void setSendBufferBytes(Integer sendBufferBytes) {
        this.sendBufferBytes = sendBufferBytes;
    }

    public Integer getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public void setReceiveBufferBytes(Integer receiveBufferBytes) {
        this.receiveBufferBytes = receiveBufferBytes;
    }

    public Boolean getEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(Boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}