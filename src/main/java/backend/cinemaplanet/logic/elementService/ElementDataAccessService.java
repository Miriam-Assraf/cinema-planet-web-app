package backend.cinemaplanet.logic.elementService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.miriam.assraf.backend.dao.ElementDao;
import com.miriam.assraf.backend.data.ElementEntity;
import com.miriam.assraf.backend.data.utils.DoubleAndStringConverter;
import com.miriam.assraf.backend.data.utils.ElementEntityConverter;
import com.miriam.assraf.backend.data.utils.LongAndStringConverter;
import com.miriam.assraf.backend.logic.exceptions.ForbiddenException;
import com.miriam.assraf.backend.logic.exceptions.NotFoundException;
import com.miriam.assraf.backend.logic.userService.UserDataAccessService;
import com.miriam.assraf.backend.view.ElementBoundary;
import com.miriam.assraf.backend.view.RoleBoundary;
import com.miriam.assraf.backend.view.UserBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ElementDataAccessService implements EnhancedElementService {
    private ElementDao elementDao; // Data access object
    private ElementEntityConverter entityConverter;
    private UserDataAccessService userService;

    @Autowired
    public ElementDataAccessService(ElementDao elementDao, UserDataAccessService userService) {
        this.elementDao = elementDao;
        this.userService = userService;
    }

    @Autowired
    public void setEntityConverter(ElementEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Override
    @Transactional
    public ElementBoundary create(String managerEmail, ElementBoundary element) {
        // check user is manager
        UserBoundary user = userService.login(managerEmail); // will throw exception by user service if user doesn't
                                                             // exist in server
        if (user.getRole() == RoleBoundary.MANAGER) {
            // create new tupple in idValue table with non-used id
            // LastIdValue idValue = this.lastValueDao.save(new LastIdValue());
            ElementEntity elementEntity = this.entityConverter.convertToEntity(element);

            // elementEntity.setElementId(idValue.getLastId()); // use new id
            elementEntity.setCreatedTimestamp(new Date());
            elementEntity.setCreatedBy(managerEmail);
            // this.lastValueDao.delete(idValue); // clean redundant data

            elementEntity = this.elementDao.save(elementEntity);

            return this.entityConverter.convertFromEntity(elementEntity);
        } else
            throw new ForbiddenException("Uunothorized to create new element.");
    }

    @Override
    @Transactional
    public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update) {
        // check user is manager
        UserBoundary user = userService.login(managerEmail); // will throw exception by user service if user doesn't
                                                             // exist in server
        if (user.getRole() == RoleBoundary.MANAGER) {
            ElementEntity existingElement = this.elementDao.findById(LongAndStringConverter.convertToLong(elementId))
                    .orElseThrow(() -> new NotFoundException());

            if (update.getType() != null) {
                existingElement.setType(update.getType());
            }

            if (update.getName() != null) {
                existingElement.setName(update.getName());
            }

            if (update.getActive() != null) {
                existingElement.setActive(update.getActive());
            }

            if (update.getLocation() != null) {
                existingElement.setLat(DoubleAndStringConverter.convertToDouble(update.getLocation().getLat()));
                existingElement.setLng(DoubleAndStringConverter.convertToDouble(update.getLocation().getLng()));
            }

            if (update.getElementAttributes() != null) {
                existingElement.setElementAttributes(
                        this.entityConverter.convertAttributesToEntity(update.getElementAttributes()));
            }

            existingElement.getChildren().size();
            existingElement.getParents().size();
            // existingElement.getChildren().forEach(c->this.elementDao.save(c));
            // existingElement.getParents().forEach(p->this.elementDao.save(p));
            this.elementDao.save(existingElement);

            return this.entityConverter.convertFromEntity(existingElement);
        } else
            throw new ForbiddenException("Uunothorized to update element.");
    }

    @Override
    @Transactional(readOnly = true)
    public ElementBoundary getSpecificElement(String userEmail, String elementId) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        // SELECT * FROM MESSAGES WHERE ID=?
        Optional<ElementEntity> elementEntity = this.elementDao.findById(Long.parseLong(elementId));

        if (elementEntity.isPresent()) {
            if (user.getRole() == RoleBoundary.PLAYER && !elementEntity.get().getActive()) {
                throw new NotFoundException("could not find element with id: " + elementId);
            } else if ((user.getRole() != RoleBoundary.PLAYER && !elementEntity.get().getActive())
                    || user.getRole() != RoleBoundary.MANAGER) {
                return this.entityConverter.convertFromEntity(elementEntity.get());
            } else {
                throw new ForbiddenException("Unauthorized to retrieve element."); // not player or manager
            }
        } else {
            throw new NotFoundException("could not find element with id: " + elementId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementBoundary> getAll(String userEmail) {
        // ON INIT - create new Transaction
        List<ElementBoundary> rv = new ArrayList<>();

        // SELECT * FROM ELEMENTS
        Iterable<ElementEntity> elements = this.elementDao.findAll();
        for (ElementEntity element : elements) {
            rv.add(this.entityConverter.convertFromEntity(element));
        }

        // On SUCCESS - commit transaction
        // On Error - rollback transaction
        return rv;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementBoundary> getAll(String userEmail, int size, int page) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        // chose to sort by type and the name, because our elements are cinemas, movies,
        // theaters
        if (user.getRole() == RoleBoundary.MANAGER) {
            return this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "type", "elementId")).getContent()
                    .stream() // Stream<ElementEntity>
                    .map(this.entityConverter::convertFromEntity) // convert to Stream<ElementBoundary>
                    .collect(Collectors.toList()); // back to List<ElementBoundary>
        } else if (user.getRole() == RoleBoundary.PLAYER) { // return only active elements
            return this.elementDao.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "type", "elementId")) // Page<ElementEntity>
                    .stream() // Stream<ElementEntity>
                    .map(this.entityConverter::convertFromEntity) // convert to Stream<ElementBoundary>
                    .collect(Collectors.toList()); // back to List<ElementBoundary>
        } else
            throw new ForbiddenException("Unauthorized to get all elements."); // not player or manager
    }

    @Override
    @Transactional
    public void deleteAllElements(String adminEmail) {
        UserBoundary user = userService.login(adminEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.ADMIN) {
            this.elementDao.deleteAll();
        } else
            throw new ForbiddenException("Unauthorized to delete all elements.");
    }

    @Override
    @Transactional
    public void addChildToParent(String managerEmail, String parentId, String childId) {
        UserBoundary user = userService.login(managerEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.MANAGER) {
            ElementEntity parent = this.elementDao.findById(LongAndStringConverter.convertToLong(parentId))
                    .orElseThrow(() -> new NotFoundException("could not find element with id: " + parentId));

            ElementEntity child = this.elementDao.findById(LongAndStringConverter.convertToLong(childId))
                    .orElseThrow(() -> new NotFoundException("could not find element with id: " + childId));

            parent.addChild(child);

            this.elementDao.save(parent);
        } else
            throw new ForbiddenException("Unauthorized to connect between elements.");
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ElementBoundary> getChildren(String userEmail, String parentId, int size, int page) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.MANAGER) {
            return this.elementDao
                    .findAllByParents_elementId(LongAndStringConverter.convertToLong(parentId),
                            PageRequest.of(page, size, Direction.ASC, "type", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else if (user.getRole() == RoleBoundary.PLAYER) {
            return this.elementDao
                    .findAllByParents_elementId_AndActiveIsTrue(LongAndStringConverter.convertToLong(parentId),
                            PageRequest.of(page, size, Direction.ASC, "type", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else
            throw new ForbiddenException();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ElementBoundary> getParents(String userEmail, String childId, int size, int page) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.MANAGER) {
            return this.elementDao
                    .findAllByChildren_elementId(LongAndStringConverter.convertToLong(childId),
                            PageRequest.of(page, size, Direction.ASC, "type", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else if (user.getRole() == RoleBoundary.PLAYER) {
            return this.elementDao
                    .findAllByChildren_elementId_AndActiveIsTrue(LongAndStringConverter.convertToLong(childId),
                            PageRequest.of(page, size, Direction.ASC, "type", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else
            throw new ForbiddenException();
    }

    /*
     * @Transactional(readOnly=true) public Set<ElementBoundary> getParents(String
     * childId) { ElementEntity child =
     * this.elementDao.findById(LongAndStringConverter.toEntityId(childId)).
     * orElseThrow(()->new
     * MessageNotFoundException("could not find element with id: "+childId));
     * 
     * return
     * child.getParents().stream().map(this.entityConverter::convertFromEntity).
     * collect(Collectors.toSet()); }
     */

    @Override
    @Transactional(readOnly = true)
    public List<ElementBoundary> getElementsByName(String userEmail, String elementName, int size, int page) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.MANAGER) {
            return this.elementDao
                    .findAllByNameLike(elementName,
                            PageRequest.of(page, size, Direction.ASC, "type", "name", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else if (user.getRole() == RoleBoundary.PLAYER) {
            return this.elementDao
                    .findAllByNameLikeAndActiveIsTrue(elementName,
                            PageRequest.of(page, size, Direction.ASC, "type", "name", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else
            throw new ForbiddenException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementBoundary> getElementsByType(String userEmail, String elementType, int size, int page) {
        UserBoundary user = userService.login(userEmail); // will throw exception by user service if user doesn't

        if (user.getRole() == RoleBoundary.MANAGER) {
            return this.elementDao
                    .findAllByTypeLike(elementType, PageRequest.of(page, size, Direction.ASC, "name", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else if (user.getRole() == RoleBoundary.PLAYER) {
            return this.elementDao
                    .findAllByTypeLikeAndActiveIsTrue(elementType,
                            PageRequest.of(page, size, Direction.ASC, "name", "elementId"))
                    .stream().map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        } else
            throw new ForbiddenException();
    }
}
