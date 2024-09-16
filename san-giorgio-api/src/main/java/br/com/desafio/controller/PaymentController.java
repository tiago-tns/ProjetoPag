package br.com.desafio.controller;

import br.com.desafio.dto.PaymentDTO;
import br.com.desafio.usecase.ConfirmPaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @PutMapping(path = "/api/payment")
    @Operation(summary = "Processa um pagamento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento processado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados de cobrança não conferem")
    })
    public ResponseEntity<String> processPayment(@RequestBody PaymentDTO paymentDTO) {
        return confirmPaymentUseCase.confirm(paymentDTO);
    }
}
