package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.Recycler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecyclerRepository extends JpaRepository<Recycler, Long> {
}
