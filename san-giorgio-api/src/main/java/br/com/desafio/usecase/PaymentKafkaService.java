package br.com.desafio.usecase;

import br.com.desafio.dto.PaymentItemDTO;
import br.com.desafio.util.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public void sendPartialPayment(PaymentItemDTO paymentItem) {
        kafkaProducerService.sendMessage("partial_payments", paymentItem);
    }

    public void sendFullPayment(PaymentItemDTO paymentItem) {
        kafkaProducerService.sendMessage("full_payments", paymentItem);
    }

    public void sendExcessPayment(PaymentItemDTO paymentItem) {
        kafkaProducerService.sendMessage("excess_payments", paymentItem);
    }
}
