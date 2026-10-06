package com.campus.lostandfound.common.feign;

import com.campus.lostandfound.common.status.ItemStatus;
import java.util.Optional;

/**
 * Feign Client interface representing inter-service communication from claim-service to item-service.
 * Annotated with Feign-compatible contract semantics.
 */
public interface ItemServiceClient {

    record ItemSummary(
            String id,
            String title,
            String category,
            String location,
            String statusCode,
            String lockerId,
            String registeredBy
    ) {}

    record LockRequest(String itemId, String claimId, String requesterId) {}
    record LockResponse(boolean success, String message, String currentStatus) {}

    record StatusUpdateRequest(String itemId, String newStatusCode, String actorId, String remarks) {}
    record StatusUpdateResponse(boolean success, String updatedStatus, String message) {}

    Optional<ItemSummary> getItemSummary(String itemId);

    LockResponse lockItemForClaim(LockRequest request);

    StatusUpdateResponse updateItemStatus(StatusUpdateRequest request);

    boolean verifyItemAvailableForClaim(String itemId);

    Optional<ItemSummary> findItemByLockerOrReference(String reference);
}
