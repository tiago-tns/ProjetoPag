package br.com.desafio.usecase;

import br.com.desafio.dto.PaymentDTO;
import br.com.desafio.dto.PaymentItemDTO;
import br.com.desafio.model.ChargerModel;
import br.com.desafio.model.MerchantModel;
import br.com.desafio.repository.ChargerRepository;
import br.com.desafio.repository.MerchantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmPaymentUseCaseImpl implements ConfirmPaymentUseCase {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ChargerRepository chargerRepository;

    @Autowired
    private PaymentKafkaService paymentKafkaService;

    @Override
    public ResponseEntity<String> confirm(PaymentDTO paymentDTO) {
    	//valida se existe o Cliente antes, para evitar processamento desnecessário
        if (validateMerchant(paymentDTO.getClientId()).isPresent()) {
        	//valida se TODOS os dados de cobrança estão correto antes de prosseguir
                if (!validatePaymentItems(paymentDTO)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados de cobrança não conferem");
                }
            processPaymentItems(paymentDTO);
            return ResponseEntity.ok("Pagamento processado com sucesso:\n"+paymentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
    }
    
    private void processPaymentItems(PaymentDTO paymentDTO) {
        for (PaymentItemDTO paymentItem : paymentDTO.getPaymentItems()) {
            ChargerModel charger = chargerRepository.findByOrderItemIdAndMerchantId(
                    paymentItem.getPaymentId(),
                    paymentDTO.getClientId()
            );
            if (charger != null) {
                int comparisonResult = paymentItem.getPaymentValue().compareTo(charger.getOriginalAmount());
                if (comparisonResult < 0) {
                    paymentItem.setPaymentStatus("PARCIAL");
                    paymentKafkaService.sendPartialPayment(paymentItem);
                } else if (comparisonResult == 0) {
                    paymentItem.setPaymentStatus("TOTAL");
                    paymentKafkaService.sendFullPayment(paymentItem);
                } else {
                    paymentItem.setPaymentStatus("EXCEDENTE");
                    paymentKafkaService.sendExcessPayment(paymentItem);
                }
            }
        }
    }
    
    private boolean validatePaymentItems(PaymentDTO paymentDTO) {
        for (PaymentItemDTO paymentItem : paymentDTO.getPaymentItems()) {
            ChargerModel charger = chargerRepository.findByOrderItemIdAndMerchantId(
                    paymentItem.getPaymentId(),
                    paymentDTO.getClientId()
            );
            if (charger == null) {
                return false;
            }
        }
        return true;
    }    
    
    private Optional<MerchantModel> validateMerchant(Integer clientId) {
        return merchantRepository.findById(clientId);
    }    
}