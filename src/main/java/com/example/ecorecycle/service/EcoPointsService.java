package com.example.ecorecycle.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to calculate eco points based on plastic type and weight
 * Points are calculated using modest per-kg rates to avoid excessive totals
 */
@Service
public class EcoPointsService {

    /**
     * Eco points per kilogram for different plastic types
     * Values are intentionally conservative to keep totals small per delivery
     */
    private static final Map<String, Double> PLASTIC_TYPE_RATES = new HashMap<>();

    static {
        // Conservative rate in points per kg
        // Higher value plastics slightly higher points, others lower
        PLASTIC_TYPE_RATES.put("PET", 10.0);    // PET bottles/containers
        PLASTIC_TYPE_RATES.put("HDPE", 9.0);    // High-density polyethylene
        PLASTIC_TYPE_RATES.put("LDPE", 8.0);    // Low-density polyethylene
        PLASTIC_TYPE_RATES.put("PP", 7.0);      // Polypropylene
        PLASTIC_TYPE_RATES.put("PVC", 6.0);     // Polyvinyl chloride
        PLASTIC_TYPE_RATES.put("PS", 5.0);      // Polystyrene
        PLASTIC_TYPE_RATES.put("OTHER", 4.0);   // Other plastics
    }

    /**
     * Calculate eco points for a pickup based on plastic types and weight
     * @param plasticTypes Comma-separated plastic types (e.g., "PET,HDPE,PP")
     * @param weight Total weight in kilograms
     * @return Total eco points earned (rounded to nearest integer)
     */
    public Long calculateEcoPoints(String plasticTypes, Double weight) {
        if (plasticTypes == null || plasticTypes.trim().isEmpty() || weight == null || weight <= 0) {
            return 0L;
        }

        String[] types = plasticTypes.split(",");
        double averageRate = 0.0;
        int count = 0;
        for (String type : types) {
            String trimmedType = type.trim().toUpperCase();
            Double rate = PLASTIC_TYPE_RATES.getOrDefault(trimmedType, PLASTIC_TYPE_RATES.get("OTHER"));
            averageRate += rate;
            count++;
        }
        if (count == 0) {
            return 0L;
        }
        averageRate = averageRate / count;

        // Total points = weight * average rate, rounded
        double totalPoints = weight * averageRate;

        // Ensure small, reasonable numbers
        return Math.round(totalPoints);
    }

    /**
     * Get the rate for a specific plastic type
     * @param plasticType The plastic type
     * @return Points per kg for that plastic type
     */
    public Double getPlasticTypeRate(String plasticType) {
        return PLASTIC_TYPE_RATES.getOrDefault(plasticType.toUpperCase(), PLASTIC_TYPE_RATES.get("OTHER"));
    }

    /**
     * Get all available plastic types and their rates
     * @return Map of plastic types and their rates
     */
    public Map<String, Double> getAllPlasticTypeRates() {
        return new HashMap<>(PLASTIC_TYPE_RATES);
    }
}
