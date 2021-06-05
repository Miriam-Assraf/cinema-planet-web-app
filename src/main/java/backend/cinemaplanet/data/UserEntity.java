package backend.cinemaplanet.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.miriam.assraf.backend.logic.validators.EmailValidator;

//Table name: USERS
@Entity
@Table(name = "USERS")
public class UserEntity {
    private String email;
    private RoleEntity role;
    private String username;
    private String avatar;

    public UserEntity() {
    }

    @Id
    @EmailValidator
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Enumerated(EnumType.STRING)
    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    @NotNull(message = "username can't be null")
    @NotEmpty(message = "username can't be empty")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull(message = "avatar can't be null")
    @NotEmpty(message = "avatar can't be empty")
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
