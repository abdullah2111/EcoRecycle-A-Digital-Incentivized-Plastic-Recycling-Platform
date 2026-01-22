package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
}

