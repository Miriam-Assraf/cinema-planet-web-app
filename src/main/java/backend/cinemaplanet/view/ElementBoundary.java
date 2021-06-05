package backend.cinemaplanet.view;

import java.util.Date;
import java.util.Map;

public class ElementBoundary {
    private String elementId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private CreatedByBoundary createdBy;
    private LocationBoundary location;
    private Map<String, Object> elementAttributes;

    // Constructor for STUBBING
    public ElementBoundary(String type, String name, Boolean active, LocationBoundary location,
            Map<String, Object> elementAttributes) {
        this.type = type;
        this.name = name;
        this.active = active;
        this.location = location;
        this.elementAttributes = elementAttributes;
    }

    // Default constructor
    public ElementBoundary() {
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public CreatedByBoundary getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedByBoundary createdBy) {
        this.createdBy = createdBy;
    }

    public LocationBoundary getLocation() {
        return location;
    }

    public void setLocation(LocationBoundary location) {
        this.location = location;
    }

    public Map<String, Object> getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(Map<String, Object> elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    @Override
    public String toString() {
        return "ElementBoundary [elementId=" + elementId + ", type=" + type + ", name=" + name + ", active=" + active
                + ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", location=" + location
                + ", elementAttributes=" + elementAttributes + "]";
    }

}
