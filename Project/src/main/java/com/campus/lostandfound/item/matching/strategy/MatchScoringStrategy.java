package com.campus.lostandfound.item.matching.strategy;

import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

/**
 * Strategy interface for scoring similarity between a LostReport and a FoundItem.
 */
public interface MatchScoringStrategy {
    String strategyName();
    MatchResult calculateScore(LostReport report, FoundItem item);
}
