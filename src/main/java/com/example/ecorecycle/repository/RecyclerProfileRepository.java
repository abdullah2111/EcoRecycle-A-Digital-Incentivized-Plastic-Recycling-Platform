package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.RecyclerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecyclerProfileRepository extends JpaRepository<RecyclerProfile, Long> {
}

