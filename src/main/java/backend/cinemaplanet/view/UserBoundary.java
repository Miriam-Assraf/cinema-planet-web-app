package backend.cinemaplanet.view;

public class UserBoundary {
    private String email;
    private RoleBoundary role;
    private String username;
    private String avatar;

    // Constructor for STUBBING
    public UserBoundary(String email, RoleBoundary role, String username, String avatar) {
        this.email = email;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    // Default constructor
    public UserBoundary() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleBoundary getRole() {
        return role;
    }

    public void setRole(RoleBoundary role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar
                + "]";
    }
}
