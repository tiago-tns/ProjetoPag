package br.com.desafio.usecase;


import org.springframework.http.ResponseEntity;

import br.com.desafio.dto.PaymentDTO;



public interface ConfirmPaymentUseCase {

	ResponseEntity<String> confirm(PaymentDTO paymentDTO);
	
}
