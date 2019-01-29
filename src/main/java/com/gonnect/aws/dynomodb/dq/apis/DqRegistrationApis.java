package com.gonnect.aws.dynomodb.dq.apis;

import com.gonnect.aws.dynomodb.dq.model.DqRegistration;
import com.gonnect.aws.dynomodb.dq.services.DqRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * A REST ful APIs exposed by Data Quality Service for registration of "a" resource. All the registration will be stored
 * in the dynomoDB
 */
@RestController("/dqaas")
@Slf4j
public class DqRegistrationApis {

    private final DqRegistrationService service;

    public DqRegistrationApis(DqRegistrationService service) {
        this.service = service;
    }

    @RequestMapping(path = "/registration", method = GET)
    public ResponseEntity<List<DqRegistration>> list() {

        log.trace("Entering loadAllWithPagination()");
        List<DqRegistration> customers = service.loadAllWithPagination();
        if (customers.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        return new ResponseEntity<>(customers, OK);
    }

    @RequestMapping(path = "/registration/{datasourcename}", method = GET)
    public ResponseEntity<DqRegistration> read(@PathVariable String datasourcename) {

        log.trace("Entering findByDataSourceName() with {}", datasourcename);
        return service.findByDataSourceName(datasourcename)
                .map(customer -> new ResponseEntity<>(customer, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/registration", method = POST)
    public ResponseEntity<DqRegistration> create(@RequestBody @Valid DqRegistration dqRegistration) {

        log.trace("Entering createDqRegistration() with {}", dqRegistration);
        return service.createDqRegistration(dqRegistration)
                .map(newCustomerData -> new ResponseEntity<>(newCustomerData, CREATED))
                .orElse(new ResponseEntity<>(CONFLICT));
    }

    @RequestMapping(path = "/registration/{datasourcename}", method = PUT)
    public ResponseEntity<DqRegistration> put(@PathVariable String datasourcename, @RequestBody DqRegistration dqRegistration) {

        log.trace("Entering put() with {}, {}", datasourcename, dqRegistration);
        dqRegistration.setDataSourceName(datasourcename);
        return service.replaceExistingDqRegistration(dqRegistration)
                .map(newCustomerData -> new ResponseEntity<>(newCustomerData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/registration/{datasourcename}", method = PATCH)
    public ResponseEntity<DqRegistration> patch(@PathVariable String datasourcename, @RequestBody DqRegistration dqRegistration) {

        log.trace("Entering patch() with {}, {}", datasourcename, dqRegistration);
        dqRegistration.setDataSourceName(datasourcename);
        return service.updateDqRegistration(dqRegistration)
                .map(newCustomerData -> new ResponseEntity<>(newCustomerData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/registration/{name}", method = DELETE)
    public ResponseEntity<Void> delete(@PathVariable String datasourcename) {

        log.trace("Entering deleteDqRegistration() with {}", datasourcename);
        return service.delete(datasourcename) ?
                new ResponseEntity<>(NO_CONTENT) :
                new ResponseEntity<>(NOT_FOUND);
    }
}
