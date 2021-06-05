package backend.cinemaplanet.data.utils;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miriam.assraf.backend.data.ActionEntity;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.ActionElementBoundary;
import com.miriam.assraf.backend.view.CreatedByBoundary;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class ActionEntityConverter {

    private ObjectMapper jackson;

    @PostConstruct
    public void setup() {
        this.jackson = new ObjectMapper();
    }

    public ActionBoundary convertFromEntity(ActionEntity actionEntity) {
        ActionBoundary actionBoundary = new ActionBoundary();

        actionBoundary.setActionId(LongAndStringConverter.convertToString(actionEntity.getActionId()));
        actionBoundary.setCreatedTimestamp(actionEntity.getCreatedTimestamp());
        actionBoundary.setType(actionEntity.getType());
        actionBoundary.setInvokedBy(new CreatedByBoundary(actionEntity.getInvokedBy()));
        actionBoundary.setElement(new ActionElementBoundary(actionEntity.getElement()));

        // unmarshalling
        try {
            if (actionEntity.getActionAttributes() != null) { // action attributes can be null
                actionBoundary.setActionAttributes(this.jackson.readValue(actionEntity.getActionAttributes(),
                        new TypeReference<Map<String, Object>>() {
                        }));
            } else {
                actionBoundary.setActionAttributes(null);
            }
        } catch (Exception exeption) {
            throw new RuntimeException(exeption);
        }

        return actionBoundary;
    }

    public ActionEntity convertToEntity(ActionBoundary actionBoundary) {
        ActionEntity actionEntity = new ActionEntity();

        if (actionBoundary.getActionId() != null) { // initalized as null until service update it with new id
            Long actionId = LongAndStringConverter.convertToLong(actionBoundary.getActionId());
            actionEntity.setActionId(actionId);
        } else {
            actionEntity.setActionId(null);
        }
        if (actionBoundary.getCreatedTimestamp() != null) { // initialized as null until service update it with date
                                                            // created
            actionEntity.setCreatedTimestamp(actionBoundary.getCreatedTimestamp());
        } else {
            actionEntity.setCreatedTimestamp(null);
        }
        actionEntity.setInvokedBy(actionBoundary.getInvokedBy().getEmail());
        actionEntity.setElement(actionBoundary.getElement().getElementId());
        actionEntity.setType(actionBoundary.getType());

        // marshalling
        try {
            if (actionBoundary.getActionAttributes() != null) { // action attributes can be null
                actionEntity.setActionAttributes(this.jackson.writeValueAsString(actionBoundary.getActionAttributes()));
            } else {
                actionEntity.setActionAttributes(null);
            }

        } catch (Exception exeption) {
            throw new RuntimeException(exeption);
        }

        return actionEntity;
    }

}