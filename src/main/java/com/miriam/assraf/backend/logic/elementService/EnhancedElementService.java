package com.miriam.assraf.backend.logic.elementService;

import java.util.Collection;
import java.util.List;
import com.miriam.assraf.backend.view.ElementBoundary;

public interface EnhancedElementService extends ElementService {
    public void addChildToParent(String managerEmail, String parentId, String childId);

    public Collection<ElementBoundary> getChildren(String parentId, int size, int page);

    public Collection<ElementBoundary> getParents(String childId, int size, int page);

    public List<ElementBoundary> getAll(int size, int page);

    public List<ElementBoundary> getElementsByName(String elementName, int size, int page);

    public List<ElementBoundary> getElementsByType(String elementType, int size, int page);

    public List<ElementBoundary> getElementsByNameAndType(String elementName, String elementType, int size, int page);
}
