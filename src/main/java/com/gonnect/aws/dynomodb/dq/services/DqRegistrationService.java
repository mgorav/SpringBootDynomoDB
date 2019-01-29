package com.gonnect.aws.dynomodb.dq.services;

import com.gonnect.aws.dynomodb.dq.model.DqRegistration;
import com.gonnect.aws.dynomodb.dq.repositories.DqRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * The application service responsible for performing DQ registration
 */
@Configuration
@Slf4j
public class DqRegistrationService {

    private final DqRegistrationRepository repository;


    public DqRegistrationService(DqRegistrationRepository repository) {
        this.repository = repository;
    }

    public Optional<DqRegistration> findByDataSourceName(String dataSourceName) {

        log.trace("Entering findByDataSourceName() with {}", dataSourceName);
        return repository.findByDataSourceName(dataSourceName);
    }

    public Optional<DqRegistration> createDqRegistration(DqRegistration dqRegistration) {

        log.trace("Entering createDqRegistration() with {}", dqRegistration);
        if (repository.findByDataSourceName(dqRegistration.getDataSourceName()).isPresent()) {
            log.warn("DqRegistration {} not found", dqRegistration.getDataSourceName());
            return empty();
        }
        repository.saveDqRegistration(dqRegistration);
        return of(dqRegistration);
    }

    public Optional<DqRegistration> replaceExistingDqRegistration(DqRegistration newDqRegistration) {

        log.trace("Entering replaceExistingDqRegistration() with {}", newDqRegistration);
        Optional<DqRegistration> existingDqRegistration = repository.findByDataSourceName(newDqRegistration.getDataSourceName());
        if (isDqRegistrationNotPresent(existingDqRegistration, newDqRegistration.getDataSourceName())) {
            return empty();
        }
        DqRegistration dqRegistration = existingDqRegistration.get();
        dqRegistration.setDataSourceName(newDqRegistration.getDataSourceName());
        dqRegistration.setStartDateTime(newDqRegistration.getStartDateTime());
        dqRegistration.setEndDateTime(newDqRegistration.getEndDateTime());
        dqRegistration.setSourceCount(newDqRegistration.getSourceCount());
        dqRegistration.setSourceRejectionCount(newDqRegistration.getSourceRejectionCount());

        repository.saveDqRegistration(dqRegistration);
        return of(dqRegistration);
    }

    public Optional<DqRegistration> updateDqRegistration(DqRegistration newDqRegistration) {

        log.trace("Entering updateDqRegistration() with {}", newDqRegistration);
        Optional<DqRegistration> existingDqRegistration = repository.findByDataSourceName(newDqRegistration.getDataSourceName());
        if (isDqRegistrationNotPresent(existingDqRegistration, newDqRegistration.getDataSourceName())) return empty();
        DqRegistration dqRegistration = existingDqRegistration.get();
        if (!isNullOrEmpty(newDqRegistration.getDataSourceName())) {
            dqRegistration.setDataSourceName(newDqRegistration.getDataSourceName());
        }
        if (newDqRegistration.getStartDateTime() != null) {
            dqRegistration.setStartDateTime(newDqRegistration.getStartDateTime());
        }
        if (newDqRegistration.getEndDateTime() != null) {
            dqRegistration.setStartDateTime(newDqRegistration.getEndDateTime());
        }
        if (newDqRegistration.getSourceCount() > -1) {
            dqRegistration.setSourceCount(newDqRegistration.getSourceCount());
        }
        if (newDqRegistration.getSourceRejectionCount() > -1) {
            dqRegistration.setSourceRejectionCount(newDqRegistration.getSourceRejectionCount());
        }
        repository.saveDqRegistration(dqRegistration);
        return of(dqRegistration);
    }

    public boolean delete(String dataSourceName) {

        log.trace("Entering deleteDqRegistration() with {}", dataSourceName);
        if (isDqRegistrationNotPresent(repository.findByDataSourceName(dataSourceName), dataSourceName)) return false;
        repository.deleteDqRegistration(dataSourceName);
        return true;
    }

    public List<DqRegistration> loadAllWithPagination() {

        log.trace("Entering loadAllWithPagination()");
        return repository.loadAllWithPagination();
    }

    // ~Utility methods
    private boolean isDqRegistrationNotPresent(Optional<DqRegistration> existingDqRegistration, String dataSourceName) {
        if (!existingDqRegistration.isPresent()) {
            log.warn("DqRegistration {} not found", dataSourceName);
            return true;
        }
        return false;
    }
}
