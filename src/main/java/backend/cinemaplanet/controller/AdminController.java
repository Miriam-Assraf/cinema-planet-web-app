package backend.cinemaplanet.controller;

import com.miriam.assraf.backend.logic.actionService.EnhancedActionService;
import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.logic.userService.EnhancedUserService;
import com.miriam.assraf.backend.view.ActionBoundary;
import com.miriam.assraf.backend.view.UserBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com")
public class AdminController {
    private EnhancedActionService actionService;
    private EnhancedUserService userService;
    private EnhancedElementService elementService;

    // injection
    @Autowired
    public AdminController(EnhancedActionService actionService, EnhancedUserService userService,
            EnhancedElementService elementService) {
        super();
        this.actionService = actionService;
        this.userService = userService;
        this.elementService = elementService;
    }

    // GET --> export all users
    @RequestMapping(path = "/acs/admin/users/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary[] exportAllUsers(@PathVariable("adminEmail") String adminEmail,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size, // not required, will return
                                                                                          // 10 elements as default
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) { // not required, will return
                                                                                           // first page as default
        return this.userService.getAllUsers(adminEmail, size, page).toArray(new UserBoundary[0]);
    }

    // GET --> export all actions
    @RequestMapping(path = "/acs/admin/actions/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionBoundary[] exportAllActions(@PathVariable("adminEmail") String adminEmail,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size, // not required, will return
                                                                                          // 10 elements as default
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) { // not required, will return
                                                                                           // first page as default
        return this.actionService.getAllActions(adminEmail, size, page).toArray(new ActionBoundary[0]);
    }

    // DELETE --> delete all users in the system
    @RequestMapping(path = "/acs/admin/users/{adminEmail}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAllUsers(@PathVariable("adminEmail") String adminEmail) {
        this.userService.deleteAllUsers(adminEmail);
    }

    // DELETE --> delete all elements in the system
    @RequestMapping(path = "/acs/admin/elements/{adminEmail}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAllElements(@PathVariable("adminEmail") String adminEmail) {
        this.elementService.deleteAllElements(adminEmail);
    }

    // DELETE --> delete all actions in the system
    @RequestMapping(path = "/acs/admin/actions/{adminEmail}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAllActions(@PathVariable("adminEmail") String adminEmail) {
        this.actionService.deleteAllActions(adminEmail);
    }
}
