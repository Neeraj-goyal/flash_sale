package com.orchestrate.flashsale.dao;

import com.orchestrate.flashsale.entities.ProductEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepositoryDao extends JpaRepository<ProductEntity,Long> {

     // this uses Cache-aside pattern: i.e. Check Redis Cache first, if not found then go to DB and update Redis cache
     @Cacheable(value = "products", key = "#productId")
     Optional<ProductEntity> findByProductId(Long productId);

     void deleteByProductId(Long productId);
}
