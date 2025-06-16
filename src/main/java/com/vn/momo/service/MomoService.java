package com.vn.momo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vn.momo.config.MomoConfig;
import com.vn.momo.util.MomoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoService {
    private static final int TIMEOUT = 30000; // 30 seconds
    private static final String SIGNATURE_FORMAT = "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MomoConfig momoConfig;
    private final MomoUtil momoUtil;

    public Map<String, Object> createOrder(String amount, String orderInfo) {
        try {
            String requestId = momoConfig.getPartnerCode() + "-" + java.util.UUID.randomUUID();
            String orderId = "OD-" + requestId; // Use requestId as orderId
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    momoConfig.getAccessKey(), amount, "", momoConfig.getCallBack(), orderId, orderInfo,
                    momoConfig.getPartnerCode(), momoConfig.getRedirectUrl(), requestId, momoConfig.getRequestType()
            );

            String signature = momoUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);
            if (signature.isEmpty()) {
                throw new IllegalStateException("Failed to generate signature");
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("partnerCode", momoConfig.getPartnerCode());
            requestBody.put("accessKey", momoConfig.getAccessKey());
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", momoConfig.getRedirectUrl());
            requestBody.put("ipnUrl", momoConfig.getCallBack());
            requestBody.put("extraData", "");
            requestBody.put("requestType", momoConfig.getRequestType());
            requestBody.put("signature", signature);
            requestBody.put("lang", "vi");

            return sendRequest(momoConfig.getCreateEndpoint(), requestBody);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create payment request");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return errorResponse;
        }
    }

    public Map<String, Object> queryTransactionStatus(String orderId) {
        return processQuery(orderId, momoConfig.getQueryEndpoint(), "Failed to query transaction status");
    }

    public Map<String, Object> refundTransaction(Long transId, String amount, String description) {
        try {

            String requestId = momoConfig.getPartnerCode() + "-" + UUID.randomUUID();
            String orderId = "RF-" + requestId; // Generate a unique orderId for refund

            // Generate raw signature
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&description=%s&orderId=%s&partnerCode=%s&requestId=%s&transId=%d",
                    momoConfig.getAccessKey(),
                    amount,
                    description,
                    orderId,
                    momoConfig.getPartnerCode(),
                    requestId,
                    transId
            );

            String signature = momoUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);
            if (signature.isEmpty()) {
                throw new IllegalStateException("Failed to generate signature");
            }

            // Request body for refund
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("partnerCode", momoConfig.getPartnerCode());
            requestBody.put("orderId", orderId);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("transId", String.valueOf(transId));
            requestBody.put("lang", "vi");
            requestBody.put("description", description);
            requestBody.put("signature", signature);

            // Send refund request
            return sendRequest(momoConfig.getRefundEndpoint(), requestBody);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to process refund");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return errorResponse;
        }
    }

    public Map<String, Object> queryRefundStatus(String refundOrderId) {
        return processQuery(refundOrderId, momoConfig.getQueryRefundEndpoint(), "Failed to query refund status");
    }

    private Map<String, Object> processQuery(String orderId, String endpoint, String errorMessage) {
        try {
            String requestId = momoConfig.getPartnerCode() + "-" + UUID.randomUUID();

            String signature = momoUtil.hmacSHA256(
                    momoConfig.getSecretKey(),
                    String.format(SIGNATURE_FORMAT,
                            momoConfig.getAccessKey(),
                            orderId,
                            momoConfig.getPartnerCode(),
                            requestId
                    )
            );

            if (signature.isEmpty()) {
                throw new IllegalStateException("Failed to generate signature");
            }

            Map<String, String> requestBody = new HashMap<>() {{
                put("partnerCode", momoConfig.getPartnerCode());
                put("requestId", requestId);
                put("orderId", orderId);
                put("lang", "vi");
                put("signature", signature);
            }};

            return sendRequest(endpoint, requestBody);
        } catch (Exception e) {
            return new HashMap<>() {{
                put("error", errorMessage);
                put("message", e.getMessage());
                put("timestamp", System.currentTimeMillis());
            }};
        }
    }

    private Map<String, Object> sendRequest(String endpoint, Map<String, String> requestBody) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            // Write request
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonBody);
                writer.flush();
            }

            // Read response
            int responseCode = connection.getResponseCode();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            responseCode == HttpURLConnection.HTTP_OK
                                    ? connection.getInputStream()
                                    : connection.getErrorStream(),
                            StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "HTTP error code: " + responseCode);
                    errorResponse.put("message", response.toString());
                    errorResponse.put("timestamp", System.currentTimeMillis());
                    return errorResponse;
                }

                return objectMapper.readValue(response.toString(), new TypeReference<Map<String, Object>>() {});
            }
        } finally {
            connection.disconnect();
        }
    }
}