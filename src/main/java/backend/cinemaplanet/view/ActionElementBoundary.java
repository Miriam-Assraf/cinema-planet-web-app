package backend.cinemaplanet.view;

public class ActionElementBoundary {
    private String elementId;

    public ActionElementBoundary(String elementId) {
        this.elementId = elementId;
    }

    public ActionElementBoundary() {
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public String toString() {
        return "ActionElementBoundary [elementId=" + elementId + "]";
    }
}