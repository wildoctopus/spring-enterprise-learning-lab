package com.example.lifecycle.design;

import com.example.lifecycle.playground.PlaygroundSelection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(60)
public class DesignPrinciplesDemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
                if (!PlaygroundSelection.includes("design", args)) {
                        return;
                }
        List<String> auditLog = new ArrayList<>();
        PaymentGateway auditedCardGateway = new AuditedPaymentGateway(
                new CardPaymentGateway(),
                receipt -> auditLog.add(receipt.provider() + ":" + receipt.status()));
        PaymentGatewayFactory factory = new PaymentGatewayFactory(Map.of(
                "CARD", auditedCardGateway,
                "BANK_TRANSFER", new BankTransferGateway()));
        EnterprisePaymentService service = new EnterprisePaymentService(
                factory,
                new LegacyFraudCheckAdapter(new LegacyFraudClient()),
                List.of(receipt -> System.out.println("[Observer] Payment completed: " + receipt.orderId())));

        PaymentCommand command = PaymentCommand.builder()
                .orderId("order-100")
                .amount(new BigDecimal("125.50"))
                .currency("USD")
                .build();
        PaymentReceipt receipt = service.authorize(command, "CARD");

        System.out.println("\n--- SOLID and enterprise design patterns ---");
        System.out.println("Payment result: " + receipt);
        System.out.println("Audit log: " + auditLog);
        System.out.println("Try an unknown payment method to see the factory boundary fail fast.");
    }
}
