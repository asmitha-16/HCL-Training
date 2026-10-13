package com.campus.lostandfound.strategy;

import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;

/**
 * Strategy interface (GoF Strategy Pattern) for calculating
 * similarity confidence scores between a lost report and a found inventory item.
 */
public interface MatchScoringStrategy {

    /**
     * Calculates similarity score in range [0.0, 1.0].
     *
     * @param lostItem  The reported lost item.
     * @param foundItem The physical found item in custody.
     * @return Score between 0.00 (no match) and 1.00 (perfect match).
     */
    double calculateScore(LostItem lostItem, FoundItem foundItem);

    /**
     * Returns human-readable strategy name.
     */
    String getStrategyName();
}
