package backend.cinemaplanet.data.utils;

import com.miriam.assraf.backend.data.RoleEntity;
import com.miriam.assraf.backend.data.UserEntity;
import com.miriam.assraf.backend.view.RoleBoundary;
import com.miriam.assraf.backend.view.UserBoundary;

import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public UserBoundary convertFromEntity(UserEntity userEntity) {
        UserBoundary userBoundary = new UserBoundary();

        userBoundary.setEmail(userEntity.getEmail());
        userBoundary.setUsername(userEntity.getUsername());
        userBoundary.setAvatar(userEntity.getAvatar());

        if (userEntity.getRole() != null) {
            userBoundary.setRole(RoleBoundary.valueOf(userEntity.getRole().name().toUpperCase()));
        }

        return userBoundary;
    }

    public UserEntity convertToEntity(UserBoundary userBoundary) {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(userBoundary.getEmail());
        userEntity.setUsername(userBoundary.getUsername());
        userEntity.setAvatar(userBoundary.getAvatar());

        if (userBoundary.getRole() != null) {
            userEntity.setRole(RoleEntity.valueOf(userBoundary.getRole().name().toLowerCase()));
        }

        return userEntity;
    }

}