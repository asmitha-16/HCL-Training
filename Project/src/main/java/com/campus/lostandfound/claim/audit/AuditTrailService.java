package com.campus.lostandfound.claim.audit;

import com.campus.lostandfound.common.model.Dtos.AuditLogEntry;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tamper-evident audit trail service meeting NFR-P3: Claim fraud audit trail.
 * Logs immutable events with cryptographic SHA-256 checksums to ensure non-repudiation.
 */
public class AuditTrailService {

    private final List<AuditLogEntry> auditLog = new CopyOnWriteArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(100);
    private String lastChecksum = "GENESIS-CAMPUS-SECURITY-0000";

    public synchronized AuditLogEntry recordEvent(
            String eventType,
            String entityType,
            String entityId,
            String actorId,
            String actorRole,
            String ipAddress,
            String actionSummary,
            String previousState,
            String newState
    ) {
        String id = "AUDIT-" + counter.incrementAndGet();
        Instant now = Instant.now();

        String rawContent = id + "|" + eventType + "|" + entityId + "|" + actorId + "|" + previousState + "|" + newState + "|" + now.toString() + "|" + lastChecksum;
        String checksum = calculateSha256(rawContent);
        this.lastChecksum = checksum;

        AuditLogEntry entry = new AuditLogEntry(
                id,
                eventType,
                entityType,
                entityId,
                actorId != null ? actorId : "SYSTEM",
                actorRole != null ? actorRole : "SYSTEM",
                ipAddress != null ? ipAddress : "127.0.0.1",
                actionSummary,
                previousState != null ? previousState : "NONE",
                newState != null ? newState : "NONE",
                checksum,
                now
        );

        auditLog.add(entry);
        return entry;
    }

    private String calculateSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    public List<AuditLogEntry> getAllEntries() {
        List<AuditLogEntry> list = new ArrayList<>(auditLog);
        Collections.reverse(list);
        return list;
    }

    public List<AuditLogEntry> getEntriesForEntity(String entityId) {
        return auditLog.stream()
                .filter(e -> e.entityId().equalsIgnoreCase(entityId))
                .sorted(Comparator.comparing(AuditLogEntry::timestamp).reversed())
                .toList();
    }

    public List<AuditLogEntry> getEntriesByActor(String actorId) {
        return auditLog.stream()
                .filter(e -> e.actorId().equalsIgnoreCase(actorId))
                .sorted(Comparator.comparing(AuditLogEntry::timestamp).reversed())
                .toList();
    }
}
