/**
 * RoleHierarchy.java
 *
 * Day 5 Project Deliverable: Role Hierarchy
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Establishes concrete user roles with polymorphic permissions:
 *   BaseEntity -> User -> StudentUser, StaffUser, SecurityOfficerUser, AdminUser
 */
public class RoleHierarchy {

    public static abstract class User extends BaseEntity {
        protected final String fullName;
        protected final String email;

        public User(String id, String fullName, String email) {
            super(id);
            if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Name required");
            if (email == null || !email.contains("@")) throw new IllegalArgumentException("Valid email required");
            this.fullName = fullName.trim();
            this.email = email.trim();
        }

        public abstract String getRoleName();
        public abstract boolean canVerifyClaims();
        public abstract boolean canManageInventory();

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return String.format("%s[ID=%s, Name='%s', Role=%s]",
                    getClass().getSimpleName(), id, fullName, getRoleName());
        }
    }

    public static class StudentUser extends User {
        private final String rollNumber;

        public StudentUser(String id, String fullName, String email, String rollNumber) {
            super(id, fullName, email);
            this.rollNumber = rollNumber;
        }

        @Override public String getRoleName() { return "STUDENT"; }
        @Override public boolean canVerifyClaims() { return false; }
        @Override public boolean canManageInventory() { return false; }
        public String getRollNumber() { return rollNumber; }
    }

    public static class StaffUser extends User {
        private final String department;

        public StaffUser(String id, String fullName, String email, String department) {
            super(id, fullName, email);
            this.department = department;
        }

        @Override public String getRoleName() { return "STAFF"; }
        @Override public boolean canVerifyClaims() { return false; }
        @Override public boolean canManageInventory() { return false; }
        public String getDepartment() { return department; }
    }

    public static class SecurityOfficerUser extends User {
        private final String badgeNumber;

        public SecurityOfficerUser(String id, String fullName, String email, String badgeNumber) {
            super(id, fullName, email);
            this.badgeNumber = badgeNumber;
        }

        @Override public String getRoleName() { return "SECURITY_DESK"; }
        @Override public boolean canVerifyClaims() { return true; }
        @Override public boolean canManageInventory() { return true; }
        public String getBadgeNumber() { return badgeNumber; }
    }

    public static class AdminUser extends User {
        public AdminUser(String id, String fullName, String email) {
            super(id, fullName, email);
        }

        @Override public String getRoleName() { return "ADMIN"; }
        @Override public boolean canVerifyClaims() { return true; }
        @Override public boolean canManageInventory() { return true; }
    }
}
