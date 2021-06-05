package backend.cinemaplanet.logic.actionService;

import java.util.List;
import com.miriam.assraf.backend.view.ActionBoundary;

public interface ActionService {
    public Object invokeAction(ActionBoundary action);

    public List<ActionBoundary> getAllActions(String adminEmail);

    public void deleteAllActions(String adminEmail);
}