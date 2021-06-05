package backend.cinemaplanet.logic.userService;

import java.util.List;
import com.miriam.assraf.backend.view.UserBoundary;

public interface UserService {
    public UserBoundary createUser(UserBoundary user);

    public UserBoundary login(String userEmail);

    public UserBoundary updateUser(String userEmail, UserBoundary update);

    public List<UserBoundary> getAllUsers(String adminEmail);

    public void deleteAllUsers(String adminEmail);
}
