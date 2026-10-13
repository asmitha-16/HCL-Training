package com.campus.lostandfound.model.role;

/**
 * Staff / Faculty role in the campus hierarchy.
 */
public class Staff extends User {

    private String employeeId;
    private String department;
    private String designation;

    public Staff() {
        super();
    }

    public Staff(String id, String username, String fullName, String email, String phoneNumber,
                 String employeeId, String department, String designation) {
        super(id, username, fullName, email, phoneNumber);
        this.employeeId = employeeId;
        this.department = department;
        this.designation = designation;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getRoleTitle() {
        return "STAFF";
    }

    @Override
    public boolean canApproveClaims() {
        return false;
    }

    @Override
    public boolean canAccessCustodyLockers() {
        return false;
    }
}
