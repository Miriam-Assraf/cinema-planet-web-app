package backend.cinemaplanet.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.miriam.assraf.backend.logic.validators.EmailValidator;

//Table name: ACTIONS
@Entity
@Table(name = "ACTIONS")
public class ActionEntity {
    private Long actionId;
    private String type;
    private String element;
    private Date createdTimestamp;
    private String invokedBy;
    private String actionAttributes;

    public ActionEntity() {
    }

    @Id
    @GeneratedValue
    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    @NotNull(message = "type can't be null")
    @NotEmpty(message = "type can't be empty")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NotNull(message = "element can't be null")
    @NotEmpty(message = "element can't be empty")
    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @NotNull(message = "must include email of user invoking action")
    @NotEmpty(message = "must include email of user invoking action")
    @EmailValidator
    public String getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(String invokedBy) {
        this.invokedBy = invokedBy;
    }

    @Lob
    public String getActionAttributes() {
        return actionAttributes;
    }

    public void setActionAttributes(String actionAttributes) {
        this.actionAttributes = actionAttributes;
    }
}
