package backend.cinemaplanet.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// check if need to be moved to acs.data
@Entity
public class LastIdValue {
    private Long lastId;

    public LastIdValue() {
    }

    @Id
    @GeneratedValue
    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }
}
