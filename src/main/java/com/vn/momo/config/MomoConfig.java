package com.vn.momo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "momo.config")
public class MomoConfig {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String requestType;
    private String createEndpoint;
    private String queryEndpoint;
    private String refundEndpoint;
    private String queryRefundEndpoint;
    private String redirectUrl;
    private String callBack;
}