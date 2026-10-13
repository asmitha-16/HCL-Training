package com.campus.lostandfound.model.role;

/**
 * Student role in the campus hierarchy.
 */
public class Student extends User {

    private String rollNumber;
    private String department;
    private int currentYear;

    public Student() {
        super();
    }

    public Student(String id, String username, String fullName, String email, String phoneNumber,
                   String rollNumber, String department, int currentYear) {
        super(id, username, fullName, email, phoneNumber);
        this.rollNumber = rollNumber;
        this.department = department;
        this.currentYear = currentYear;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    @Override
    public String getRoleTitle() {
        return "STUDENT";
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
