package br.com.desafio.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @JsonProperty("client_id")
    private Integer clientId;  //por descuido meu, acabei declarando como INT.
    @JsonProperty("payment_items")
    private List<PaymentItem> paymentItems;
    @JsonProperty("paymentm_status")
    private String payment_status;
}
