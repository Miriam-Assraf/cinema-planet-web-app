package backend.cinemaplanet.logic.elementService;

import java.util.Collection;
import java.util.List;
import com.miriam.assraf.backend.view.ElementBoundary;

public interface EnhancedElementService extends ElementService {
    public void addChildToParent(String managerEmail, String parentId, String childId);

    public Collection<ElementBoundary> getChildren(String userEmail, String parentId, int size, int page);

    public Collection<ElementBoundary> getParents(String userEmail, String childId, int size, int page);

    public List<ElementBoundary> getAll(String userEmail, int size, int page);

    public List<ElementBoundary> getElementsByName(String userEmail, String elementName, int size, int page);

    public List<ElementBoundary> getElementsByType(String userEmail, String elementType, int size, int page);
}
