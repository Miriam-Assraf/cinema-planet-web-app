package backend.cinemaplanet.logic.userService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.miriam.assraf.backend.dao.UserDao;
import com.miriam.assraf.backend.data.UserEntity;
import com.miriam.assraf.backend.data.utils.UserEntityConverter;
import com.miriam.assraf.backend.logic.exceptions.ForbiddenException;
import com.miriam.assraf.backend.logic.exceptions.NotFoundException;
import com.miriam.assraf.backend.view.RoleBoundary;
import com.miriam.assraf.backend.view.UserBoundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDataAccessService implements EnhancedUserService {
    private UserDao userDao; // Data access object
    private UserEntityConverter entityConverter;

    @Autowired
    public UserDataAccessService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setEntityConverter(UserEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Override
    @Transactional // (readOnly = false)
    public UserBoundary createUser(UserBoundary userBoundary) {
        if (userBoundary.getEmail() != null && userBoundary.getUsername() != null && userBoundary.getRole() != null) {
            UserEntity userEntity = this.entityConverter.convertToEntity(userBoundary);
            // UPSERT: SELECT -> UPDATE / INSERT
            userEntity = this.userDao.save(userEntity);

            return this.entityConverter.convertFromEntity(userEntity);
        } else {
            throw new NotFoundException("Can't create new user without email, usrname or role values.\n");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary login(String userEmail) {
        // SELECT * FROM USERS WHERE EMAIL=userEmail
        Optional<UserEntity> userEntity = this.userDao.findById(userEmail);

        if (userEntity.isPresent()) {
            return this.entityConverter.convertFromEntity(userEntity.get());
        } else {
            throw new NotFoundException("User email: " + userEmail + " not found.\n");
        }
    }

    @Override
    @Transactional // (readOnly = false)
    public UserBoundary updateUser(String userEmail, UserBoundary update) {
        UserBoundary existingUser = this.login(userEmail);

        if (update.getUsername() != null) {
            existingUser.setUsername(update.getUsername());
        }
        if (update.getRole() != null) {
            // if invalid enum value, java throws exception
            existingUser.setRole(update.getRole());
        }
        if (update.getAvatar() != null) {
            existingUser.setAvatar(update.getAvatar());
        }

        this.userDao.save(this.entityConverter.convertToEntity(existingUser));

        return existingUser;
    }

    @Override
    // have database handle race conditions
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminEmail) {
        // ON INIT - create new Transaction

        List<UserBoundary> rv = new ArrayList<>();

        // SELECT * FROM USERS
        Iterable<UserEntity> content = this.userDao.findAll();
        for (UserEntity user : content) {
            rv.add(this.entityConverter.convertFromEntity(user));
        }

        // On SUCCESS - commit transaction
        // On Error - rollback transaction
        return rv;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminEmail, int size, int page) {
        UserBoundary user = login(adminEmail);

        if (user.getRole() == RoleBoundary.ADMIN) {
            return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "email")) // Page<UserEntity>
                    .getContent().stream() // Stream<UserEntity>
                    .map(this.entityConverter::convertFromEntity) // convert to Stream<UserBoundary>
                    .collect(Collectors.toList()); // back to List<UserBoundary>
        } else
            throw new ForbiddenException("Anauthorized to get all users.");
    }

    @Override
    @Transactional // (readOnly = false)
    public void deleteAllUsers(String adminEmail) {
        UserBoundary user = login(adminEmail);

        if (user.getRole() == RoleBoundary.ADMIN) {
            this.userDao.deleteAll();
        } else
            throw new ForbiddenException("Anauthorized to delete all users.");

    }

}
