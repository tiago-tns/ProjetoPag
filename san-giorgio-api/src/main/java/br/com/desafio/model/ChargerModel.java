package br.com.desafio.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charger")
@Data
public class ChargerModel {

    @Id
    @Column(name = "idcharger")
    private Integer idcharger;  

    @Column(name = "merchant_id", nullable = false) // vendedor
    private Integer merchantId;

    @Column(name = "order_item_id", nullable = false, length = 45) // c�digo da cobran�a
    private String orderItemId;

    @Column(name = "original_amount", nullable = false) // Valor original da cobran�a
    private BigDecimal originalAmount;

}
