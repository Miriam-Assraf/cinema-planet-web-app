package backend.cinemaplanet.logic.userService;

import java.util.List;
import com.miriam.assraf.backend.view.UserBoundary;

public interface EnhancedUserService extends UserService {
    public List<UserBoundary> getAllUsers(String adminEmail, int size, int page);
}