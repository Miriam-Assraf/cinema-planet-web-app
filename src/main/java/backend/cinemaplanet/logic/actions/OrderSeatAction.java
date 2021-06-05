package backend.cinemaplanet.logic.actions;

import java.util.Map;
import com.miriam.assraf.backend.dao.ElementDao;
import com.miriam.assraf.backend.data.utils.ElementEntityConverter;
import com.miriam.assraf.backend.logic.actionService.ActionInterface;
import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.logic.exceptions.NotFoundException;
import com.miriam.assraf.backend.logic.userService.EnhancedUserService;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.ElementBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderSeatAction implements ActionInterface {
    private EnhancedElementService elementService;
    private EnhancedUserService userService;
    private ElementDao elementDao;
    private Map<String, Object> update;
    private ElementEntityConverter entityConverter;

    @Autowired
    public OrderSeatAction(ElementDao elementDao, EnhancedElementService elementService,
            EnhancedUserService userService) {
        this.elementService = elementService;
        this.elementDao = elementDao;
        this.userService = userService;

    }

    @Autowired
    public void setEntityConverter(ElementEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Override
    @Transactional
    public Object doAction(ActionBoundary action) {
        try {
            // get specific seat
            com.miriam.assraf.backend.view.UserBoundary user = userService.login(action.getInvokedBy().getEmail());
            ElementBoundary element = elementService.getSpecificElement(user.getEmail(),
                    action.getElement().getElementId());
            // update seat status - available = false
            update = element.getElementAttributes();
            update.put("available", false);
            element.setElementAttributes(update); // change available attribute
            this.elementDao.save(entityConverter.convertToEntity(element));
            return element;

        } catch (Exception e) {
            return new NotFoundException("Sorry, Can't order this seat");
        }
    }
}
