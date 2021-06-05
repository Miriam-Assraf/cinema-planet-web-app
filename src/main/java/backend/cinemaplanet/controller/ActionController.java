package backend.cinemaplanet.controller;

import com.miriam.assraf.backend.logic.actionService.EnhancedActionService;
import com.miriam.assraf.backend.view.ActionBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com")
public class ActionController {
    private EnhancedActionService actionService;

    // injection
    @Autowired
    public ActionController(EnhancedActionService actionService) {
        super();
        this.actionService = actionService;
    }

    // POST --> create new action
    @RequestMapping(path = "/acs/actions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeNewAction(@RequestBody ActionBoundary action) {

        return this.actionService.invokeAction(action);
    }
}