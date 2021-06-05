package backend.cinemaplanet.view;

import java.util.Date;
import java.util.Map;

public class ActionBoundary {
    private String actionId;
    private String type;
    private ActionElementBoundary element;
    private Date createdTimestamp;
    private CreatedByBoundary invokedBy;
    private Map<String, Object> actionAttributes;

    // Default constructor
    public ActionBoundary() {
    }

    public ActionBoundary(String type, ActionElementBoundary element, CreatedByBoundary invokedBy,
            Map<String, Object> actionAttributes) {
        super();
        this.type = type;
        this.element = element;
        this.invokedBy = invokedBy;
        this.actionAttributes = actionAttributes;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ActionElementBoundary getElement() {
        return element;
    }

    public void setElement(ActionElementBoundary element) {
        this.element = element;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public CreatedByBoundary getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(CreatedByBoundary invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getActionAttributes() {
        return actionAttributes;
    }

    public void setActionAttributes(Map<String, Object> actionAttributes) {
        this.actionAttributes = actionAttributes;
    }

}