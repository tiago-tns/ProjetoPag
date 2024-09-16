package br.com.desafio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // Necess�rio para o Jackson
@AllArgsConstructor // Necess�rio para o @Builder funcionar corretamente
public class PaymentItemDTO  {
    
    private String paymentId; // C�digo da cobran�a
    private BigDecimal paymentValue; // Valor pago
    private String paymentStatus; // Status do pagamento (Parcial, Total, Excedente)
    
}
