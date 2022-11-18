package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT obj "
           + "FROM Sale obj "
           + "WHERE obj.date BETWEEN :dateMin AND :dateMax "
           + "AND UPPER(obj.seller.name) LIKE UPPER(CONCAT('%',:sellerName,'%'))")
    Page<Sale> searchReport(LocalDate dateMin, LocalDate dateMax, String sellerName, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * "
            + "FROM tb_sales "
            + "INNER JOIN tb_seller ON tb_sales.seller_id = tb_seller.id "
            + "WHERE tb_sales.date  BETWEEN :dateMin AND :dateMax")
    List<Sale> searchSummary(LocalDate dateMin, LocalDate dateMax);
}
