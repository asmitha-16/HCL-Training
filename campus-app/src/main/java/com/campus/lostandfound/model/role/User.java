package com.campus.lostandfound.model.role;

import com.campus.lostandfound.model.base.BaseEntity;

/**
 * Base User class in the role hierarchy.
 */
public abstract class User extends BaseEntity {

    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;

    public User() {
        super();
    }

    public User(String id, String username, String fullName, String email, String phoneNumber) {
        super(id);
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public abstract String getRoleTitle();

    public abstract boolean canApproveClaims();

    public abstract boolean canAccessCustodyLockers();

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + getRoleTitle() +
                '}';
    }
}
