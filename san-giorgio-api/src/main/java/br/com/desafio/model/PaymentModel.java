package br.com.desafio.model;

public class PaymentModel {
	// acabei chamando a PaymentModel de ChargerModel
	// a tabela de "Payment" no futuro ficaria reservada pra guardar os pagamentos
	// Exemplo. Vendedor A paga 50% do "original_amount"
	// e em outra transação pagaria os outros 50% faltantes.
	// teriamos que dar como Pago, e não enviar para a fila de Pagamento Parcial.
	// podendo talvez enviar para a fila de Pagamento Excedente caso o Cliente tenha pago 50% e depois +60% por exemplo.
}
