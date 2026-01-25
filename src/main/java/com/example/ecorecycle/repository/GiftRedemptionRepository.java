package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.GiftRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRedemptionRepository extends JpaRepository<GiftRedemption, Long> {
    List<GiftRedemption> findByUserOrderByCreatedAtDesc(BaseUser user);
    List<GiftRedemption> findByUserAndStatusOrderByCreatedAtDesc(BaseUser user, GiftRedemption.RedemptionStatus status);
}

