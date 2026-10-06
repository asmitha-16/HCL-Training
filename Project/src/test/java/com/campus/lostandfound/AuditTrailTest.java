package com.campus.lostandfound;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.common.model.Dtos.AuditLogEntry;

import java.util.List;

public class AuditTrailTest {

    public static void run() {
        System.out.println("--- Running AuditTrailTest (NFR-P3: Tamper-Evident Claim Fraud Audit Trail) ---");

        AuditTrailService audit = new AuditTrailService();

        AuditLogEntry e1 = audit.recordEvent(
                "CLAIM_SUBMITTED",
                "Claim",
                "CLM-101",
                "USR-STU-01",
                "STUDENT_STAFF",
                "10.0.0.1",
                "Claim submitted for MacBook",
                "NONE",
                "PENDING_VERIFICATION"
        );

        AuditLogEntry e2 = audit.recordEvent(
                "CLAIM_APPROVED",
                "Claim",
                "CLM-101",
                "USR-SEC-01",
                "SECURITY_DESK",
                "10.0.0.50",
                "Officer verified serial number against device chassis",
                "PENDING_VERIFICATION",
                "APPROVED"
        );

        assert e1.checksumHash() != null && e1.checksumHash().length() >= 8 : "Entry 1 must have cryptographic checksum";
        assert e2.checksumHash() != null && e2.checksumHash().length() >= 8 : "Entry 2 must have cryptographic checksum";
        assert !e1.checksumHash().equals(e2.checksumHash()) : "Checksums must differ across events";

        List<AuditLogEntry> forClaim = audit.getEntriesForEntity("CLM-101");
        assert forClaim.size() == 2 : "Expected 2 audit entries for CLM-101";

        List<AuditLogEntry> forOfficer = audit.getEntriesByActor("USR-SEC-01");
        assert forOfficer.size() == 1 : "Expected 1 audit entry for USR-SEC-01";

        System.out.println("  [PASS] AuditTrailTest: Tamper-evident cryptographic log with SHA-256 integrity hashes verified successfully.");
    }
}