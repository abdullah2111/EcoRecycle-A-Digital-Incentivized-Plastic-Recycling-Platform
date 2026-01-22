package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.HouseholdProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdProfileRepository extends JpaRepository<HouseholdProfile, Long> {
}

