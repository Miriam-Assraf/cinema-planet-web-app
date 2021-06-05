package backend.cinemaplanet.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {
    private Long elementId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private String createdBy;
    private double lat;
    private double lng;
    private String elementAttributes;
    private Set<ElementEntity> parents; // set doens't have a specific order
    private Set<ElementEntity> children;

    public ElementEntity(Long elementId, String type, String name, Boolean active, String createdBy, double lat,
            double lng, String elementAttributes) {
        this.elementId = elementId;
        this.type = type;
        this.name = name;
        this.active = active;
        this.createdBy = createdBy;
        this.lat = lat;
        this.lng = lng;
        this.elementAttributes = elementAttributes;

    }

    public ElementEntity() {
        this.parents = new HashSet<>();
        this.children = new HashSet<>();
    }

    @Id
    @GeneratedValue
    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    @NotNull(message = "type can't be null")
    @NotEmpty(message = "type can't be empty")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NotNull(message = "name can't be null")
    @NotEmpty(message = "name can't be empty")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Lob
    // can be empty
    public String getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(String elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Elements_Association", joinColumns = @JoinColumn(name = "ChildID"), inverseJoinColumns = @JoinColumn(name = "ParentID"))
    public Set<ElementEntity> getParents() {
        return parents;
    }

    public void setParents(Set<ElementEntity> parents) {
        this.parents = parents;
    }

    @ManyToMany(mappedBy = "parents")
    public Set<ElementEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<ElementEntity> children) {
        this.children = children;
    }

    public void addChild(ElementEntity element) {
        this.children.add(element); // add child to parent
        element.parents.add(this); // add parent to child
    }

    @Override
    public int hashCode() {
        return elementId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ElementEntity other = (ElementEntity) obj;
        if (elementId == null) {
            if (other.elementId != null)
                return false;
        } else if (!elementId.equals(other.elementId))
            return false;
        return true;
    }

}
