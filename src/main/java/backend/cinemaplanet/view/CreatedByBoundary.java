package backend.cinemaplanet.view;

public class CreatedByBoundary {
    private String email;

    public CreatedByBoundary(String email) {
        this.email = email;
    }

    // Default constructor
    public CreatedByBoundary() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CreatedByBoundary [email=" + email + "]";
    }
}