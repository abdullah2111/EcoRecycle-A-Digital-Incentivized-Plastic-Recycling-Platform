package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.Recycler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecyclerRepository extends JpaRepository<Recycler, Long> {
    Optional<Recycler> findByEmail(String email);
}
