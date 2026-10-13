package com.campus.lostandfound.model.role;

/**
 * Campus Administrator role responsible for category taxonomies,
 * 60-day expiry donation batches, and KPI audits.
 */
public class Admin extends User {

    private String adminDepartment;
    private int clearanceLevel;

    public Admin() {
        super();
    }

    public Admin(String id, String username, String fullName, String email, String phoneNumber,
                 String adminDepartment, int clearanceLevel) {
        super(id, username, fullName, email, phoneNumber);
        this.adminDepartment = adminDepartment;
        this.clearanceLevel = clearanceLevel;
    }

    public String getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(String adminDepartment) {
        this.adminDepartment = adminDepartment;
    }

    public int getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(int clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    @Override
    public String getRoleTitle() {
        return "ADMIN";
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
