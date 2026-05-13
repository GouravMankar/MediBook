package com.medibook.payment.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.payment.dto.PaymentRequestDTO;
import com.medibook.payment.dto.PaymentResponseDTO;
import com.medibook.payment.dto.RazorpayVerifyRequestDTO;
import com.medibook.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPaymentRecord(@Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(service.createPaymentRecord(request));
    }

    @PostMapping("/razorpay/order")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(service.processPayment(request));
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<PaymentResponseDTO> verifyRazorpayPayment(
            @Valid @RequestBody RazorpayVerifyRequestDTO request) {
        return ResponseEntity.ok(service.verifyRazorpayPayment(request));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByAppointment(@PathVariable Long appointmentId) {
        Optional<PaymentResponseDTO> payment = service.getPaymentByAppointment(appointmentId);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getPaymentsByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(service.getPaymentsByProvider(providerId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentHistory() {
        return ResponseEntity.ok(service.getPaymentHistory());
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(service.refundPayment(id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPaymentStatus(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(service.updatePaymentStatus(id, status));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<String> generateInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(service.generateInvoice(id));
    }

    @GetMapping("/totalRevenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(service.getTotalRevenue());
    }
}
