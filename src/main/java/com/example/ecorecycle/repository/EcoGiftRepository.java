package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.EcoGift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoGiftRepository extends JpaRepository<EcoGift, Long> {
    List<EcoGift> findByIsAvailableOrderByCreatedAtDesc(Boolean isAvailable);
    List<EcoGift> findByCategoryOrderByCreatedAtDesc(String category);
}

