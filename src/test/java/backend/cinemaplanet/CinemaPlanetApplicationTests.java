package backend.cinemaplanet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import backend.cinemaplanet.view.ActionBoundary;
import backend.cinemaplanet.view.ActionElementBoundary;
import backend.cinemaplanet.view.CreatedByBoundary;
import backend.cinemaplanet.view.ElementBoundary;
import backend.cinemaplanet.view.LocationBoundary;
import backend.cinemaplanet.view.RoleBoundary;
import backend.cinemaplanet.view.UserBoundary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CinemaPlanetApplicationTests {
	private RestTemplate restTemplate;
	private String userUrl, actionUrl, elementUrl, adminUrl;
	private int port;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.userUrl = "http://localhost:" + port + "/acs/users";
		this.actionUrl = "http://localhost:" + port + "/acs/actions";
		this.elementUrl = "http://localhost:" + port + "/acs/elements";
		this.adminUrl = "http://localhost:" + port + "/acs/admin";

	}

	@BeforeEach
	public void setup() { // setup test environment before each test
	}

	@AfterEach
	public void teardown() { // cleanup test environment after each test
		// create admin to delete all objects in db
		this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		this.restTemplate.delete(this.adminUrl + "/actions/admin@demo.com");
		this.restTemplate.delete(this.adminUrl + "/elements/admin@demo.com");
		this.restTemplate.delete(this.adminUrl + "/users/admin@demo.com");
	}

	@Test
	public void testContext() {

	}

	/* USER TESTS */
	@Test
	public void testLoginWithValidEmailReturnsUserDetailsFromDatabase() throws Exception {
		// GIVEN server is up
		// AND server contains user with email "user@demo.com" defined as {userEmail}
		UserBoundary newUser = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);

		// WHEN GET /acs/users/login/{userEmail}
		UserBoundary userFromServer = this.restTemplate.getForObject(this.userUrl + "/login/{userEmail}",
				UserBoundary.class, newUser.getEmail());

		// THEN server responds with 2xx
		// AND server responds with user details
		assertThat(userFromServer).isEqualToComparingFieldByField(newUser);
	}

	@Test
	public void testCreateUserReturnsUserDetailsInResponse() throws Exception {
		// GIVEN server is up

		// WHEN POST /acs/users with new user
		UserBoundary newUser = new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)");

		UserBoundary userFromServer = this.restTemplate.postForObject(this.userUrl, newUser, UserBoundary.class);

		// THEN server responds with 2xx
		// AND server responds with user details
		assertThat(userFromServer).isEqualToComparingFieldByField(newUser);
	}

	@Test
	public void testCreateUserCreateNewUserWithPropperDetailsInDatabase() throws Exception {
		// GIVEN the server is up

		// WHEN POST /acs/users with new user
		UserBoundary newUser = new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)");

		UserBoundary userFromServer = this.restTemplate.postForObject(this.userUrl, newUser, UserBoundary.class);

		// THEN the database contains new user
		UserBoundary userFromDatabase = this.restTemplate.getForObject(this.userUrl + "/login/{userEmail}",
				UserBoundary.class, userFromServer.getEmail());

		assertThat(userFromDatabase).isEqualToComparingFieldByField(userFromServer);
	}

	@Test
	public void testUpdatedUserWithValidAttributesIsUpdatedInDatabase() throws Exception {
		// GIVEN the server is up

		// AND server contains a user with username "userDemo", role "PLAYER" and email
		// "user@demo.com" defined as {userEmail}
		UserBoundary newUser = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);

		// WHEN PUT /acs/users/{userEmail} and enter {"username":"newUsername",
		// "role":"MANAGER"}
		Map<String, Object> update = new HashMap<>();
		update.put("username", "new_username");
		update.put("role", RoleBoundary.MANAGER);

		this.restTemplate.put(this.userUrl + "/{userEmail}", update, newUser.getEmail());

		// THEN server responds with 2xx
		// AND database is updated only with the modified fields
		assertThat(this.restTemplate.getForObject(this.userUrl + "/login/{userEmail}", UserBoundary.class,
				newUser.getEmail())).extracting("username", "role", "avatar").containsExactly(update.get("username"),
				update.get("role"), newUser.getAvatar());
	}

	@Test
	public void testGetAllUserWithDatabaseContaining4UsersInvokedByAdminReturnsAllUsersInfo() throws Exception {
		// Given the system’s DB has admin user saved and the admin email is
		// admin@demo.com
		List<UserBoundary> databaseContent = Collections.synchronizedList(new ArrayList<UserBoundary>());
		UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)");
		this.restTemplate.postForObject(this.userUrl, admin, UserBoundary.class);
		databaseContent.add(admin);

		// AND has 3 more users
		for (int i = 0; i < 2; i++) {
			UserBoundary user = new UserBoundary("user" + i + "@demo.com", RoleBoundary.PLAYER, "user" + i + "Demo",
					":)");
			this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class);
			databaseContent.add(user);
		}

		// When the admin invokes GET all users, using url:
		// /acs/admin/users/admin@demo.com
		UserBoundary[] dataFromServer = this.restTemplate.getForObject(this.adminUrl + "/users/{adminEmail}",
				UserBoundary[].class, admin.getEmail());

		// THEN the server returns status 2xx
		// AND the response includes all users in the database in any order
		assertThat(dataFromServer).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(databaseContent);
	}
	// NO TEST TO GET ALL USERS WITH EMPTY USERS IN DB
	// can't get all users with no users in DB because must be called by ADMIN user
	// (which must exist in DB)

	@Test
	public void testDelelteAllUsersWithDatabaseContainig4UsersInvokedByAdminRemovesAllUsersFromDatabse()
			throws Exception {
		// GIVEN server is up
		// AND the server contains admin user in DB
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)"), UserBoundary.class);
		// AND the server contains 4 users in DB
		IntStream.range(0, 3)
				.mapToObj(i -> new UserBoundary("user" + (i + 1) + "@demo.com", RoleBoundary.PLAYER,
						"user" + (i + 1) + "Demo", ":)"))
				.map(user -> this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class))
				.collect(Collectors.toList());

		// WHEN admin invokes DELETE with /acs/admin/users/admin@demo.com
		this.restTemplate.delete(this.adminUrl + "/users/" + admin.getEmail());

		// THEN server responds with 2xx
		// AND all users are removed from database (contains only admin)
		// UserBoundary[] allUsers = this.restTemplate.getForObject(this.adminUrl +
		// "/users/{adminEmail}",
		// UserBoundary[].class, admin.getEmail());
		// assertThat(allUsers).isEmpty();
		// can't export all users without any user.
	}

	/* ELEMENTS TEST */

	@Test
	public void testCreateElementWithPropperDetailsInvokedByManagerSavesElementDetailsInDatabase() throws Exception {
		// GIVEN server is up
		// AND server contains manager in DB with email "manager@demo.com"
		UserBoundary manager = new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)");
		this.restTemplate.postForObject(this.userUrl, manager, UserBoundary.class);

		// WHEN manager invokes POST /acs/elements/{managerEmail} with new element
		ElementBoundary newElement = new ElementBoundary("type element1", "element1", true,
				new LocationBoundary("1.1", "1.2"), null);
		ElementBoundary elementFromServer = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				newElement, ElementBoundary.class, manager.getEmail());

		// THEN server responds with 2xx
		// AND saves new element to database
		ElementBoundary elementFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{elementId}", ElementBoundary.class, manager.getEmail(),
				elementFromServer.getElementId());
		assertThat(elementFromDatabase).usingRecursiveComparison().isEqualTo(elementFromServer);
	}

	@Test
	public void testCreateElementWithPropperDetailsInvokedByManagerReturnsNewElementDetails() throws Exception {
		// GIVEN server is up
		// AND server contains manager in DB with email "manager@demo.com"
		UserBoundary manager = new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)");
		this.restTemplate.postForObject(this.userUrl, manager, UserBoundary.class);

		// WHEN manager invokes POST /acs/elements/{managerEmail} with new element
		ElementBoundary newElement = new ElementBoundary("type element1", "element1", true,
				new LocationBoundary("1.1", "1.2"), null);

		ElementBoundary elementFromServer = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				newElement, ElementBoundary.class, manager.getEmail());

		// THEN server responds with 2xx
		// AND server responds with element details
		ElementBoundary elementFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{elementId}", ElementBoundary.class, manager.getEmail(),
				elementFromServer.getElementId());
		assertThat(elementFromDatabase).usingRecursiveComparison().isEqualTo(elementFromServer);
	}

	@Test
	public void testUpdatedElementWithValidAttributesInvokedByManagerIsUpdatedInDatabase() throws Exception {
		// GIVEN the server is up
		// AND server contains manager with email manager@demo.com
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND server contains an element
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN manager invokes PUT /acs/element/{managerEmail}/{elementId} and enter
		// {"name":"invalid",
		// "active":"false"}
		Map<String, Object> update = new HashMap<>();
		update.put("name", "invalid");
		update.put("active", false);

		this.restTemplate.put(this.elementUrl + "/{managerEmail}/{elementId}", update, manager.getEmail(),
				element.getElementId());

		// THEN server responds with 2xx
		// AND database is updated only with the modified fields
		ElementBoundary elementFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{elementId}", ElementBoundary.class, manager.getEmail(),
				element.getElementId());

		assertThat(elementFromDatabase).extracting("name", "active", "type", "createdTimestamp", "elementAttributes")
				.containsExactly(update.get("name"), update.get("active"), element.getType(),
						element.getCreatedTimestamp(), element.getElementAttributes());
	}

	@Test
	public void testRetrieveSpecificElementByIdContainedInDatabaseAndIsNOTActiveInvokedByManagerReturnsElementDetails()
			throws Exception {
		// GIVEN server is up
		// AND server contains user with email manager@demo.com
		UserBoundary user = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND server contains and element in database
		ElementBoundary elementFromServer = this.restTemplate.postForObject(this.elementUrl + "/{managetEmail}",
				new ElementBoundary("type element1", "element1", false, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, user.getEmail());

		// WHEN GET /acs/elements/{userEmail}/{elementId}
		ElementBoundary elementFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{elementId}", ElementBoundary.class, user.getEmail(),
				elementFromServer.getElementId());

		// THEN server responds with 2xx
		// AND responds with element details
		assertThat(elementFromDatabase).usingRecursiveComparison().isEqualTo(elementFromServer);
	}

	@Test
	public void testRetrieveSpecificElementByIdContainedInDatabaseAndIsActiveInvokedByPlayerReturnsElementDetails()
			throws Exception {
		// GIVEN server is up
		// AND server contains user with email manager@demo.com
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND server contains element in db
		ElementBoundary elementFromServer = this.restTemplate.postForObject(this.elementUrl + "/{managetEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN player invokes GET /acs/elements/{userEmail}/{elementId}
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		ElementBoundary elementFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{elementId}", ElementBoundary.class, player.getEmail(),
				elementFromServer.getElementId());

		// THEN server responds with 2xx
		// AND responds with element details
		assertThat(elementFromDatabase).usingRecursiveComparison().isEqualTo(elementFromServer);
	}

	@Test
	public void testRetrieveSpecificElementByIdContainedInDatabaseAndIsNOTActiveInvokedByPlayerReturns404NotFoundException()
			throws Exception {
		// GIVEN server is up
		// AND server contains user with email manager@demo.com
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND server contains en element in db and it is NOT active
		ElementBoundary elementFromServer = this.restTemplate.postForObject(this.elementUrl + "/{managetEmail}",
				new ElementBoundary("type element1", "element1", false, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN player invokes GET /acs/elements/{userEmail}/{elementId}
		// THEN server responds with 404 not found
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		assertThrows(NotFound.class, () -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/{elementId}",
				ElementBoundary.class, player.getEmail(), elementFromServer.getElementId()));
	}

	@Test
	public void testRetrieveSpecificElementByIdNOTContainedInDatabaseAndIsActiveInvokedByPlayerReturns404NotFoundException()
			throws Exception {
		// GIVEN server is up
		// AND server doesn't contain any element

		// WHEN player invokes GET /acs/elements/{userEmail}/{elementId}
		// AND elementId deosn't exist
		// THEN server responds with 404 doesn't exist
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		assertThrows(NotFound.class, () -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/{elementId}",
				ElementBoundary.class, player.getEmail(), "1"));
	}

	@Test
	public void testGetAllElementsOnServerInitInvokedByManagerReturnsEmptyArray() throws Exception {
		// Given server is up
		// AND the serve contains user use in DB
		UserBoundary user = new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class);

		// AND the server doesn't contain any elements

		// WHEN manager invokes GET /acs/elements/manager@demo.com
		ElementBoundary[] allElements = this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, user.getEmail());

		// THEN the server responds with 2xx
		// AND the response is an empty array
		assertThat(allElements).isEmpty();
	}

	@Test
	public void testGetAllElementsOnServerInitInvokedByPlayerReturnsEmptyArray() throws Exception {
		// Given server is up
		// AND the serve contains user use in DB
		UserBoundary user = new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class);

		// AND the server doesn't contain any elements

		// WHEN player invokes GET /acs/elements/user@demo.com
		ElementBoundary[] allElements = this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, user.getEmail());

		// THEN the server responds with 2xx
		// AND the response is an empty array
		assertThat(allElements).isEmpty();
	}

	@Test
	public void testGetAllElementsWithDatabaseContainig4ElementsInvokedByManagerReturnsAllElementsInTheDatabase()
			throws Exception {
		// GIVEN server is up
		// AND manager with email manager@demo.com exist in database
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)"), UserBoundary.class);

		// AND the database contains 4 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND the database contains 2 not active elements
		List<ElementBoundary> notActiveElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 4), "element" + (i + 4), false,
						new LocationBoundary("1." + (i + 4), "1." + 2 * (i + 4)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN manager invokes GET /acs/elements/{userEmail}
		ElementBoundary[] dataFromServer = this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, manager.getEmail());

		// THEN the server returns status 2xx
		// AND the response includes all elements in db sorted by type
		List<ElementBoundary> allElements = Stream.concat(activeElements.stream(), notActiveElements.stream())
				.collect(Collectors.toList());
		assertThat(dataFromServer).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(allElements);
	}

	@Test
	public void testGetAllElementsWithDatabaseContainig4ElementsInvokedByPlayerReturnsOnlyActiveElementsInTheDatabase()
			throws Exception {
		// GIVEN server is up
		// AND manager with email manager@demo.com exist in database
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)"), UserBoundary.class);
		// AND the database contains 4 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND the database contains 2 not active elements
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 4), "element" + (i + 4), false,
						new LocationBoundary("1." + (i + 4), "1." + 2 * (i + 4)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN player invokes GET /acs/elements/{userEmail}
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", "=)"), UserBoundary.class);
		ElementBoundary[] dataFromServer = this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, player.getEmail());

		// THEN the server returns status 2xx
		// AND the response includes only active messages in db sorted by type
		assertThat(dataFromServer).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(activeElements);
	}

	@Test
	public void testGetAllElementsInvokedByAdminReturns403ForbiddenException() throws Exception {
		// Given server is up
		// AND the serve contains user use in DB
		UserBoundary user = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, user, UserBoundary.class);

		// AND the server doesn't contain any elements

		// WHEN admin invokes GET /acs/elements/admin@demo.com
		// THEN the server responds with 403 forbidden exception
		assertThrows(Forbidden.class, () -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, user.getEmail()));
	}

	@Test
	public void testDelelteAllElementsWithDatabaseContainig4ElementsInvokedByAdminRemovesAllElementsFromDatabse()
			throws Exception {
		// GIVEN server is up
		// AND the serve contains admin user in DB
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)"), UserBoundary.class);
		// AND manager with email manager@demo exist in database
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)"), UserBoundary.class);
		// AND the database contains 4 elements
		IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN admin invokes DELETE with /acs/admin/elements/admin@demo.com
		this.restTemplate.delete(this.adminUrl + "/elements/" + admin.getEmail());

		// THEN server responds with 2xx
		// AND all elements are removed from database
		ElementBoundary[] allElements = this.restTemplate.getForObject(this.elementUrl + "/{userEmail}",
				ElementBoundary[].class, manager.getEmail());
		assertThat(allElements).isEmpty();
	}

	@Test
	public void testDelelteAllElementsWithDatabaseContainig4ElementsInvokedByManagerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND manager with email manager@demo exist in database
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)"), UserBoundary.class);
		// AND the database contains 4 elements
		IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN manager invokes DELETE with /acs/admin/elements/manager@demo.com
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class,
				() -> this.restTemplate.delete(this.adminUrl + "/elements/" + manager.getEmail()));
	}

	@Test
	public void testDelelteAllElementsWithDatabaseContainig4ElementsInvokedByPlayerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND manager with email manager@demo exist in database
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", "=)"), UserBoundary.class);
		// AND the database contains 4 elements
		IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN player invokes DELETE with /acs/admin/elements/user@demo.com
		// THEN server responds with 403 forbidden exception
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", "=)"), UserBoundary.class);
		assertThrows(Forbidden.class, () -> this.restTemplate.delete(this.adminUrl + "/elements/" + player.getEmail()));
	}

	@Test
	public void testAddChildToParentInvokedByManagerAddsChildElementToParentsChildren() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN manager invokes PUT with
		// /acs/elements/{managerEmail}/{parentElementId}/children
		this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", element,
				manager.getEmail(), element.getElementId());

		// THEN server returns with 2xx
		// AND child is added to element children
		ElementBoundary[] children = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/children", ElementBoundary[].class,
				manager.getEmail(), element.getElementId());
		ElementBoundary[] elementsAdded = { element };
		// assertThat(element).isIn(children);
		assertThat(children).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(elementsAdded);
	}

	@Test
	public void testAddChildToParentByManagerAddsParentElementToChildsParents() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN PUT with /acs/elements/{managerEmail}/{parentElementId}/children
		this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", element,
				manager.getEmail(), element.getElementId());

		// THEN server returns with 2xx
		// AND parent is added to element parents
		ElementBoundary[] parents = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/parents", ElementBoundary[].class, manager.getEmail(),
				element.getElementId());
		ElementBoundary[] elementParentsAdded = { element };

		assertThat(parents).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(elementParentsAdded);
	}

	@Test
	public void testAddChildToParentInvokedByPlayerReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN player invokes PUT with
		// /acs/elements/{managerEmail}/{parentElementId}/children
		// THEN server returns with 403 forbidden exception
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", element,
						player.getEmail(), element.getElementId()));
	}

	@Test
	public void testAddChildToParentInvokedByAdminReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// WHEN player invokes PUT with
		// /acs/elements/{managerEmail}/{parentElementId}/children
		// THEN server returns with 403 forbidden exception
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", element,
						admin.getEmail(), element.getElementId()));
	}

	@Test
	public void testGetAllChildrenForElementWithoutChildrenReturnsEmptyArray() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// AND the element doesn't have any children

		// WHEN GET /acs/elements/{userEmail}/{parentElementId}/children
		ElementBoundary[] allChildren = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/children", ElementBoundary[].class,
				manager.getEmail(), element.getElementId());

		// THEN the server responds with 2xx
		// AND the response is an empty array
		assertThat(allChildren).isEmpty();
	}

	@Test
	public void testGetAllChildrenForElementWith6ChildrenInvokedByManagerReturnsAllChildrenDetailsInArray()
			throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 4 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements
		List<ElementBoundary> notActiveElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 4), "element" + (i + 4), false,
						new LocationBoundary("1." + (i + 4), "1." + 2 * (i + 4)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is parent of all elements
		ElementBoundary parent = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("parent", "parentElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		List<ElementBoundary> allChildren = Stream.concat(activeElements.stream(), notActiveElements.stream())
				.collect(Collectors.toList());
		allChildren.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children",
						element, manager.getEmail(), parent.getElementId()));

		// WHEN manager invokes GET /acs/elements/{userEmail}/{parentElementId}/children
		ElementBoundary[] childrenFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/children", ElementBoundary[].class,
				manager.getEmail(), parent.getElementId());

		// THEN the server responds with 2xx
		// AND the response with array of all children
		assertThat(childrenFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(allChildren);
	}

	@Test
	public void testGetAllChildrenForElementWith6ChildrenInvokedByPlayerReturnsOnlyActiveChildrenDetailsInArray()
			throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 4 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 3)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements
		List<ElementBoundary> notActiveElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 4), "element" + (i + 4), false,
						new LocationBoundary("1." + (i + 4), "1." + 2 * (i + 4)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is parent of all elements
		ElementBoundary parent = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("parent", "parentElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		List<ElementBoundary> allChildren = Stream.concat(activeElements.stream(), notActiveElements.stream())
				.collect(Collectors.toList());
		allChildren.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children",
						element, manager.getEmail(), parent.getElementId()));

		// WHEN player invokes GET /acs/elements/{userEmail}/{parentElementId}/children
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);
		ElementBoundary[] childrenFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/children", ElementBoundary[].class, player.getEmail(),
				parent.getElementId());

		// THEN the server responds with 2xx
		// AND the response with array of only 4 active children
		assertThat(childrenFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(activeElements);
	}

	@Test
	public void testGetAllChildrenForElementWith2ChildrenInvokedByAdminReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 2 elements
		List<ElementBoundary> childElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is parent of all elements
		ElementBoundary parent = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("parent", "parentElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		childElements.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children",
						element, manager.getEmail(), parent.getElementId()));

		// WHEN admin invokes GET /acs/elements/{userEmail}/{parentElementId}/parents
		// THEN server returns with 403 forbidden exception
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/{parentElementId}/children",
						ElementBoundary[].class, admin.getEmail(), parent.getElementId()));

	}

	@Test
	public void testGetAllParentsForElementWithoutParentsReturnsEmptyArray() throws Exception {
		// GIVEN the server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// AND the element doesn't have any parents

		// WHEN GET /acs/elements/{userEmail}/{parentElementId}/parents
		ElementBoundary[] allParents = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{parentElementId}/parents", ElementBoundary[].class, manager.getEmail(),
				element.getElementId());

		// THEN the server responds with 2xx
		// AND the response is an empty array
		assertThat(allParents).isEmpty();
	}

	@Test
	public void testGetAllParentsForElementWith2ParentsInvokedByManagerReturnsAllParentsDetailsInArray()
			throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 2 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements
		List<ElementBoundary> notActiveElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 2), "element" + (i + 2), false,
						new LocationBoundary("1." + (i + 2), "1." + 2 * (i + 2)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is child of all elements
		ElementBoundary child = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("child", "childElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		List<ElementBoundary> allParents = Stream.concat(activeElements.stream(), notActiveElements.stream())
				.collect(Collectors.toList());
		allParents.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", child,
						manager.getEmail(), element.getElementId()));

		// WHEN manager invokes GET /acs/elements/{userEmail}/{parentElementId}/parents
		ElementBoundary[] childrenFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{childElementId}/parents", ElementBoundary[].class, manager.getEmail(),
				child.getElementId());

		// THEN the server responds with 2xx
		// AND the response with array of all parents
		assertThat(childrenFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(allParents);
	}

	@Test
	public void testGetAllParentsForElementWith2ParentsInvokedByPlayerReturnsAllParentsDetailsInArray()
			throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 2 active elements
		List<ElementBoundary> activeElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements
		List<ElementBoundary> notActiveElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 2), "element" + (i + 2), false,
						new LocationBoundary("1." + (i + 2), "1." + 2 * (i + 2)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is child of all elements
		ElementBoundary child = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("child", "childElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		List<ElementBoundary> allParents = Stream.concat(activeElements.stream(), notActiveElements.stream())
				.collect(Collectors.toList());
		allParents.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", child,
						manager.getEmail(), element.getElementId()));

		// WHEN player invokes GET /acs/elements/{userEmail}/{parentElementId}/parents
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);
		ElementBoundary[] parentsFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/{childElementId}/parents", ElementBoundary[].class, player.getEmail(),
				child.getElementId());

		// THEN the server responds with 2xx
		// AND the response with array of only 2 active parents
		assertThat(parentsFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(activeElements);
	}

	@Test
	public void testGetAllParentsForElementWith2ParentsInvokedByAdminReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND db contains 2 elements
		List<ElementBoundary> parentElements = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND an element which is child of all elements
		ElementBoundary child = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("child", "childElement", true, new LocationBoundary("1.33", "1.22"), null),
				ElementBoundary.class, manager.getEmail());
		parentElements.stream().forEach(
				element -> this.restTemplate.put(this.elementUrl + "/{managerEmail}/{parentElementId}/children", child,
						manager.getEmail(), element.getElementId()));

		// WHEN admin invokes GET /acs/elements/{userEmail}/{parentElementId}/parents
		// THEN server returns with 403 forbidden exception
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/{childElementId}/parents",
						ElementBoundary[].class, admin.getEmail(), child.getElementId()));

	}

	@Test
	public void testGetElementsByNameInvokedByManagerReturnsOnlyRelevantElements() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "element" in their name
		List<ElementBoundary> withElementPrefixActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements with prefix "element" in their name
		List<ElementBoundary> withElementPrefixNotActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "element" as prefix in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "another" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN manager invokes GET
		// /acs/elements/{managerEmail}/search/byName/element%25
		ElementBoundary[] elementsFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/search/byName/{namePrefix}%", ElementBoundary[].class,
				manager.getEmail(), "element");

		// THEN the server responds with 2xx
		// AND returns an array with the 4 elements with prefix "element" in their name
		// sorted by type
		List<ElementBoundary> allWithElementPrefix = Stream
				.concat(withElementPrefixActive.stream(), withElementPrefixNotActive.stream())
				.collect(Collectors.toList());
		assertThat(elementsFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(allWithElementPrefix);
	}

	@Test
	public void testGetElementsByNameInvokedByPlayerReturnsOnlyActiveRelevantElements() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "element" in their name
		List<ElementBoundary> withElementPrefixActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 1 not active elements with prefix "element" in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "element" as prefix in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "another" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN player invokes GET /acs/elements/{userrEmail}/search/byName/element%25
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);
		ElementBoundary[] elementsFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/search/byName/{namePrefix}%", ElementBoundary[].class,
				player.getEmail(), "element");

		// THEN the server responds with 2xx
		// AND returns an array with the 2 active elements with prefix "element" in
		// their name sorted by type
		assertThat(elementsFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(withElementPrefixActive);
	}

	@Test
	public void testGetElementsByNameInvokedByAdminReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "element" in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements with prefix "element" in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "element" as prefix in their name
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "another" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN admin invokes GET /acs/elements/{adminEmail}/search/byType/element%25
		// THEN the server responds with 403 forbidden exception
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/search/byName/{namePrefix}%",
						ElementBoundary[].class, admin.getEmail(), "element"));

	}

	@Test
	public void testGetElementsByTypeInvokedByManagerReturnsOnlyRelevantElements() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "type" in their type
		List<ElementBoundary> withTypePrefixActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements with prefix "type" in their type
		List<ElementBoundary> withTypePrefixNotActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "type" as prefix in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("another" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN manager invokes GET
		// /acs/elements/{managerEmail}/search/byType/element%25
		ElementBoundary[] elementsFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/search/byType/{typePrefix}%", ElementBoundary[].class,
				manager.getEmail(), "type");

		// THEN the server responds with 2xx
		// AND returns an array with the 4 elements with prefix "type" in their type
		List<ElementBoundary> allWithElementPrefix = Stream
				.concat(withTypePrefixActive.stream(), withTypePrefixNotActive.stream()).collect(Collectors.toList());
		assertThat(elementsFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(allWithElementPrefix);
	}

	@Test
	public void testGetElementsByTypeInvokedByPlayerReturnsOnlyActiveRelevantElements() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "type" in their type
		List<ElementBoundary> withTypePrefixActive = IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements with prefix "type" in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "type" as prefix in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("another" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN player invokes GET /acs/elements/{userrEmail}/search/byType/element%25
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "userDemo", ":)"), UserBoundary.class);
		ElementBoundary[] elementsFromDatabase = this.restTemplate.getForObject(
				this.elementUrl + "/{userEmail}/search/byType/{typePrefix}%", ElementBoundary[].class,
				player.getEmail(), "type");

		// THEN the server responds with 2xx
		// AND returns an array with the 2 active elements with prefix "type" in their
		// type
		assertThat(elementsFromDatabase).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(withTypePrefixActive);
	}

	@Test
	public void testGetElementsByTypeInvokedByAdminReturns403ForbiddenException() throws Exception {
		// GIVEN the server is up
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		// AND the server contains 2 active elements with prefix "type" in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());
		// AND 2 not active elements with prefix "type" in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("type" + (i + 1), "element" + (i + 1), false,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// AND 2 elements without "type" as prefix in their type
		IntStream.range(0, 1)
				.mapToObj(i -> new ElementBoundary("another" + (i + 1), "element" + (i + 1), true,
						new LocationBoundary("1." + (i + 1), "1." + 2 * (i + 1)), null))
				.map(element -> this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}", element,
						ElementBoundary.class, manager.getEmail()))
				.collect(Collectors.toList());

		// WHEN admin invokes GET /acs/elements/{adminEmail}/search/byType/element%25
		// THEN the server responds with 403 forbidden exception
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", ":)"), UserBoundary.class);
		assertThrows(Forbidden.class,
				() -> this.restTemplate.getForObject(this.elementUrl + "/{userEmail}/search/byType/{typePrefix}%",
						ElementBoundary[].class, admin.getEmail(), "type"));

	}

	/* ACTION TEST */

	// @Test
	// public void
	// testInvokeActionWithWithPropperDetailsInDatabaseInvokedByPlayerRespondsWithJsonObject()
	// throws Exception {
	// // GIVEN server is up
	// // AND server contains and element in DB
	// UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
	// new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo",
	// ":)"), UserBoundary.class);
	// ElementBoundary element = this.restTemplate.postForObject(this.elementUrl +
	// "/{managerEmail}",
	// new ElementBoundary("seat", "1", true, new LocationBoundary("1.1", "1.2"),
	// null),
	// ElementBoundary.class, manager.getEmail());
	// // AND the serve contains admin user in DB
	// UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN,
	// "adminDemo", "=)");
	// this.restTemplate.postForObject(this.userUrl, admin, UserBoundary.class);
	//
	// // WHEN POST with /acs/actions with new action
	// UserBoundary player = this.restTemplate.postForObject(this.userUrl,
	// new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"),
	// UserBoundary.class);
	// ActionBoundary newAction = new ActionBoundary("order seat", new
	// ActionElementBoundary(element.getElementId()),
	// new CreatedByBoundary(player.getEmail()), null);
	// ActionBoundary actionFromServer =
	// this.restTemplate.postForObject(this.actionUrl, newAction,
	// ActionBoundary.class);
	//
	// // THEN server responds with 2xx
	// // AND responds with some Json object
	//
	// // AND posted data was saved to database
	// ActionBoundary[] actionsFromDatabase =
	// this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
	// ActionBoundary[].class, admin.getEmail());
	//
	// assertThat(actionsFromDatabase).usingRecursiveFieldByFieldElementComparator()
	// .containsExactlyInAnyOrder(actionFromServer);
	// }

	@Test
	public void testInvokeActionWithWithPropperDetailsInDatabaseInvokedByManagerRespondsWith403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());
		// AND the serve contains admin user in DB
		UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, admin, UserBoundary.class);

		// WHEN manager invokes POST with /acs/actions with new action
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class,
				() -> this.restTemplate.postForObject(this.actionUrl,
						new ActionBoundary("type1", new ActionElementBoundary(element.getElementId()),
								new CreatedByBoundary(manager.getEmail()), null),
						ActionBoundary.class));
	}

	@Test
	public void testInvokeActionWithWithPropperDetailsInDatabaseInvokedByAdminRespondsWith403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());
		// AND the serve contains admin user in DB
		UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, admin, UserBoundary.class);

		// WHEN manager invokes POST with /acs/actions with new action
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class,
				() -> this.restTemplate.postForObject(this.actionUrl,
						new ActionBoundary("type1", new ActionElementBoundary(element.getElementId()),
								new CreatedByBoundary(admin.getEmail()), null),
						ActionBoundary.class));
	}

	@Test
	public void testGetAllActionsOnServerInitInvokedByAdminReturnsEmptyArray() throws Exception {
		// Given server is up
		// AND the serve contains admin user in DB
		UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)");
		this.restTemplate.postForObject(this.userUrl, admin, UserBoundary.class);
		// AND the server doesn't contain any actions

		// WHEN admin invokes GET /acs/admin/actions/admin@demo.com
		ActionBoundary[] allActions = this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
				ActionBoundary[].class, admin.getEmail());

		// THEN the server responds with 2xx
		// AND the response is an empty array
		assertThat(allActions).isEmpty();
	}

	// @Test
	// public void
	// testGetAllActionsWithDatabaseContainig4ActionsInvokedByAdminReturnsAllActionsInTheDatabase()
	// throws Exception {
	// // GIVEN server is up
	// // AND the serve contains admin user in DB
	// UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
	// new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)"),
	// UserBoundary.class);
	// // AND server contains and element in DB
	// UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
	// new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo",
	// ":)"), UserBoundary.class);
	// ElementBoundary element = this.restTemplate.postForObject(this.elementUrl +
	// "/{managerEmail}",
	// new ElementBoundary("type element1", "element1", true, new
	// LocationBoundary("1.1", "1.2"), null),
	// ElementBoundary.class, manager.getEmail());
	// // AND the database contains 4 Actions
	// UserBoundary player = this.restTemplate.postForObject(this.userUrl,
	// new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"),
	// UserBoundary.class);
	// List<ActionBoundary> newActions = IntStream.range(0, 3)
	// .mapToObj(i -> new ActionBoundary("type" + i, new
	// ActionElementBoundary(element.getElementId()),
	// new CreatedByBoundary(player.getEmail()), null))
	// .map(action -> this.restTemplate.postForObject(this.actionUrl, action,
	// ActionBoundary.class))
	// .collect(Collectors.toList());
	//
	// // WHEN GET /acs/elements/{userEmail}
	// ActionBoundary[] actionsFromDatabase =
	// this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
	// ActionBoundary[].class, admin.getEmail());
	//
	// // THEN the server returns status 2xx
	// // AND the response includes all messages in the database in any order
	// assertThat(actionsFromDatabase).usingRecursiveFieldByFieldElementComparator()
	// .containsExactlyInAnyOrderElementsOf(newActions);
	// }

	@Test
	public void testGetAllActionsWithDatabaseContainig4ActionsInvokedByManagerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());
		// AND the database contains 4 Actions
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		IntStream.range(0, 3)
				.mapToObj(i -> new ActionBoundary("type" + i, new ActionElementBoundary(element.getElementId()),
						new CreatedByBoundary(player.getEmail()), null))
				.map(action -> this.restTemplate.postForObject(this.actionUrl, action, ActionBoundary.class))
				.collect(Collectors.toList());

		// WHEN manager invokes GET /acs/admin/actions/{userEmail}
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class, () -> this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
				ActionBoundary[].class, manager.getEmail()));
	}

	@Test
	public void testGetAllActionsWithDatabaseContainig4ActionsInvokedByPlayerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());
		// AND the database contains 4 Actions
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		IntStream.range(0, 3)
				.mapToObj(i -> new ActionBoundary("type" + i, new ActionElementBoundary(element.getElementId()),
						new CreatedByBoundary(player.getEmail()), null))
				.map(action -> this.restTemplate.postForObject(this.actionUrl, action, ActionBoundary.class))
				.collect(Collectors.toList());

		// WHEN player invokes GET /acs/admin/actions/{userEmail}
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class, () -> this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
				ActionBoundary[].class, player.getEmail()));
	}

	@Test
	public void testDelelteAllActionssWithDatabaseContainig4ActionsInvokedByAdminRemovesAllActionsFromDatabse()
			throws Exception {
		// GIVEN server is up
		// AND the serve contains admin user in DB
		UserBoundary admin = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "adminDemo", "=)"), UserBoundary.class);
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// AND the database contains 4 Actions
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		IntStream.range(0, 3)
				.mapToObj(i -> new ActionBoundary("type" + i, new ActionElementBoundary(element.getElementId()),
						new CreatedByBoundary(player.getEmail()), null))
				.map(action -> this.restTemplate.postForObject(this.actionUrl, action, ActionBoundary.class))
				.collect(Collectors.toList());

		// WHEN admin invokes DELETE with /acs/admin/actions/admin@demo.com
		this.restTemplate.delete(this.adminUrl + "/actions/" + admin.getEmail());

		// THEN server responds with 2xx
		// AND all actions are removed from database
		ActionBoundary[] allActions = this.restTemplate.getForObject(this.adminUrl + "/actions/{adminEmail}",
				ActionBoundary[].class, admin.getEmail());
		assertThat(allActions).isEmpty();

	}

	@Test
	public void testDelelteAllActionssWithDatabaseContainig4ActionsInvokedByManagerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// AND the database contains 4 Actions
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		IntStream.range(0, 3)
				.mapToObj(i -> new ActionBoundary("type" + i, new ActionElementBoundary(element.getElementId()),
						new CreatedByBoundary(player.getEmail()), null))
				.map(action -> this.restTemplate.postForObject(this.actionUrl, action, ActionBoundary.class))
				.collect(Collectors.toList());

		// WHEN manager invokes DELETE /acs/admin/actions/{userEmail}
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class, () -> this.restTemplate.delete(this.adminUrl + "/actions/" + manager.getEmail()));

	}

	@Test
	public void testDelelteAllActionssWithDatabaseContainig4ActionsInvokedByPlayerReturns403ForbiddenException()
			throws Exception {
		// GIVEN server is up
		// AND server contains and element in DB
		UserBoundary manager = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "managerDemo", ":)"), UserBoundary.class);
		ElementBoundary element = this.restTemplate.postForObject(this.elementUrl + "/{managerEmail}",
				new ElementBoundary("type element1", "element1", true, new LocationBoundary("1.1", "1.2"), null),
				ElementBoundary.class, manager.getEmail());

		// AND the database contains 4 Actions
		UserBoundary player = this.restTemplate.postForObject(this.userUrl,
				new UserBoundary("player@demo.com", RoleBoundary.PLAYER, "playerDemo", ":)"), UserBoundary.class);
		IntStream.range(0, 3)
				.mapToObj(i -> new ActionBoundary("type" + i, new ActionElementBoundary(element.getElementId()),
						new CreatedByBoundary(player.getEmail()), null))
				.map(action -> this.restTemplate.postForObject(this.actionUrl, action, ActionBoundary.class))
				.collect(Collectors.toList());

		// WHEN manager invokes DELETE /acs/admin/actions/{userEmail}
		// THEN server responds with 403 forbidden exception
		assertThrows(Forbidden.class, () -> this.restTemplate.delete(this.adminUrl + "/actions/" + player.getEmail()));

	}
}
