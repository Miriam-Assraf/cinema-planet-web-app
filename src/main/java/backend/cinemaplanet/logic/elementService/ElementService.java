package backend.cinemaplanet.logic.elementService;

import java.util.List;
import com.miriam.assraf.backend.view.ElementBoundary;

public interface ElementService {
    public ElementBoundary create(String managerEmail, ElementBoundary element);

    public ElementBoundary update(String managerEmail, String elementId, ElementBoundary update);

    public List<ElementBoundary> getAll(String userEmail);

    public ElementBoundary getSpecificElement(String userEmail, String elementId);

    public void deleteAllElements(String adminEmail);
}