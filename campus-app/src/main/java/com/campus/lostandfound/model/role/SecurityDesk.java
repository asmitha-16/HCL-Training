package com.campus.lostandfound.model.role;

/**
 * Security Desk officer role responsible for custody lockers,
 * verification reviews, and physical handovers.
 */
public class SecurityDesk extends User {

    private String badgeNumber;
    private String securityPostLocation;

    public SecurityDesk() {
        super();
    }

    public SecurityDesk(String id, String username, String fullName, String email, String phoneNumber,
                        String badgeNumber, String securityPostLocation) {
        super(id, username, fullName, email, phoneNumber);
        this.badgeNumber = badgeNumber;
        this.securityPostLocation = securityPostLocation;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getSecurityPostLocation() {
        return securityPostLocation;
    }

    public void setSecurityPostLocation(String securityPostLocation) {
        this.securityPostLocation = securityPostLocation;
    }

    @Override
    public String getRoleTitle() {
        return "SECURITY_DESK";
    }

    @Override
    public boolean canApproveClaims() {
        return true;
    }

    @Override
    public boolean canAccessCustodyLockers() {
        return true;
    }
}
