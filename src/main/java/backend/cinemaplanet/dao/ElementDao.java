package backend.cinemaplanet.dao;

import java.util.List;

import com.miriam.assraf.backend.data.ElementEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, Long> {
    // for PLAYER only active elements
    // SELECT * FROM ELEMENTS WHERE ACTIVE = ?
    public List<ElementEntity> findAllByActive(@Param("active") boolean active, Pageable pageable);

    // SELECT * FROM elements WHERE Parents with elementId=?
    public List<ElementEntity> findAllByParents_elementId(@Param("parentId") Long parentId, Pageable pageable); // list-
                                                                                                                // we
                                                                                                                // want
                                                                                                                // it to
                                                                                                                // be
                                                                                                                // ordered
    // for PLAYER only active elements

    public List<ElementEntity> findAllByParents_elementId_AndActiveIsTrue(@Param("parentId") Long parentId,
            Pageable pageable); // list- we want it to be ordered

    public List<ElementEntity> findAllByChildren_elementId(@Param("childId") Long childId, Pageable pageable); // list-
                                                                                                               // we
                                                                                                               // want
                                                                                                               // it to
                                                                                                               // be
                                                                                                               // ordered
    // for PLAYER only active elements

    public List<ElementEntity> findAllByChildren_elementId_AndActiveIsTrue(@Param("childId") Long childId,
            Pageable pageable); // list- we want it to be ordered

    // SELECT * FROM elements WHERE Name LIKE name=?
    public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);

    // for PLAYER only active elements
    public List<ElementEntity> findAllByNameLikeAndActiveIsTrue(@Param("name") String name, Pageable pageable);

    public List<ElementEntity> findAllByTypeLike(@Param("type") String type, Pageable pageable);

    // for PLAYER only active elements
    public List<ElementEntity> findAllByTypeLikeAndActiveIsTrue(@Param("type") String type, Pageable pageable);

    // get child by x
    public ElementEntity findByParents_elementId_AndLngLikeAndActiveIsTrue(@Param("parentId") Long parentId,
            @Param("lng") double lng);

    // get child by y
    public ElementEntity findByParents_elementId_AndLatLikeAndActiveIsTrue(@Param("parentId") Long parentId,
            @Param("lat") double lat);

    // get child by x between range
    public ElementEntity findByParents_elementId_AndLngBetweenAndActiveIsTrue(@Param("parentId") Long parentId,
            @Param("lngStart") double lngStart, @Param("lngEnd") double lngEnd);

}
