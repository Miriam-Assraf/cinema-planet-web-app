package backend.cinemaplanet.logic.actions;

import java.util.List;
import java.util.stream.Collectors;

import com.miriam.assraf.backend.dao.ElementDao;
import com.miriam.assraf.backend.data.utils.ElementEntityConverter;
import com.miriam.assraf.backend.data.utils.LongAndStringConverter;
import com.miriam.assraf.backend.logic.actionService.ActionInterface;
import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.logic.userService.EnhancedUserService;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.ElementBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetAvailableSeatsAction implements ActionInterface {
    private ElementDao elementDao; // Data access object
    private ElementEntityConverter entityConverter;
    private EnhancedElementService elementService;
    private EnhancedUserService userService;
    private int maxNumSeats = 25;

    @Autowired
    public GetAvailableSeatsAction(ElementDao elementDao, EnhancedElementService elementService,
            EnhancedUserService userService) {
        this.elementDao = elementDao;
        this.elementService = elementService;
        this.userService = userService;
    }

    @Autowired
    public void setEntityConverter(ElementEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Object doAction(ActionBoundary action) {
        com.miriam.assraf.backend.view.UserBoundary user = userService.login(action.getInvokedBy().getEmail());
        ElementBoundary element = elementService.getSpecificElement(user.getEmail(),
                action.getElement().getElementId()); // if user role is PLAYER and element is not active, will
        List<ElementBoundary> allSeatsInRow = this.elementDao.findAllByParents_elementId_AndActiveIsTrue( // only player
                                                                                                          // is allowed
                                                                                                          // to invoke
                                                                                                          // action
                LongAndStringConverter.convertToLong(element.getElementId()),
                PageRequest.of(0, maxNumSeats, Direction.ASC, "name", "elementId")).stream()
                .map(this.entityConverter::convertFromEntity).collect(Collectors.toList());
        // get all in list which attribute has key available with value true
        return allSeatsInRow.stream() // stream over each seat element
                .filter(seat -> seat.getElementAttributes().keySet().stream() // stream over each key of
                                                                              // elementAttributes of seat
                        .anyMatch(attribute -> attribute.equals("available") && // if key='available'
                                Boolean.TRUE.equals(seat.getElementAttributes().get(attribute)))) // and key value is
                                                                                                  // true
                .collect(Collectors.toList()); // return those seats as a list of available seats
    }
}
