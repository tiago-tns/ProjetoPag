package br.com.desafio.repository;

import br.com.desafio.model.ChargerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ChargerRepository extends JpaRepository<ChargerModel, String> {

    ChargerModel findByOrderItemIdAndMerchantIdAndOriginalAmount(String order_item_id, Integer merchant_id, BigDecimal original_amount);
    
    ChargerModel findByOrderItemIdAndMerchantId(String order_item_id, Integer merchant_id);
}
