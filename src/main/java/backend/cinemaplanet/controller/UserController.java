package backend.cinemaplanet.controller;

import com.miriam.assraf.backend.logic.userService.EnhancedUserService;
import com.miriam.assraf.backend.view.UserBoundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com")
public class UserController {
    private EnhancedUserService userService;

    // injection
    @Autowired
    public UserController(EnhancedUserService userService) {
        super();
        this.userService = userService;
    }

    // GET --> login user by email and get user details
    @RequestMapping(path = "/acs/users/login/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary login(@PathVariable("userEmail") String userEmail) {
        return this.userService.login(userEmail);
    }

    // POST --> create new user and store in system (SQL: INSERT)
    @RequestMapping(path = "/acs/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary CreateUser(@RequestBody UserBoundary user) {
        return this.userService.createUser(user);
    }

    // PUT --> update user details
    @RequestMapping(path = "/acs/users/{userEmail}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@PathVariable("userEmail") String userEmail, @RequestBody UserBoundary update) {
        this.userService.updateUser(userEmail, update);
    }
}
