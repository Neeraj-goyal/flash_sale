package com.orchestrate.flashsale.dao;

import com.orchestrate.flashsale.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepositoryDao extends JpaRepository< OrderEntity,Long> {
}
