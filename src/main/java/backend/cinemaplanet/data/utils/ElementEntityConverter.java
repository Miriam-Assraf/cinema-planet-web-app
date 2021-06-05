package backend.cinemaplanet.data.utils;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miriam.assraf.backend.data.ElementEntity;
import com.miriam.assraf.backend.view.CreatedByBoundary;
import com.miriam.assraf.backend.view.ElementBoundary;
import com.miriam.assraf.backend.view.LocationBoundary;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class ElementEntityConverter {
    private ObjectMapper jackson;

    @PostConstruct
    public void setup() {
        this.jackson = new ObjectMapper();
    }

    public ElementBoundary convertFromEntity(ElementEntity elementEntity) {
        ElementBoundary elementBoundary = new ElementBoundary();
        LocationBoundary location = new LocationBoundary(
                DoubleAndStringConverter.convertToString(elementEntity.getLat()),
                DoubleAndStringConverter.convertToString(elementEntity.getLng()));

        elementBoundary.setElementId(LongAndStringConverter.convertToString(elementEntity.getElementId()));
        elementBoundary.setName(elementEntity.getName());
        elementBoundary.setActive(elementEntity.getActive());
        elementBoundary.setCreatedTimestamp(elementEntity.getCreatedTimestamp());
        elementBoundary.setType(elementEntity.getType());
        elementBoundary.setCreatedBy(new CreatedByBoundary(elementEntity.getCreatedBy()));
        // elementBoundary.setLocation(new LocationBoundary(elementEntity.getLat(),
        // elementEntity.getLng()));
        elementBoundary.setLocation(location);

        // unmarshalling
        try {
            if (elementEntity.getElementAttributes() != null) { // element attributes can be null
                elementBoundary.setElementAttributes(this.jackson.readValue(elementEntity.getElementAttributes(),
                        new TypeReference<Map<String, Object>>() {
                        }));
            } else {
                elementBoundary.setElementAttributes(null);
            }

        } catch (Exception exeption) {
            throw new RuntimeException(exeption);
        }

        return elementBoundary;
    }

    public ElementEntity convertToEntity(ElementBoundary elementBoundary) {
        ElementEntity elementEntity = new ElementEntity();

        if (elementBoundary.getElementId() != null) { // initalized as null until service update it with new id
            Long elementId = LongAndStringConverter.convertToLong(elementBoundary.getElementId());
            elementEntity.setElementId(elementId);
        } else {
            elementEntity.setElementId(null);
        }
        if (elementBoundary.getCreatedTimestamp() != null) { // initialized as null until service update it with date
                                                             // created
            elementEntity.setCreatedTimestamp(elementBoundary.getCreatedTimestamp());
        } else {
            elementEntity.setCreatedTimestamp(null);
        }
        if (elementBoundary.getCreatedBy() != null) { // initialized null until service update it with manager email
            elementEntity.setCreatedBy(elementBoundary.getCreatedBy().getEmail());
        } else {
            elementEntity.setCreatedBy(null);
        }

        elementEntity.setActive(elementBoundary.getActive());
        elementEntity.setName(elementBoundary.getName());
        elementEntity.setType(elementBoundary.getType());
        elementEntity.setLat(DoubleAndStringConverter.convertToDouble(elementBoundary.getLocation().getLat()));
        elementEntity.setLng(DoubleAndStringConverter.convertToDouble(elementBoundary.getLocation().getLng()));

        // marshalling
        try {
            if (elementBoundary.getElementAttributes() != null) { // element attributes can be null
                elementEntity
                        .setElementAttributes(this.jackson.writeValueAsString(elementBoundary.getElementAttributes()));
            } else {
                elementEntity.setElementAttributes(null);
            }

            return elementEntity;
        } catch (Exception exeption) {
            throw new RuntimeException(exeption);
        }
    }

    public String convertAttributesToEntity(Map<String, Object> attributes) {
        if (attributes != null) {
            try {
                return this.jackson.writeValueAsString(attributes);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        } else
            return null;
    }

}