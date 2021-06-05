package backend.cinemaplanet.logic.actionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.miriam.assraf.backend.dao.ActionDao;
import com.miriam.assraf.backend.data.ActionEntity;
import com.miriam.assraf.backend.data.utils.ActionEntityConverter;
import com.miriam.assraf.backend.logic.actions.GetAvailableSeatsAction;
import com.miriam.assraf.backend.logic.actions.GetMiddleRowAction;
import com.miriam.assraf.backend.logic.actions.GetMiddleSeatAction;
import com.miriam.assraf.backend.logic.actions.GetRowByDistanceAction;
import com.miriam.assraf.backend.logic.actions.OrderSeatAction;
import com.miriam.assraf.backend.logic.exceptions.ForbiddenException;
import com.miriam.assraf.backend.logic.userService.UserDataAccessService;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.RoleBoundary;
import com.miriam.assraf.backend.view.UserBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionDataAccessService implements EnhancedActionService {
    private ActionDao actionDao;
    private ActionEntityConverter entityConverter;
    private UserDataAccessService userService;
    private GetMiddleSeatAction getMiddleSeat;
    private GetAvailableSeatsAction getAvailableSeats;
    private GetRowByDistanceAction getRowByDistance;
    private GetMiddleRowAction getMiddleRow;
    private OrderSeatAction orderSeat;

    @Autowired
    public ActionDataAccessService(ActionDao actionDao, UserDataAccessService userService) {
        this.actionDao = actionDao;
        this.userService = userService;
    }

    @Autowired
    public void setEntityConverter(ActionEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Autowired
    public void setGetMiddleSeatAction(GetMiddleSeatAction getMiddleSeat) {
        this.getMiddleSeat = getMiddleSeat;
    }

    @Autowired
    public void setGetAvailableSeatAction(GetAvailableSeatsAction getAvailableSeats) {
        this.getAvailableSeats = getAvailableSeats;
    }

    @Autowired
    public void setGetRowByDistanceAction(GetRowByDistanceAction getRowByDistance) {
        this.getRowByDistance = getRowByDistance;
    }

    @Autowired
    public void setGetMiddleRow(GetMiddleRowAction getMiddleRow) {
        this.getMiddleRow = getMiddleRow;
    }

    @Autowired
    public void setOrderSeat(OrderSeatAction orderSeat) {
        this.orderSeat = orderSeat;
    }

    @Override
    @Transactional
    public Object invokeAction(ActionBoundary actionBoundary) {
        UserBoundary user = userService.login(actionBoundary.getInvokedBy().getEmail());
        Object res = null;

        if (user.getRole() == RoleBoundary.MANAGER || user.getRole() == RoleBoundary.ADMIN) { // actions for players only
            throw new ForbiddenException("Unauthorized to invoke new action.");
        }
            // create new tuple in idValue table with non-used id
            ActionEntity actionEntity = this.entityConverter.convertToEntity(actionBoundary);
            // LastIdValue idValue = this.lastValueDao.save(new LastIdValue());

            // actionEntity.setActionId(idValue.getLastId());
            actionEntity.setCreatedTimestamp(new Date());
            actionEntity.setInvokedBy(actionBoundary.getInvokedBy().getEmail());
            // this.lastValueDao.delete(idValue); // clean redundant data

            actionEntity = this.actionDao.save(actionEntity);

            switch (actionBoundary.getType()) {
                case "get middle seat":
                    res = this.getMiddleSeat.doAction(actionBoundary);
                    break;
                case "get middle row":
                    res = this.getMiddleRow.doAction(actionBoundary);
                    break;
                case "get available seats":
                    res = this.getAvailableSeats.doAction(actionBoundary);
                    break;
                case "order seat":
                    res = this.orderSeat.doAction(actionBoundary);
                    break;
                case "get row by distance":
                    res = this.getRowByDistance.doAction(actionBoundary);
                    break;
                default:
                    break; // do nothing
            }
            return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminEmail) {
        List<ActionBoundary> rv = new ArrayList<>();
        Iterable<ActionEntity> content = this.actionDao.findAll();
        for (ActionEntity action : content) {
            rv.add(this.entityConverter.convertFromEntity(action));
        }
        return rv;
    }

    @Override
    @Transactional
    public void deleteAllActions(String adminEmail) {
        UserBoundary user = userService.login(adminEmail);

        if (user.getRole() == RoleBoundary.ADMIN) {
            this.actionDao.deleteAll();
        } else
            throw new ForbiddenException("Unauthorized to delete all actions.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionBoundary> getAllActions(String adminEmail, int size, int page) {
        UserBoundary user = userService.login(adminEmail);

        if (user.getRole() == RoleBoundary.ADMIN) {
            return this.actionDao
                    .findAll(PageRequest.of(page, size, Direction.ASC, "type", "createdTimestamp", "actionId")) // Page<ActionEntity>
                    .getContent().stream() // Stream<ActionEntity>
                    .map(this.entityConverter::convertFromEntity) // convert to Stream<ActionBoundary>
                    .collect(Collectors.toList()); // back to List<ActionBoundary>
        } else
            throw new ForbiddenException("Unauthorized to get all actions.");
    }
}