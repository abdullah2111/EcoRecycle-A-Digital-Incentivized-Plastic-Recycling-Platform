package com.example.ecorecycle.repository;

import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.PickupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {

    // Find all pickup requests by user
    List<PickupRequest> findByUserOrderByCreatedAtDesc(BaseUser user);

    // Find pickup requests by status
    List<PickupRequest> findByStatusOrderByCreatedAtDesc(PickupRequest.PickupStatus status);

    // Find pickup requests by user and status
    List<PickupRequest> findByUserAndStatusOrderByCreatedAtDesc(BaseUser user, PickupRequest.PickupStatus status);

    // Find pickup requests by recycler
    List<PickupRequest> findByRecyclerOrderByCreatedAtDesc(BaseUser recycler);

    // Find pickup requests by preferred date
    List<PickupRequest> findByPreferredDateOrderByPreferredTime(LocalDate preferredDate);

    // Count pickup requests by user
    Long countByUser(BaseUser user);

    // Count pickup requests by user and status
    Long countByUserAndStatus(BaseUser user, PickupRequest.PickupStatus status);

    // Find pending requests in a specific area (for recyclers to see available requests)
    List<PickupRequest> findByStatusAndAreaOrderByCreatedAtDesc(PickupRequest.PickupStatus status, String area);

    // Find pending requests in a specific thana (for recyclers to see available requests)
    List<PickupRequest> findByStatusAndThanaOrderByCreatedAtDesc(PickupRequest.PickupStatus status, String thana);
}

