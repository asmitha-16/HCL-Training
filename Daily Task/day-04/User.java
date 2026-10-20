import java.util.Objects;

/**
 * User.java
 *
 * Day 4 Project Deliverable: Encapsulated Domain Model 4
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Represents campus participants across student, staff, security, and admin roles.
 */
public class User {

    private final String id;
    private final String fullName;
    private final String email;
    private final String role; // "STUDENT", "STAFF", "SECURITY_DESK", "ADMIN"

    public User(String id, String fullName, String email, String role) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("User ID cannot be blank");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Name cannot be blank");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email format");

        this.id = id.trim();
        this.fullName = fullName.trim();
        this.email = email.trim();
        this.role = (role != null) ? role.trim().toUpperCase() : "STUDENT";
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User[ID=%s, Name='%s', Role=%s]", id, fullName, role);
    }
}
