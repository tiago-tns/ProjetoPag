package br.com.desafio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // Necessário para o Jackson
@AllArgsConstructor // Necessário para o @Builder funcionar corretamente
public class PaymentItemDTO  {
    
    private String paymentId; // Código da cobrança
    private BigDecimal paymentValue; // Valor pago
    private String paymentStatus; // Status do pagamento (Parcial, Total, Excedente)
    
}
