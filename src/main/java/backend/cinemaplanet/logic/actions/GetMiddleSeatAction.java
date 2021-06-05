package backend.cinemaplanet.logic.actions;

import com.miriam.assraf.backend.dao.ElementDao;
import com.miriam.assraf.backend.data.utils.ElementEntityConverter;
import com.miriam.assraf.backend.data.utils.LongAndStringConverter;
import com.miriam.assraf.backend.logic.actionService.ActionInterface;
import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.logic.userService.EnhancedUserService;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.ElementBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetMiddleSeatAction implements ActionInterface {
    private ElementDao elementDao; // Data access object
    private ElementEntityConverter entityConverter;
    private EnhancedElementService elementService;
    private EnhancedUserService userService;

    @Autowired
    public GetMiddleSeatAction(ElementDao elementDao, EnhancedElementService elementService,
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
        return this.entityConverter.convertFromEntity(this.elementDao.findByParents_elementId_AndLngLikeAndActiveIsTrue(
                LongAndStringConverter.convertToLong(element.getElementId()), 0.0));
    }
}