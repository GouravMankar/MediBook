package com.medibook.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.payment.dto.PaymentRequestDTO;
import com.medibook.payment.dto.PaymentResponseDTO;
import com.medibook.payment.dto.RazorpayVerifyRequestDTO;
import com.medibook.payment.service.PaymentService;

@WebMvcTest(controllers = PaymentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Test
    void createPaymentRecordReturnsPayment() throws Exception {
        when(service.createPaymentRecord(any(PaymentRequestDTO.class))).thenReturn(payment("PAID"));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "slotId": 4,
                                  "amount": 700,
                                  "paymentMode": "UPI",
                                  "currency": "INR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void razorpayOrderAndVerifyEndpointsReturnPayment() throws Exception {
        when(service.processPayment(any(PaymentRequestDTO.class))).thenReturn(payment("PENDING"));
        when(service.verifyRazorpayPayment(any(RazorpayVerifyRequestDTO.class))).thenReturn(payment("PAID"));

        mockMvc.perform(post("/payments/razorpay/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentId": 1,
                                  "patientId": 2,
                                  "providerId": 3,
                                  "slotId": 4,
                                  "amount": 700,
                                  "paymentMode": "UPI",
                                  "currency": "INR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(post("/payments/razorpay/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentId": 1,
                                  "razorpayOrderId": "order_123",
                                  "razorpayPaymentId": "pay_123",
                                  "razorpaySignature": "signature"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void getPaymentByAppointmentReturnsNotFoundWhenMissing() throws Exception {
        when(service.getPaymentByAppointment(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/payments/appointment/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listRefundStatusInvoiceAndRevenueEndpointsReturnServiceData() throws Exception {
        when(service.getPaymentByAppointment(1L)).thenReturn(Optional.of(payment("PAID")));
        when(service.getPaymentsByPatient(2L)).thenReturn(List.of(payment("PAID")));
        when(service.getPaymentsByProvider(3L)).thenReturn(List.of(payment("PAID")));
        when(service.getPaymentHistory()).thenReturn(List.of(payment("PAID")));
        when(service.refundPayment(1L)).thenReturn(payment("REFUNDED"));
        when(service.getPaymentStatus(1L)).thenReturn("PAID");
        when(service.generateInvoice(1L)).thenReturn("INVOICE");
        when(service.getTotalRevenue()).thenReturn(700.0);

        mockMvc.perform(get("/payments/appointment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
        mockMvc.perform(get("/payments/patient/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(2));
        mockMvc.perform(get("/payments/provider/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value(3));
        mockMvc.perform(get("/payments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PAID"));
        mockMvc.perform(post("/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
        mockMvc.perform(get("/payments/1/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("PAID"));
        mockMvc.perform(get("/payments/1/invoice"))
                .andExpect(status().isOk())
                .andExpect(content().string("INVOICE"));
        mockMvc.perform(get("/payments/totalRevenue"))
                .andExpect(status().isOk())
                .andExpect(content().string("700.0"));
    }

    @Test
    void updatePaymentStatusReturnsUpdatedPayment() throws Exception {
        when(service.updatePaymentStatus(1L, "REFUNDED")).thenReturn(payment("REFUNDED"));

        mockMvc.perform(put("/payments/1/status").param("status", "REFUNDED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }

    private PaymentResponseDTO payment(String status) {
        return PaymentResponseDTO.builder()
                .paymentId(1L)
                .appointmentId(1L)
                .patientId(2L)
                .providerId(3L)
                .slotId(4L)
                .amount(700.0)
                .mode("UPI")
                .currency("INR")
                .status(status)
                .build();
    }
}
