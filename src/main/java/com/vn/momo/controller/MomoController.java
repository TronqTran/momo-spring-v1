package com.vn.momo.controller;

import com.vn.momo.service.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/momo")
@RequiredArgsConstructor
public class MomoController {

    private final MomoService momoService;

    // Example endpoint to create an order
     @PostMapping("/createOrder")
     public ResponseEntity<?> createOrder(@RequestParam String amount,
                                          @RequestParam String orderInfo) {
         Map<String, Object> response = momoService.createOrder(amount, orderInfo);
         return ResponseEntity.ok(response);
     }

    // Example endpoint to query an order
    @GetMapping("/queryOrder")
    public ResponseEntity<?> queryOrder(@RequestParam String orderId) {
        Map<String, Object> response = momoService.queryTransactionStatus(orderId);
        return ResponseEntity.ok(response);
    }

    // Example endpoint to refund an order
    @PostMapping("/refundOrder")
    public ResponseEntity<?> refundOrder(@RequestParam Long transactionId,
                                         @RequestParam String amount,
                                         @RequestParam String reason) {
        Map<String, Object> response = momoService.refundTransaction(transactionId, amount, reason);
        return ResponseEntity.ok(response);
    }

    // Example endpoint to query refund status
    @GetMapping("/queryRefundStatus")
    public ResponseEntity<?> queryRefundStatus(@RequestParam String orderId) {
        Map<String, Object> response = momoService.queryRefundStatus(orderId);
        return ResponseEntity.ok(response);
    }
}
