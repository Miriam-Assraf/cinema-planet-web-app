package com.miriam.assraf.backend.rest;

import com.miriam.assraf.backend.logic.elementService.EnhancedElementService;
import com.miriam.assraf.backend.view.ElementBoundary;
import com.miriam.assraf.backend.view.ElementIdBoundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com")
public class ElementController {
    private EnhancedElementService elementService;

    // injection
    @Autowired
    public ElementController(EnhancedElementService elementService) {
        super();
        this.elementService = elementService;
    }

    // POST --> instance in system (SQL: INSERT)
    @RequestMapping(path = "/acs/elements/{managerEmail}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary createElement(@PathVariable("managerEmail") String managerEmail,
            @RequestBody ElementBoundary elementBoundary) {
        return this.elementService.create(managerEmail, elementBoundary);
    }

    // PUT --> update an element
    @RequestMapping(path = "/acs/elements/{managerEmail}/{elementId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateElement(@PathVariable("managerEmail") String managerEmail,
            @PathVariable("elementId") String elementId, @RequestBody ElementBoundary update) {
        this.elementService.update(managerEmail, elementId, update);
    }

    // GET --> get an element
    @RequestMapping(path = "/acs/elements/{elementId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary getElement(@PathVariable("elementId") String elementId) {
        return this.elementService.getSpecificElement(elementId);
    }

    // GET --> get all elements
    @RequestMapping(path = "/acs/elements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllElements(@RequestParam(name = "size", required = false, defaultValue = "10") int size, // not required, will return
                                            @RequestParam(name = "page", required = false, defaultValue = "0") int page) { // not required, will return
        return this.elementService.getAll(size, page).toArray(new ElementBoundary[0]);
    }

    // PUT --> add child element to element
    @RequestMapping(path = "/acs/elements/{managerEmail}/{parentElementId}/children", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addChildToParent(@PathVariable("managerEmail") String managerEmail, @PathVariable("parentElementId") String parentId, @RequestBody ElementIdBoundary child) {
        this.elementService.addChildToParent(managerEmail, parentId, child.getElementId());
    }

    // GET --> get array of children of element
    @RequestMapping(path = "/acs/elements/{parentElementId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllChildren(@PathVariable("parentElementId") String parentId,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService.getChildren(parentId, size, page).toArray(new ElementBoundary[0]);
    }

    // GET --> get array of parents of element
    @RequestMapping(path = "/acs/elements/{childElementId}/parents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllParents(@PathVariable("childElementId") String childId,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService.getParents(childId, size, page).toArray(new ElementBoundary[0]);
    }

    // GET --> get array of elements matching specific name
    @RequestMapping(path = "/acs/elements/search/byName/{elementName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllByName(@PathVariable("elementName") String elementName,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService.getElementsByName(elementName, size, page)
                .toArray(new ElementBoundary[0]);
    }

    // GET --> get array of elements matching specific type
    @RequestMapping(path = "/acs/elements/search/byType/{elementType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllByType(@PathVariable("elementType") String elementType,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService.getElementsByType(elementType, size, page)
                .toArray(new ElementBoundary[0]);
    }

    @RequestMapping(path = "/acs/elements/search/byName/{elementName}/byType/{elementType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ElementBoundary[] getAllByNameAndType(@PathVariable("elementName") String elementName,
                                          @PathVariable("elementType") String elementType,
                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                          @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return this.elementService.getElementsByNameAndType(elementName, elementType, size, page)
                .toArray(new ElementBoundary[0]);
    }
}
