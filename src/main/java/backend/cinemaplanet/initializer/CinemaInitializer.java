package backend.cinemaplanet.initializer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.logic.userService.UserService;
import com.miriam.assraf.backend.view.ElementBoundary;
import com.miriam.assraf.backend.view.LocationBoundary;
import com.miriam.assraf.backend.view.RoleBoundary;
import com.miriam.assraf.backend.view.UserBoundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CinemaInitializer implements CommandLineRunner {
    private EnhancedElementService elementService;
    private UserService userService;
    private int numMovies = 5;
    private int numTheaters = 5;
    private int numScreeningsPerDay = 3;
    private int numDays = 2;

    public CinemaInitializer() {
    }

    @Autowired
    public void setElementService(EnhancedElementService elementService, UserService userService) {
        this.elementService = elementService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        UserBoundary manager = new UserBoundary("manager@demo.com", RoleBoundary.MANAGER, "manager",
                "https://img.webmd.com/dtmcms/live/webmd/consumer_assets/site_images/article_thumbnails/other/cat_relaxing_on_patio_other/1800x1200_cat_relaxing_on_patio_other.jpg");
        this.userService.createUser(manager);
        UserBoundary user = new UserBoundary("user@demo.com", RoleBoundary.PLAYER, "user",
                "https://img.webmd.com/dtmcms/live/webmd/consumer_assets/site_images/article_thumbnails/other/cat_relaxing_on_patio_other/1800x1200_cat_relaxing_on_patio_other.jpg");
        this.userService.createUser(user);
        UserBoundary admin = new UserBoundary("admin@demo.com", RoleBoundary.ADMIN, "admin",
                "https://img.webmd.com/dtmcms/live/webmd/consumer_assets/site_images/article_thumbnails/other/cat_relaxing_on_patio_other/1800x1200_cat_relaxing_on_patio_other.jpg");
        this.userService.createUser(admin);

        // cinema
        ElementBoundary cinema = createCinema("Tel-Aviv", manager.getEmail());

        // movies
        List<ElementBoundary> movies = new LinkedList<>();
        movies.add(createMovie("Tenet", true, "sci-fi/action", "180 minutes", manager.getEmail()));
        movies.add(createMovie("Wonder Woman 1984", true, "fantasy/action", "120 minutes", manager.getEmail()));
        movies.add(createMovie("Quiet Place II", true, "horror", "120 minutes", manager.getEmail()));
        movies.add(createMovie("Black Widow", true, "action", "120 minutes", manager.getEmail()));
        movies.add(createMovie("Candyman", true, "horror", "120 minutes", manager.getEmail()));

        // theaters
        List<ElementBoundary> theaters = new LinkedList<>();
        for (int i = 0; i < numTheaters; i++) {
            theaters.add(createTheater("theater " + (i + 1), true, manager.getEmail()));
        }

        // screenings
        List<ElementBoundary> screenings = new LinkedList<>();
        // can be in parallel times for each theater
        for (int i = 0; i < numMovies; i++) {
            for (int j = 0; j < numScreeningsPerDay; j++) {
                for (int k = 0; k < numDays; k++) {
                    // for each movie create 3 different screenings for 2 different days
                    Calendar cal = Calendar.getInstance();
                    cal.set(2020, 7, 22 + k, 14 + 2 * j, 30);
                    ElementBoundary screening = (createScreening(cal.getTime().toString(), true, cal.getTime(),
                            manager.getEmail()));
                    screenings.add(screening);
                    this.elementService.addChildToParent(manager.getEmail(), screening.getElementId(),
                            theaters.get(i).getElementId());
                }
            }
        }

        for (int i = 0; i < numMovies; i++) {
            for (int j = 0; j < numScreeningsPerDay * numDays; j++) {
                // for each movie add all different screenings
                this.elementService.addChildToParent(manager.getEmail(), movies.get(i).getElementId(),
                        screenings.get(j).getElementId());
            }
        }

        // rows
        for (int i = 0; i < 5; i++) {
            ElementBoundary row = createRow("" + (i + 1), -7 + i, 5 + i * 1.5, manager.getEmail()); // distance 1.5m per
                                                                                                    // row
            for (int j = 0; j < numTheaters; j++) {
                this.elementService.addChildToParent(manager.getEmail(), theaters.get(j).getElementId(),
                        row.getElementId());
            }
            // seat
            List<ElementBoundary> seats = createSeats(10, (i + 1), manager.getEmail());
            for (int k = 0; k < 10; k++) {
                this.elementService.addChildToParent(manager.getEmail(), row.getElementId(),
                        seats.get(k).getElementId());
            }
        }
    }

    private ElementBoundary createCinema(String branch, String managerEmail) {
        ElementBoundary cinema = new ElementBoundary();
        cinema.setType("cinema");
        cinema.setName("Cinema Planet " + branch);
        cinema.setActive(true);
        cinema.setLocation(new LocationBoundary("0.0", "0.0"));
        return this.elementService.create(managerEmail, cinema);
    }

    private ElementBoundary createTheater(String name, boolean active, String managerEmail) {
        ElementBoundary theater = new ElementBoundary();
        theater.setType("theater");
        theater.setName(name);
        theater.setActive(active);
        theater.setLocation(new LocationBoundary("0.0", "0.0"));
        return this.elementService.create(managerEmail, theater);
    }

    private List<ElementBoundary> createSeats(int numSeats, int numRow, String managerEmail) {
        Map<String, Object> moreAttributes = new HashMap<>();
        moreAttributes.put("available", true); // initialize all seats available

        return IntStream.range(0, numSeats)
                .mapToObj(i -> new ElementBoundary("seat", "" + (i + 1), true,
                        new LocationBoundary("0.0", String.valueOf(-numSeats / 2 + i)), moreAttributes)) // lng=x
                                                                                                         // position in
                                                                                                         // row
                .map(seat -> this.elementService.create(managerEmail, seat)).collect(Collectors.toList());
    }

    private ElementBoundary createRow(String numRow, int height, double distance, String managerEmail) {
        ElementBoundary row = new ElementBoundary();
        row.setType("row");
        row.setName(numRow);
        row.setActive(true);
        row.setLocation(new LocationBoundary("" + height, "" + distance)); // y, z
        return this.elementService.create(managerEmail, row);
    }

    private ElementBoundary createScreening(String name, boolean active, Date date, String managerEmail) {
        ElementBoundary screening = new ElementBoundary();
        screening.setType("screening");
        screening.setName(name);
        screening.setActive(active);
        screening.setLocation(new LocationBoundary("0.0", "0.0")); // y, z
        Map<String, Object> moreAttributes = new HashMap<>();
        moreAttributes.put("date", date.getDate() + "/" + date.getMonth()); // initialize all seats available
        moreAttributes.put("time", date.getHours() + ":" + date.getMinutes());
        screening.setElementAttributes(moreAttributes);
        return this.elementService.create(managerEmail, screening);
    }

    private ElementBoundary createMovie(String name, boolean active, String genre, String runtime,
            String managerEmail) {
        ElementBoundary movie = new ElementBoundary();
        movie.setType("movie");
        movie.setName(name);
        movie.setActive(active);
        movie.setLocation(new LocationBoundary("0.0", "0.0")); // y, z
        Map<String, Object> moreAttributes = new HashMap<>();
        moreAttributes.put("genre", genre);
        moreAttributes.put("runtime", runtime);
        movie.setElementAttributes(moreAttributes);
        return this.elementService.create(managerEmail, movie);
    }

}