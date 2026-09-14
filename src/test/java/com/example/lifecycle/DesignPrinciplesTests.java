package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.lifecycle.design.AuditedPaymentGateway;
import com.example.lifecycle.design.CardPaymentGateway;
import com.example.lifecycle.design.EnterprisePaymentService;
import com.example.lifecycle.design.FraudCheck;
import com.example.lifecycle.design.LegacyFraudCheckAdapter;
import com.example.lifecycle.design.LegacyFraudClient;
import com.example.lifecycle.design.PaymentCommand;
import com.example.lifecycle.design.PaymentGateway;
import com.example.lifecycle.design.PaymentGatewayFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DesignPrinciplesTests {

    @Test
    void factoryStrategyAdapterDecoratorAndObserverWorkTogether() {
        List<String> audit = new ArrayList<>();
        List<String> events = new ArrayList<>();
        PaymentGateway gateway = new AuditedPaymentGateway(new CardPaymentGateway(),
                receipt -> audit.add(receipt.status()));
        EnterprisePaymentService service = new EnterprisePaymentService(
                new PaymentGatewayFactory(Map.of("CARD", gateway)),
                new LegacyFraudCheckAdapter(new LegacyFraudClient()),
                List.of(receipt -> events.add(receipt.orderId())));

        PaymentReceiptAssert receipt = new PaymentReceiptAssert(
                service.authorize(command("order-1", "20.00"), "CARD"));

        assertThat(receipt.provider()).isEqualTo("card");
        assertThat(receipt.status()).isEqualTo("AUTHORIZED");
        assertThat(audit).containsExactly("AUTHORIZED");
        assertThat(events).containsExactly("order-1");
    }

    @Test
    void unsupportedStrategyFailsAtFactoryBoundary() {
        PaymentGatewayFactory factory = new PaymentGatewayFactory(Map.of());

        assertThatThrownBy(() -> factory.forMethod("CRYPTO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported payment method");
    }

    @Test
    void fraudFailureStopsBeforeGatewayCall() {
        FraudCheck rejectingCheck = command -> false;
        EnterprisePaymentService service = new EnterprisePaymentService(
                new PaymentGatewayFactory(Map.of("CARD", new CardPaymentGateway())),
                rejectingCheck,
                List.of());

        assertThatThrownBy(() -> service.authorize(command("order-2", "20.00"), "CARD"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fraud");
    }

    @Test
    void builderValidatesImmutablePaymentCommand() {
        PaymentCommand command = command("order-3", "10.25");

        assertThat(command.amount()).isEqualByComparingTo(new BigDecimal("10.25"));
        assertThatThrownBy(() -> PaymentCommand.builder()
                .orderId("bad")
                .amount(BigDecimal.ZERO)
                .currency("USD")
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static PaymentCommand command(String orderId, String amount) {
        return PaymentCommand.builder()
                .orderId(orderId)
                .amount(new BigDecimal(amount))
                .currency("USD")
                .build();
    }

    private record PaymentReceiptAssert(String orderId, String provider, String status) {
        PaymentReceiptAssert(com.example.lifecycle.design.PaymentReceipt receipt) {
            this(receipt.orderId(), receipt.provider(), receipt.status());
        }
    }
}
