package backend.cinemaplanet.dao;

import com.miriam.assraf.backend.data.UserEntity;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDao extends PagingAndSortingRepository<UserEntity, String> {
}
