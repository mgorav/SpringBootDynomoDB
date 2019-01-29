package com.gonnect.aws.dynomodb.dq.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.gonnect.aws.dynomodb.dq.model.DqRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior.CLOBBER;
import static java.util.Optional.ofNullable;

/**
 * {@link DqRegistration} repository
 */
@Component
@Slf4j
public class DqRegistrationRepository {

    private final DynamoDBMapper dbMapper;

    public DqRegistrationRepository(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    public List<DqRegistration> loadAllWithPagination() {

        log.trace("Entering loadAllWithPagination()");
        PaginatedList<DqRegistration> results = dbMapper.scan(DqRegistration.class, new DynamoDBScanExpression());
        results.loadAllResults();
        return results;
    }

    public Optional<DqRegistration> findByDataSourceName(String dataSourceName) {

        log.trace("Entering findByDataSourceName() with {}", dataSourceName);
        return ofNullable(dbMapper.load(DqRegistration.class, dataSourceName));
    }

    public void saveDqRegistration(DqRegistration dqRegistration) {

        log.trace("Entering saveDqRegistration() with {}", dqRegistration);
        dbMapper.save(dqRegistration);
    }

    public void deleteDqRegistration(String dataSourceName) {

        dbMapper.delete(DqRegistration.builder()
                    .dataSourceName(dataSourceName)
                    .build(),DynamoDBMapperConfig.builder()
                                    .withSaveBehavior(CLOBBER)
                                    .build());
    }

}
