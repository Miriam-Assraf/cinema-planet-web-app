package backend.cinemaplanet.logic.actionService;

import java.util.List;
import com.miriam.assraf.backend.view.ActionBoundary;

public interface EnhancedActionService extends ActionService {
    public List<ActionBoundary> getAllActions(String adminEmail, int size, int page);
}
