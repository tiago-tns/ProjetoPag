package br.com.desafio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.desafio.model.MerchantModel;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantModel, Integer> {
}
