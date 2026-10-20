import java.util.*;

/**
 * HandoverRepository.java
 *
 * Day 11 Project Deliverable: Repository Implementation
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Provides in-memory storage and query methods for HandoverReceipt records.
 */
public class HandoverRepository {

    private final Map<String, HandoverReceipt> store = new HashMap<>();

    public void save(HandoverReceipt receipt) {
        if (receipt == null) throw new IllegalArgumentException("Receipt cannot be null");
        store.put(receipt.getReceiptId(), receipt);
    }

    public Optional<HandoverReceipt> findById(String receiptId) {
        if (receiptId == null) return Optional.empty();
        return Optional.ofNullable(store.get(receiptId));
    }

    public List<HandoverReceipt> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    public int count() {
        return store.size();
    }
}
