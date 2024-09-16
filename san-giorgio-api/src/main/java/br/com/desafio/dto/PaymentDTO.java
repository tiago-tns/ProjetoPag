package br.com.desafio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder  //builder com os metodos get e set 
@NoArgsConstructor // Necessário para o Jackson (construtor em argumentos > public PaymentDTO() )
@AllArgsConstructor // Construtor com todos os argumentos para uso com @Builder ( public PaymentDTO(Integer clientId, List<PaymentItemDTO> paymentItems) )
public class PaymentDTO {
    private Integer clientId; // Código do vendedor
    private List<PaymentItemDTO> paymentItems; // Lista de itens de pagamento
}
