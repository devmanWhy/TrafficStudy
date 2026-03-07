package com.devy.payments.controller;

import com.devy.common.ApiInfo;
import com.devy.payments.controller.request.DoPaymentRequestDTO;
import com.devy.payments.controller.request.SearchPaymentInfoRequestDTO;
import com.devy.payments.controller.response.DoPaymentResponseDTO;
import com.devy.payments.controller.response.SearchPaymentInfoResponseDTO;
import com.devy.payments.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiInfo.PAYMENTS.BASE_PATH + ApiInfo.PAYMENTS.PAYMENT_PATH)
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/search")
    ResponseEntity<SearchPaymentInfoResponseDTO> searchPaymentsInfo(
            @RequestBody SearchPaymentInfoRequestDTO request
    ) {
        return ResponseEntity.ok(paymentService.searchPaymentsInfo(request));
    }

    @PostMapping("/pay")
    ResponseEntity<DoPaymentResponseDTO> pay(
            @RequestBody DoPaymentRequestDTO request
    ) {
        boolean success = paymentService.pay(
                request.orderId(),
                request.userId(),
                request.amount()
        );
        return ResponseEntity.ok(
                new DoPaymentResponseDTO(success)
        );
    }

}
